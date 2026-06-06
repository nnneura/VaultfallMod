package org.nico.vaultfall.client;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import org.nico.vaultfall.item.ModItems;
import org.nico.vaultfall.networking.JumpFeedbackPayload;
import org.nico.vaultfall.networking.JumpModulePayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ClientJumpTracker {

    private static final Logger LOGGER = LoggerFactory.getLogger("VaultfallDebug");

    private static final long SUPER_JUMP_COOLDOWN = 60L;
    private static final int INPUT_DEBOUNCE_TICKS = 3; // Reducido a 3 ticks, el filtro real ahora es la física

    private static boolean wasSneaking = false;
    private static boolean wasOnGround = false;
    private static boolean doubleJumpUsed = false;
    private static boolean superJumpReady = false;
    private static boolean feedbackShown = false;
    private static long sneakStartTime = 0L;
    private static long releaseTime = 0L;
    private static long lastSuperJumpExecution = 0L;

    private static boolean wasJumpPressed = false;
    private static int lastJumpInputTick = -100;
    private static int tickCounter = 0;

    public static void register() {
        LOGGER.info("[CLIENTE] ClientJumpTracker.register() ejecutado");
        ClientTickEvents.END_CLIENT_TICK.register(ClientJumpTracker::onTick);
    }

    public static void onSuperJumpExecuted(long worldTime) {
        lastSuperJumpExecution = worldTime;
    }

    private static boolean clientHasModuleEquipped(ClientPlayerEntity player, Item targetModule) {
        Optional<TrinketComponent> optional = TrinketsApi.getTrinketComponent(player);
        if (optional.isEmpty()) return false;

        for (var pair : optional.get().getAllEquipped()) {
            ItemStack boots = pair.getRight();

            if (!boots.isEmpty() && boots.isOf(ModItems.EXO_BOTAS) && boots.contains(DataComponentTypes.CONTAINER)) {
                ContainerComponent container = boots.get(DataComponentTypes.CONTAINER);
                if (container == null) continue;

                boolean found = container.stream().anyMatch(s -> s.isOf(targetModule));
                if (found) return true;
            }
        }
        return false;
    }

    private static void onTick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null) return;

        tickCounter++;

        boolean sneaking = player.isSneaking();
        boolean onGround = player.isOnGround();
        long now = player.getWorld().getTime();
        Vec3d velocity = player.getVelocity();

        // --- LÓGICA DE CARGA (SUPER SALTO) ---
        if (sneaking && !wasSneaking) {
            sneakStartTime = now;
            superJumpReady = false;
            feedbackShown = false;
            ClientPlayNetworking.send(new JumpModulePayload(JumpModulePayload.FLAG_START_CHARGE, now));
        } else if (!sneaking && wasSneaking) {
            if (now - sneakStartTime >= 60L) {
                superJumpReady = true;
                releaseTime = now;
                ClientPlayNetworking.send(new JumpModulePayload(JumpModulePayload.FLAG_RELEASE_CHARGE, now));
            }
            sneakStartTime = 0L;
            feedbackShown = false;
        }

        if (sneaking && sneakStartTime > 0 && !feedbackShown) {
            long chargeTime = now - sneakStartTime;
            if (chargeTime >= 60L && !isOnCooldown(now) && clientHasModuleEquipped(player, ModItems.MODULO_SUPER_SALTO)) {
                superJumpReady = true;
                feedbackShown = true;
                releaseTime = now;
                triggerLocalFeedback(client, JumpFeedbackPayload.TYPE_SUPER_JUMP_READY);
            }
        }

        if (superJumpReady && now - releaseTime > 60L) {
            superJumpReady = false;
            feedbackShown = false;
        }

        wasSneaking = sneaking;

        // --- DETECCIÓN DE SALTO ---
        boolean jumpPressed = client.options.jumpKey.isPressed();
        boolean jumpJustPressed = jumpPressed && !wasJumpPressed;
        boolean jumpInputValid = jumpJustPressed && (tickCounter - lastJumpInputTick >= INPUT_DEBOUNCE_TICKS);

        if (jumpInputValid) {
            lastJumpInputTick = tickCounter;
        }

        wasJumpPressed = jumpPressed;

        // Resetear doble salto al tocar el suelo
        if (onGround && !wasOnGround) {
            doubleJumpUsed = false;
        }
        wasOnGround = onGround;

        // --- LÓGICA DE SALTOS EN EL AIRE ---
        if (jumpInputValid && !onGround && !doubleJumpUsed) {

            // 1. El Super Salto puede ejecutarse en cualquier momento del aire (incluso subiendo)
            if (superJumpReady && !isOnCooldown(now)) {
                LOGGER.warn(">>> Enviando FLAG_SUPER_JUMP");
                ClientPlayNetworking.send(new JumpModulePayload(JumpModulePayload.FLAG_SUPER_JUMP, 0L));
                superJumpReady = false;
                feedbackShown = false;
                lastSuperJumpExecution = now;
                doubleJumpUsed = true;
            }
            // 2. El Doble Salto SOLO se ejecuta si el jugador está cayendo o en el pico del salto (VelY < 0.05)
            // Esto previene que intercepte el primer salto mientras el jugador sigue subiendo.
            else if (velocity.y < 0.05) {
                LOGGER.warn(">>> Enviando FLAG_DOUBLE_JUMP (VelY: {})", velocity.y);
                ClientPlayNetworking.send(new JumpModulePayload(JumpModulePayload.FLAG_DOUBLE_JUMP, 0L));
                doubleJumpUsed = true;
            } else {
                LOGGER.debug(">>> Doble salto ignorado: Jugador sigue ascendiendo (VelY: {})", velocity.y);
            }
        }
    }

    private static boolean isOnCooldown(long now) {
        if (lastSuperJumpExecution == 0L) return false;
        long timeSinceExecution = now - lastSuperJumpExecution;
        return timeSinceExecution < SUPER_JUMP_COOLDOWN;
    }

    private static void triggerLocalFeedback(MinecraftClient client, byte feedbackType) {
        if (client.player == null || client.world == null) return;

        Vec3d pos = client.player.getPos();

        if (feedbackType == JumpFeedbackPayload.TYPE_SUPER_JUMP_READY) {
            client.world.playSound(
                    pos.x, pos.y, pos.z,
                    SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE,
                    SoundCategory.PLAYERS,
                    0.8f, 2.2f,
                    false
            );
            spawnLocalChargeParticles(client, pos);
        }
    }

    private static void spawnLocalChargeParticles(MinecraftClient client, Vec3d pos) {
        for (int i = 0; i < 25; i++) {
            double angle = (Math.PI * 2 * i) / 25;
            double offsetX = Math.cos(angle) * 0.8;
            double offsetZ = Math.sin(angle) * 0.8;

            client.world.addParticle(
                    ParticleTypes.ELECTRIC_SPARK,
                    pos.x + offsetX,
                    pos.y + 0.1,
                    pos.z + offsetZ,
                    offsetX * 0.05,
                    0.3,
                    offsetZ * 0.05
            );
        }
    }
}