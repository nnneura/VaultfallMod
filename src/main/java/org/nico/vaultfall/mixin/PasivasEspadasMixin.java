package org.nico.vaultfall.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.nico.vaultfall.effect.ModEffects;
import org.nico.vaultfall.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerEntity.class)
public class PasivasEspadasMixin {

    @Unique
    private static final ThreadLocal<Entity> currentMainTarget = ThreadLocal.withInitial(() -> null);
    @Unique
    private static final ThreadLocal<Float> capturedDamage = ThreadLocal.withInitial(() -> null);

    @Unique
    private static final Identifier ARMOR_SHRED_ID = Identifier.of("vaultfall", "armor_shred");
    @Unique
    private static final EntityAttributeModifier ARMOR_SHRED_MODIFIER = new EntityAttributeModifier(
            ARMOR_SHRED_ID, -0.30, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
    );

    @Inject(method = "attack", at = @At("HEAD"))
    private void onAttackStart(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        // Detectamos si tiene CUALQUIERA de las dos espadas custom en mano
        boolean tieneArmaCustom = player.getMainHandStack().isOf(ModItems.ESPADA_MECANICA_MEJORADA)
                || player.getMainHandStack().isOf(ModItems.ESPADA_MECANICA_SELETHILITE); // Ajusta si el nombre varía

        if (tieneArmaCustom && !player.getWorld().isClient()) {
            currentMainTarget.set(target);
        }
    }

    @Redirect(
            method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z")
    )
    private boolean interceptarDanio(Entity instance, DamageSource source, float amount) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        boolean tieneMecanica = player.getMainHandStack().isOf(ModItems.ESPADA_MECANICA_MEJORADA);
        boolean tieneSelethilite = player.getMainHandStack().isOf(ModItems.ESPADA_MECANICA_SELETHILITE);

        if ((!tieneMecanica && !tieneSelethilite) || player.getWorld().isClient()) {
            return instance.damage(source, amount);
        }

        if (instance == currentMainTarget.get() && instance instanceof LivingEntity mainTarget) {

            // PASIVA EXCLUSIVA SELETHILITE: Amplificación por Electrificado (+15%)
            if (tieneSelethilite && mainTarget.hasStatusEffect(ModEffects.ELECTRIFICADO_ENTRY)) {
                amount *= 1.15f;
            }

            // PASIVA COMPARTIDA: Armor Shred (Bypass del 30%)
            EntityAttributeInstance armorAttr = mainTarget.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
            if (armorAttr != null) {
                armorAttr.addTemporaryModifier(ARMOR_SHRED_MODIFIER);
            }

            boolean hit;
            try {
                hit = instance.damage(source, amount);
            } finally {
                if (armorAttr != null) {
                    armorAttr.removeModifier(ARMOR_SHRED_ID);
                }
            }

            if (hit) {
                capturedDamage.set(amount);
            }
            return hit;
        }

