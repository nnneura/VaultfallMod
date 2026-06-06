package org.nico.vaultfall.networking;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class ModNetworking {

    // Se llama desde ModInitializer (Servidor/Common)
    public static void registerC2SPayloads() {
        PayloadTypeRegistry.playC2S().register(JumpModulePayload.ID, JumpModulePayload.CODEC);
    }

    // Se llama desde ClientModInitializer (Cliente)
    public static void registerS2CPayloads() {
        PayloadTypeRegistry.playS2C().register(JumpFeedbackPayload.ID, JumpFeedbackPayload.CODEC);
    }
}