package org.nico.vaultfall.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

public class ModEffects {

    // Conversión de HEX #99ffe0 a RGB (escala 0.0 a 1.0 para JOML Vector3f)
    // R: 153/255 = 0.6f | G: 255/255 = 1.0f | B: 224/255 = 0.88f
    private static final Vector3f COLOR_SELETHILITE = new Vector3f(0.6f, 1.0f, 0.88f);

    // Partícula de polvo con nuestro color y un tamaño de 1.2
    private static final DustParticleEffect PARTICULA_SELETHILITE = new DustParticleEffect(COLOR_SELETHILITE, 1.2f);

    private static final StatusEffect ESTATICA_BASE = new StatusEffect(StatusEffectCategory.HARMFUL, 0x99FFE0) {
        @Override
        public boolean canApplyUpdateEffect(int duration, int amplifier) {
            return duration % 2 == 0; // Se ejecuta casi constantemente (cada 2 ticks)
        }

        @Override
        public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
            if (entity.getWorld() instanceof ServerWorld serverWorld) {
                // Genera 5, 10 o 15 partículas dependiendo del número de golpes acumulados
                int cantidad = (amplifier + 1) * 5;
                serverWorld.spawnParticles(PARTICULA_SELETHILITE,
                        entity.getX(), entity.getBodyY(0.5), entity.getZ(),
                        cantidad, 0.4, 0.5, 0.4, 0.1); // Dispersión más amplia
            }
            return true;
        }
    };

    private static final StatusEffect ELECTRIFICADO_BASE = new StatusEffect(StatusEffectCategory.HARMFUL, 0x99FFE0) {
        @Override
        public boolean canApplyUpdateEffect(int duration, int amplifier) {
            return true; // Se ejecuta en CADA tick
        }

        @Override
        public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
            if (entity.getWorld() instanceof ServerWorld serverWorld) {
                // Una lluvia intensa del color selethilita + destellos mágicos
                serverWorld.spawnParticles(PARTICULA_SELETHILITE,
                        entity.getX(), entity.getBodyY(0.5), entity.getZ(),
                        8, 0.5, 0.6, 0.5, 0.05);
                serverWorld.spawnParticles(ParticleTypes.ENCHANTED_HIT,
                        entity.getX(), entity.getBodyY(0.5), entity.getZ(),
                        2, 0.4, 0.4, 0.4, 0.1);
            }
            return true;
        }
    };

    private static final StatusEffect PARALISIS_BASE = new StatusEffect(StatusEffectCategory.HARMFUL, 0x555555) {
        @Override
        public boolean canApplyUpdateEffect(int duration, int amplifier) {
            return duration % 2 == 0;
        }

        @Override
        public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
            if (entity.getWorld() instanceof ServerWorld serverWorld) {
                // Humo blanco brillante saliendo de la cabeza del enemigo (efecto de "cortocircuito")
                serverWorld.spawnParticles(ParticleTypes.WHITE_ASH,
                        entity.getX(), entity.getY() + entity.getStandingEyeHeight(), entity.getZ(),
                        5, 0.2, 0.2, 0.2, 0.01);
            }
            return true;
        }
    }.addAttributeModifier(
            EntityAttributes.GENERIC_MOVEMENT_SPEED,
            Identifier.of("vaultfall", "paralysis_speed_reduction"),
            -1.0,
            EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
    );

    public static RegistryEntry<StatusEffect> ESTATICA_ENTRY;
    public static RegistryEntry<StatusEffect> ELECTRIFICADO_ENTRY;
    public static RegistryEntry<StatusEffect> PARALISIS_ENTRY;

    public static void registerEffects() {
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