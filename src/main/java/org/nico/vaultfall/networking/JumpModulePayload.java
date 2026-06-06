package org.nico.vaultfall.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.nico.vaultfall.Vaultfall;

public record JumpModulePayload(byte flags, long timestamp) implements CustomPayload {

    public static final byte FLAG_DOUBLE_JUMP   = 1;
    public static final byte FLAG_SUPER_JUMP    = 2;
    public static final byte FLAG_START_CHARGE  = 4;
    public static final byte FLAG_RELEASE_CHARGE = 8;

    public static final CustomPayload.Id<JumpModulePayload> ID =
            new CustomPayload.Id<>(Identifier.of(Vaultfall.MOD_ID, "jump_module"));

    public static final PacketCodec<RegistryByteBuf, JumpModulePayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BYTE, JumpModulePayload::flags,
            PacketCodecs.VAR_LONG, JumpModulePayload::timestamp,
            JumpModulePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}