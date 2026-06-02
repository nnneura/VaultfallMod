package org.nico.vaultfall.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModEffects {

    // Instancias base de los efectos
    private static final StatusEffect ESTATICA_BASE = new StatusEffect(StatusEffectCategory.HARMFUL, 0xFFD700) {};
    private static final StatusEffect ELECTRIFICADO_BASE = new StatusEffect(StatusEffectCategory.HARMFUL, 0x00FFFF) {};

    // El efecto PARALISIS incluye un modificador de atributo nativo que resta el 100% de la velocidad de movimiento
    private static final StatusEffect PARALISIS_BASE = new StatusEffect(StatusEffectCategory.HARMFUL, 0x555555) {}
            .addAttributeModifier(
                    EntityAttributes.GENERIC_MOVEMENT_SPEED,
                    Identifier.of("vaultfall", "paralysis_speed_reduction"),
                    -1.0, // Multiplica la velocidad total por -1.0 (la reduce a 0 por completo)
                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            );

    // Contenedores RegistryEntry requeridos obligatoriamente por el motor de Minecraft 1.21+
    public static RegistryEntry<StatusEffect> ESTATICA_ENTRY;
    public static RegistryEntry<StatusEffect> ELECTRIFICADO_ENTRY;
    public static RegistryEntry<StatusEffect> PARALISIS_ENTRY;

    public static void registerEffects() {
        // Registramos en el juego y asignamos la referencia directa sin Optional
        ESTATICA_ENTRY = Registries.STATUS_EFFECT.getEntry(
                Registry.register(Registries.STATUS_EFFECT, Identifier.of("vaultfall", "estatica"), ESTATICA_BASE)
        );

        ELECTRIFICADO_ENTRY = Registries.STATUS_EFFECT.getEntry(
                Registry.register(Registries.STATUS_EFFECT, Identifier.of("vaultfall", "electrificado"), ELECTRIFICADO_BASE)
        );

        PARALISIS_ENTRY = Registries.STATUS_EFFECT.getEntry(
                Registry.register(Registries.STATUS_EFFECT, Identifier.of("vaultfall", "paralisis"), PARALISIS_BASE)
        );
    }
}