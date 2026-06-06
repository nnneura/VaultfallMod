package org.nico.vaultfall.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.nico.vaultfall.Vaultfall;

public record JumpFeedbackPayload(
        byte feedbackType,
        double x,
        double y,
        double z
) implements CustomPayload {

    public static final byte TYPE_SUPER_JUMP_READY = 1;
    public static final byte TYPE_DOUBLE_JUMP = 2;
    public static final byte TYPE_SUPER_JUMP = 3;

    public static final CustomPayload.Id<JumpFeedbackPayload> ID =
            new CustomPayload.Id<>(Identifier.of(Vaultfall.MOD_ID, "jump_feedback"));

    public static final PacketCodec<RegistryByteBuf, JumpFeedbackPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BYTE, JumpFeedbackPayload::feedbackType,
            PacketCodecs.DOUBLE, JumpFeedbackPayload::x,
            PacketCodecs.DOUBLE, JumpFeedbackPayload::y,
            PacketCodecs.DOUBLE, JumpFeedbackPayload::z,
            JumpFeedbackPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}