        return false;
    }

    @Inject(method = "attack", at = @At("TAIL"))
    private void aplicarVorticeYPasivasSelethilite(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        Float amount = capturedDamage.get();

        currentMainTarget.remove();
        capturedDamage.remove();

        if (amount == null || player.getWorld().isClient()) return;

        boolean tieneMecanica = player.getMainHandStack().isOf(ModItems.ESPADA_MECANICA_MEJORADA);
        boolean tieneSelethilite = player.getMainHandStack().isOf(ModItems.ESPADA_MECANICA_SELETHILITE);

        if (!tieneMecanica && !tieneSelethilite) return;
        if (!(target instanceof LivingEntity mainTarget)) return;

        ServerWorld world = (ServerWorld) player.getWorld();

        // PASIVA EXCLUSIVA SELETHILITE: Aplicar/Avanzar estática al objetivo principal
        if (tieneSelethilite) {
            aplicarOAvanzarEstatica(mainTarget, world);
        }

        // Efectos del súper barrido visual pesado (Pitch 0.15f)
        world.spawnParticles(ParticleTypes.SWEEP_ATTACK, mainTarget.getX(), mainTarget.getY() + 0.5, mainTarget.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.0f, 0.15f);

        // BÚSQUEDA DE ENEMIGOS SECUNDARIOS (Vórtice de succión)
        Box areaAtaque = mainTarget.getBoundingBox().expand(3.0, 1.0, 3.0);
        List<LivingEntity> enemigosCercanos = world.getEntitiesByClass(LivingEntity.class, areaAtaque,
                entity -> entity != player && entity != mainTarget && !player.isTeammate(entity));

        // ---------------------------------------------------------
        // INTEGRACIÓN DE FILO ARRASADOR (SWEEPING EDGE) EN 1.21.1
        // ---------------------------------------------------------
        // ratioBarrido será 0.0 (Sin encantar) hasta 0.75 (Nivel III)
        float ratioBarrido = (float) player.getAttributeValue(EntityAttributes.PLAYER_SWEEPING_DAMAGE_RATIO);

        // Base: 70% para Selethilite, 100% para Mecánica
        float multiplicadorBase = tieneSelethilite ? 0.70f : 1.0f;

        // Sumamos el beneficio del encantamiento.
        // Multiplicado por 0.4 para balancearlo: con Nivel 3 (0.75 * 0.4 = 0.30)
        // La Selethilite sube de 0.70 a 1.00 (recupera el daño total).
        // La Mecánica sube de 1.00 a 1.30 (se vuelve una locura para limpiar hordas).
        float multiplicadorFinal = multiplicadorBase + (ratioBarrido * 0.4f);

        float baseDamageSecundario = amount * multiplicadorFinal;

        for (LivingEntity enemigo : enemigosCercanos) {
            float danioFinalSecundario = baseDamageSecundario;

            // Multiplicador extra de Selethilite en área si ya estaban electrificados
            if (tieneSelethilite && enemigo.hasStatusEffect(ModEffects.ELECTRIFICADO_ENTRY)) {
                danioFinalSecundario *= 1.15f;
            }

            EntityAttributeInstance armorSec = enemigo.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
            if (armorSec != null) {
                armorSec.addTemporaryModifier(ARMOR_SHRED_MODIFIER);
            }

            boolean hitSec;
            try {
                hitSec = enemigo.damage(world.getDamageSources().playerAttack(player), danioFinalSecundario);
            } finally {
                if (armorSec != null) {
                    armorSec.removeModifier(ARMOR_SHRED_ID);
                }
            }

            if (hitSec) {
                world.sendEntityStatus(enemigo, (byte) 2); // Forzar flash rojo de daño

                // PASIVA COMPARTIDA: Succión limpia hacia el frente del jugador
                Vec3d centro = player.getPos().add(player.getRotationVector().multiply(1.5));
                Vec3d tiron = centro.subtract(enemigo.getPos());
                if (tiron.length() > 0.5) {
                    tiron = tiron.normalize().multiply(0.8);
                    enemigo.setVelocity(tiron.x, 0.1, tiron.z);
                    enemigo.velocityModified = true;
                }

                // PASIVA EXCLUSIVA SELETHILITE: Cargar estática en cadena a los mobs del barrido
                if (tieneSelethilite) {
                    aplicarOAvanzarEstatica(enemigo, world);
                }
            }
        }
    }

    @Unique
    private void aplicarOAvanzarEstatica(LivingEntity target, ServerWorld world) {
        if (target.hasStatusEffect(ModEffects.ELECTRIFICADO_ENTRY)) return;

        StatusEffectInstance estaticaActual = target.getStatusEffect(ModEffects.ESTATICA_ENTRY);
        int stackActual = estaticaActual != null ? estaticaActual.getAmplifier() : -1;

        if (stackActual < 2) {
            target.addStatusEffect(new StatusEffectInstance(
                    ModEffects.ESTATICA_ENTRY, 60, stackActual + 1, false, false, false
            ));
        } else {
            target.removeStatusEffect(ModEffects.ESTATICA_ENTRY);

            world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, target.getX(), target.getBodyY(0.5), target.getZ(), 15, 0.2, 0.2, 0.2, 0.1);
            world.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.PLAYERS, 0.7f, 2.0f);

            target.addStatusEffect(new StatusEffectInstance(
                    ModEffects.ELECTRIFICADO_ENTRY, 100, 0, false, false, true
            ));

            if (target instanceof PlayerEntity jugadorEnemigo) {
                jugadorEnemigo.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 10, 0, false, false, false));
                jugadorEnemigo.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20, 2, false, false, true));
            } else {
                target.addStatusEffect(new StatusEffectInstance(
                        ModEffects.PARALISIS_ENTRY, 30, 0, false, false, false
                ));
            }
        }
    }
}
