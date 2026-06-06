package org.nico.vaultfall.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import org.nico.vaultfall.networking.JumpFeedbackPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class ClientJumpFeedbackHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger("VaultfallDebug");

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(JumpFeedbackPayload.ID, ClientJumpFeedbackHandler::handle);
        LOGGER.info("[CLIENTE] JumpFeedbackHandler registrado");
    }

    private static void handle(JumpFeedbackPayload payload, ClientPlayNetworking.Context ctx) {
        MinecraftClient client = ctx.client();
        if (client == null || client.world == null || client.player == null) return;

        client.execute(() -> {
            Vec3d pos = new Vec3d(payload.x(), payload.y(), payload.z());
            byte type = payload.feedbackType();
            long worldTime = client.player.getWorld().getTime();

            switch (type) {
                case JumpFeedbackPayload.TYPE_DOUBLE_JUMP -> {
                    client.world.playSound(
                            pos.x, pos.y, pos.z,
                            SoundEvents.BLOCK_PISTON_CONTRACT,
                            SoundCategory.PLAYERS,
                            0.7f, 1.2f,
                            false
                    );
                    spawnDoubleJumpParticles(client, pos);
                    LOGGER.debug("[CLIENTE] Feedback DOUBLE_JUMP reproducido");
                }

                case JumpFeedbackPayload.TYPE_SUPER_JUMP -> {
                    client.world.playSound(
                            pos.x, pos.y, pos.z,
                            SoundEvents.ENTITY_FIREWORK_ROCKET_LARGE_BLAST,
                            SoundCategory.PLAYERS,
                            1.0f, 0.6f,
                            false
                    );
                    spawnSuperJumpParticles(client, pos);
                    LOGGER.debug("[CLIENTE] Feedback SUPER_JUMP reproducido");

                    ClientJumpTracker.onSuperJumpExecuted(worldTime);
                }
            }
        });
    }

    // ESTÉTICA: Solo partículas CLOUD (eliminado SCRAPE)
    private static void spawnDoubleJumpParticles(MinecraftClient client, Vec3d pos) {
        for (int i = 0; i < 35; i++) {
            double offsetX = (client.world.random.nextDouble() - 0.5) * 0.8;
            double offsetZ = (client.world.random.nextDouble() - 0.5) * 0.8;
            double offsetY = -client.world.random.nextDouble() * 0.5;

            client.world.addParticle(
                    ParticleTypes.CLOUD,
                    pos.x + offsetX,
                    pos.y + 0.1,
                    pos.z + offsetZ,
                    offsetX * 0.1,
                    offsetY * 0.3,
                    offsetZ * 0.1
            );
        }
    }

    private static void spawnSuperJumpParticles(MinecraftClient client, Vec3d pos) {
        for (int i = 0; i < 40; i++) {
            double angle = (Math.PI * 2 * i) / 40;
            double speed = 0.4 + client.world.random.nextDouble() * 0.4;
            double offsetX = Math.cos(angle) * speed;
            double offsetZ = Math.sin(angle) * speed;

            client.world.addParticle(
                    ParticleTypes.LARGE_SMOKE,
                    pos.x,
                    pos.y + 0.1,
                    pos.z,
                    offsetX,
                    -0.2,
                    offsetZ
            );
        }

        for (int i = 0; i < 20; i++) {
            double offsetX = (client.world.random.nextDouble() - 0.5) * 0.6;
            double offsetZ = (client.world.random.nextDouble() - 0.5) * 0.6;

            client.world.addParticle(
                    ParticleTypes.FLAME,
                    pos.x + offsetX,
                    pos.y + 0.2,
                    pos.z + offsetZ,
                    offsetX * 0.1,
                    0.4,
                    offsetZ * 0.1
            );
        }
    }
}