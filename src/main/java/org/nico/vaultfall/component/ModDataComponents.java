package org.nico.vaultfall.component;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.nico.vaultfall.Vaultfall;

public class ModDataComponents {
    // Timestamp en ticks del último uso (cooldown absoluto)
    public static final ComponentType<Long> LAST_ACTIVATION_TIMESTAMP =
            Registry.register(Registries.DATA_COMPONENT_TYPE,
                    Identifier.of(Vaultfall.MOD_ID, "last_activation_timestamp"),
                    ComponentType.<Long>builder()
                            .codec(Codec.LONG)
                            .packetCodec(PacketCodecs.VAR_LONG.cast())
                            .build());

    // Timestamp de la última carga del Super Salto (para validar window de 5s)
    public static final ComponentType<Long> SUPER_JUMP_CHARGE_TIMESTAMP =
            Registry.register(Registries.DATA_COMPONENT_TYPE,
                    Identifier.of(Vaultfall.MOD_ID, "super_jump_charge"),
                    ComponentType.<Long>builder()
                            .codec(Codec.LONG)
                            .packetCodec(PacketCodecs.VAR_LONG.cast())
                            .build());

    public static void register() {
        Vaultfall.LOGGER.info("Registrando DataComponents de Vaultfall");
    }
}