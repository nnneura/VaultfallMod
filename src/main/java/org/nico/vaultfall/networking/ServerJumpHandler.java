package org.nico.vaultfall.networking;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import org.nico.vaultfall.component.ModDataComponents;
import org.nico.vaultfall.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ServerJumpHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger("VaultfallDebug");

    private static final long DOUBLE_JUMP_COOLDOWN = 20L;
    private static final long SUPER_JUMP_WINDOW    = 60L;
    private static final long MIN_CHARGE_TIME      = 60L;

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(JumpModulePayload.ID, ServerJumpHandler::handle);
        LOGGER.info("[SERVIDOR] ServerJumpHandler registrado");
    }

    private static void handle(JumpModulePayload payload, ServerPlayNetworking.Context ctx) {
        ServerPlayerEntity player = ctx.player();
        byte flags = payload.flags();
        long now = player.getWorld().getTime();

        LOGGER.debug("[SERVIDOR] Paquete recibido: flags={}", flags);

        try {
            if ((flags & JumpModulePayload.FLAG_START_CHARGE) != 0) {
                updateModuleInBoots(player, ModItems.MODULO_SUPER_SALTO, stack ->
                        stack.set(ModDataComponents.SUPER_JUMP_CHARGE_TIMESTAMP, now)
                );
                return;
            }

            if ((flags & JumpModulePayload.FLAG_RELEASE_CHARGE) != 0) {
                updateModuleInBoots(player, ModItems.MODULO_SUPER_SALTO, stack -> {
                    Long startTs = stack.get(ModDataComponents.SUPER_JUMP_CHARGE_TIMESTAMP);
                    if (startTs != null && now - startTs >= MIN_CHARGE_TIME) {
                        stack.set(ModDataComponents.SUPER_JUMP_CHARGE_TIMESTAMP, now);
                        LOGGER.info("[SERVIDOR] Carga válida, ventana de 3s activada");
                    } else {
                        stack.set(ModDataComponents.SUPER_JUMP_CHARGE_TIMESTAMP, 0L);
                    }
                });
                return;
            }

            if ((flags & JumpModulePayload.FLAG_DOUBLE_JUMP) != 0) {
                updateModuleInBoots(player, ModItems.MODULO_DOBLE_SALTO, stack -> {
                    Long last = stack.get(ModDataComponents.LAST_ACTIVATION_TIMESTAMP);
                    long lastTime = last != null ? last : 0L;
                    if (now - lastTime < DOUBLE_JUMP_COOLDOWN) {
                        LOGGER.debug("[SERVIDOR] Double Jump en cooldown");
                        return;
                    }

                    stack.set(ModDataComponents.LAST_ACTIVATION_TIMESTAMP, now);
                    executeDoubleJump(player);
                    LOGGER.info("[SERVIDOR] Double Jump ejecutado");
                });
                return;
            }

            if ((flags & JumpModulePayload.FLAG_SUPER_JUMP) != 0) {
                updateModuleInBoots(player, ModItems.MODULO_SUPER_SALTO, stack -> {
                    Long releaseTs = stack.get(ModDataComponents.SUPER_JUMP_CHARGE_TIMESTAMP);
                    if (releaseTs == null || releaseTs == 0L || now - releaseTs > SUPER_JUMP_WINDOW) {
                        LOGGER.debug("[SERVIDOR] Super Jump fuera de ventana");
                        return;
                    }

                    stack.set(ModDataComponents.SUPER_JUMP_CHARGE_TIMESTAMP, 0L);
                    executeSuperJump(player);
                    LOGGER.info("[SERVIDOR] Super Jump ejecutado");
                });
            }
        } catch (Exception e) {
            LOGGER.error("[SERVIDOR] ERROR en handle(): {}", e.getMessage(), e);
        }
    }

    private static void executeDoubleJump(ServerPlayerEntity player) {
        Vec3d currentVel = player.getVelocity();
        boolean isSprinting = player.isSprinting();

        // CORREGIDO: Impulso vertical más alto (0.7 vs 0.42 del salto vanilla)
        double jumpImpulse = 0.7;

        // CORREGIDO: Normalizar velocidad Y antes de aplicar impulso
        // Esto garantiza que el salto tenga la misma altura independientemente
        // de si el jugador está subiendo o cayendo cuando lo activa
        double baseVertical = Math.max(currentVel.y, 0.0); // Si está cayendo, empezar desde 0

        // CORREGIDO: Preservar mejor el momentum horizontal
        // 1.15 = 15% más de velocidad horizontal (vs 1.05 anterior)
        // Si está sprintando, añadir un boost adicional
        double horizontalMultiplier = isSprinting ? 1.25 : 1.15;

        Vec3d newVel = new Vec3d(
                currentVel.x * horizontalMultiplier,
                baseVertical + jumpImpulse,
                currentVel.z * horizontalMultiplier
        );

        player.setVelocity(newVel);
        player.velocityModified = true;
        player.fallDistance = 0.0f;

        sendFeedbackToClient(player, JumpFeedbackPayload.TYPE_DOUBLE_JUMP);

        // Efectos visuales más abundantes para reforzar la sensación de potencia
        spawnServerParticles(player, ParticleTypes.CLOUD, 40);
        playServerSound(player, SoundEvents.BLOCK_PISTON_CONTRACT, 0.8f, 1.0f);

        LOGGER.debug("[SERVIDOR] Doble salto ejecutado - VelY final: {}, Sprint: {}", newVel.y, isSprinting);
    }

    private static void executeSuperJump(ServerPlayerEntity player) {
        Vec3d look = player.getRotationVector();
        Vec3d currentVel = player.getVelocity();

        Vec3d boost = new Vec3d(
                look.x * 0.8,
                1.5,
                look.z * 0.8
        );

        Vec3d newVel = currentVel.add(boost);
        player.setVelocity(newVel);
        player.velocityModified = true;
        player.fallDistance = 0.0f;

        sendFeedbackToClient(player, JumpFeedbackPayload.TYPE_SUPER_JUMP);

        spawnServerParticles(player, ParticleTypes.LARGE_SMOKE, 35);
        spawnServerParticles(player, ParticleTypes.FLAME, 20);
        playServerSound(player, SoundEvents.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1.0f, 0.6f);
    }

    private static void sendFeedbackToClient(ServerPlayerEntity player, byte feedbackType) {
        Vec3d pos = player.getPos();
        JumpFeedbackPayload payload = new JumpFeedbackPayload(
                feedbackType,
                pos.x,
                pos.y,
                pos.z
        );
        ServerPlayNetworking.send(player, payload);
    }

    private static void spawnServerParticles(ServerPlayerEntity player, ParticleEffect particleEffect, int count) {
        ServerWorld world = player.getServerWorld();
        Vec3d pos = player.getPos();

        for (int i = 0; i < count; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 0.8;
            double offsetZ = (world.random.nextDouble() - 0.5) * 0.8;
            double offsetY = world.random.nextDouble() * 0.5;

            world.spawnParticles(
                    particleEffect,
                    pos.x + offsetX,
                    pos.y + 0.5 + offsetY,
                    pos.z + offsetZ,
                    1,
                    offsetX * 0.1,
                    -0.1,
                    offsetZ * 0.1,
                    0.0
            );
        }
    }

    private static void playServerSound(ServerPlayerEntity player, SoundEvent sound, float volume, float pitch) {
        ServerWorld world = player.getServerWorld();

        world.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                sound,
                SoundCategory.PLAYERS,
                volume,
                pitch
        );
    }

    private static void updateModuleInBoots(ServerPlayerEntity player, Item targetModule, Consumer<ItemStack> modifier) {
        Optional<TrinketComponent> optional = TrinketsApi.getTrinketComponent(player);
        if (optional.isEmpty()) return;

        for (var pair : optional.get().getAllEquipped()) {
            ItemStack boots = pair.getRight();

            if (!boots.isEmpty() && boots.isOf(ModItems.EXO_BOTAS) && boots.contains(DataComponentTypes.CONTAINER)) {
                SlotReference slotRef = pair.getLeft();
                ContainerComponent container = boots.get(DataComponentTypes.CONTAINER);
                if (container == null) continue;

                List<ItemStack> stacks = container.stream().collect(Collectors.toList());
                boolean modified = false;

                for (int i = 0; i < stacks.size(); i++) {
                    ItemStack moduleStack = stacks.get(i);
                    if (moduleStack.isOf(targetModule)) {
                        modifier.accept(moduleStack);
                        stacks.set(i, moduleStack);
                        modified = true;
                        break;
                    }
                }

                if (modified) {
                    ContainerComponent newContainer = ContainerComponent.fromStacks(stacks);
                    boots.set(DataComponentTypes.CONTAINER, newContainer);
                    slotRef.inventory().setStack(slotRef.index(), boots);
                }
                return;
            }
        }
    }
}