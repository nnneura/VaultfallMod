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
public class EspadaSelethiliteMixin{

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
        if (player.getMainHandStack().isOf(ModItems.ESPADA_MECANICA_SELETHILITE) && !player.getWorld().isClient()) {
            currentMainTarget.set(target);
        }
    }

    @Redirect(
            method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z")
    )
    private boolean interceptarDanio(Entity instance, DamageSource source, float amount) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if (!player.getMainHandStack().isOf(ModItems.ESPADA_MECANICA_SELETHILITE) || player.getWorld().isClient()) {
            return instance.damage(source, amount);
        }

        if (instance == currentMainTarget.get() && instance instanceof LivingEntity mainTarget) {

            // PASIVA NUEVA: Si ya está ELECTRIFICADO, recibe 15% más de daño
            if (mainTarget.hasStatusEffect(ModEffects.ELECTRIFICADO_ENTRY)) {
                amount *= 1.15f;
            }

            // PASIVA ANTIGUA: 30% Bypass de armadura
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

        if (amount == null || player.getWorld().isClient() || !player.getMainHandStack().isOf(ModItems.ESPADA_MECANICA_SELETHILITE)) {
            return;
        }

        if (!(target instanceof LivingEntity mainTarget)) return;

        ServerWorld world = (ServerWorld) player.getWorld();

        // 1. PROCESAR PASIVA ELÉCTRICA EN EL OBJETIVO PRINCIPAL
        aplicarOAvanzarEstatica(mainTarget, world);

        // Efectos del súper barrido visual pesado
        world.spawnParticles(ParticleTypes.SWEEP_ATTACK, mainTarget.getX(), mainTarget.getY() + 0.5, mainTarget.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.0f, 0.15f);

        // 2. BÚSQUEDA DE ENEMIGOS SECUNDARIOS (Vórtice de succión)
        Box areaAtaque = mainTarget.getBoundingBox().expand(3.0, 1.0, 3.0);
        List<LivingEntity> enemigosCercanos = world.getEntitiesByClass(LivingEntity.class, areaAtaque,
                entity -> entity != player && entity != mainTarget && !player.isTeammate(entity));

        // AJUSTE DE BALANCE: El barrido secundario hace un 70% del daño del arma original
        float baseDamageSecundario = amount * 0.70f;

        for (LivingEntity enemigo : enemigosCercanos) {
            float danioFinalSecundario = baseDamageSecundario;

            // Multiplicador del 15% si el enemigo secundario ya estaba electrificado
            if (enemigo.hasStatusEffect(ModEffects.ELECTRIFICADO_ENTRY)) {
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
                world.sendEntityStatus(enemigo, (byte) 2); // Flash rojo de daño en el cliente

                // PASIVA ANTIGUA: Succión hacia el frente del jugador
                Vec3d centro = player.getPos().add(player.getRotationVector().multiply(1.5));
                Vec3d tiron = centro.subtract(enemigo.getPos());
                if (tiron.length() > 0.5) {
                    tiron = tiron.normalize().multiply(0.8);
                    enemigo.setVelocity(tiron.x, 0.1, tiron.z);
                    enemigo.velocityModified = true;
                }

                // PASIVA NUEVA: Los enemigos del barrido TAMBIÉN cargan estática (¡Detonaciones en cadena!)
                aplicarOAvanzarEstatica(enemigo, world);
            }
        }
    }

    // LÓGICA DE CONTROL DE STACKS Y DETONACIÓN ELÉCTRICA DE SELETHILITE
    @Unique
    private void aplicarOAvanzarEstatica(LivingEntity target, ServerWorld world) {
        // Ventana de inmunidad: Si ya está sufriendo el debuff, no puede acumular más estática
        if (target.hasStatusEffect(ModEffects.ELECTRIFICADO_ENTRY)) {
            return;
        }

        StatusEffectInstance estaticaActual = target.getStatusEffect(ModEffects.ESTATICA_ENTRY);
        int stackActual = estaticaActual != null ? estaticaActual.getAmplifier() : -1;

        if (stackActual < 2) {
            // No ha llegado al límite. Añade un stack (Amplifier: 0 = Golpe 1, 1 = Golpe 2, 2 = Golpe 3)
            // Se configura completamente invisible (false, false, false) para no ensuciar la pantalla
            target.addStatusEffect(new StatusEffectInstance(
                    ModEffects.ESTATICA_ENTRY, 60, stackActual + 1, false, false, false
            ));
        } else {
            // ¡CUARTO GOLPE DETECTADO! DETONACIÓN DE SOBRECARGA
            target.removeStatusEffect(ModEffects.ESTATICA_ENTRY);

            // Audio y Partículas impactantes de chispas de cortocircuito
            world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, target.getX(), target.getBodyY(0.5), target.getZ(), 15, 0.2, 0.2, 0.2, 0.1);
            world.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.PLAYERS, 0.7f, 2.0f);

            // Aplicamos "Electrificado" por 5 segundos (100 ticks). Mostramos icono para alertar de su vulnerabilidad.
            target.addStatusEffect(new StatusEffectInstance(
                    ModEffects.ELECTRIFICADO_ENTRY, 100, 0, false, false, true
            ));

            if (target instanceof PlayerEntity jugadorEnemigo) {
                // BALANCE EN PVP: Shock nervioso (Ceguera total de 0.5s y Lentitud Extrema III de 1s)
                jugadorEnemigo.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 10, 0, false, false, false));
                jugadorEnemigo.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20, 2, false, false, true));
            } else {
                // BALANCE EN PVE: Parálisis completa (Inmovilizado en el sitio por 1 segundo)
                target.addStatusEffect(new StatusEffectInstance(
                        ModEffects.PARALISIS_ENTRY, 20, 0, false, false, false
                ));
            }
        }
    }
}