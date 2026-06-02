package org.nico.vaultfall.mixin;

import org.nico.vaultfall.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerEntity.class)
public class EspadaMecanicaMejoradaMixin {

    // INYECCIÓN 1: Lógica principal (Vórtice, Daño, Succión y Penetración de Armadura)
    @Redirect(
            method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", ordinal = 0),
            require = 1
    )
    private boolean interceptarAtaquePrincipal(Entity instance, DamageSource source, float amount) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        World world = player.getWorld();

        boolean usaEspadaCustom = player.getMainHandStack().isOf(ModItems.ESPADA_MECANICA_MEJORADA);
        boolean espadaCargada = player.getAttackCooldownProgress(0.5f) > 0.9f;

        // Validamos que sea la espada, esté cargada y golpee a un ser vivo
        if (usaEspadaCustom && espadaCargada && instance instanceof LivingEntity mainTarget) {

            // 1. ARMOR SHRED (Seguro contra fallos)
            EntityAttributeInstance armorAttr = mainTarget.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
            double armaduraOriginal = 0;
            boolean tieneArmadura = armorAttr != null;

            if (tieneArmadura) {
                armaduraOriginal = armorAttr.getBaseValue();
                armorAttr.setBaseValue(armaduraOriginal * 0.70); // Bypass 30%
            }

            boolean golpeAcertado = false;
            try {
                // Aplicamos el golpe nativo
                golpeAcertado = instance.damage(source, amount);
            } finally {
                // Restauramos la armadura OBLIGATORIAMENTE, incluso si el daño falla
                if (tieneArmadura) {
                    armorAttr.setBaseValue(armaduraOriginal);
                }
            }

            // 2. EFECTOS VISUALES Y VÓRTICE (Solo si el golpe conectó)
            if (golpeAcertado) {

                // Sonido sincronizado
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0f, 0.4f);

                // Cálculo exacto de ángulo para las partículas de viento vanilla
                double d = (double)(-MathHelper.sin(player.getYaw() * 0.017453292F));
                double e = (double)MathHelper.cos(player.getYaw() * 0.017453292F);

                if (world instanceof ServerWorld serverWorld) {
                    // Spawneo en servidor (visible para otros jugadores)
                    serverWorld.spawnParticles(ParticleTypes.SWEEP_ATTACK, player.getX() + d, player.getBodyY(0.5), player.getZ() + e, 0, d, 0.0, e, 0.0);
                } else {
                    // Spawneo predictivo en tu pantalla (evita el lag visual)
                    world.addParticle(ParticleTypes.SWEEP_ATTACK, player.getX() + d, player.getBodyY(0.5), player.getZ() + e, d, 0.0, e);
                }

                // VÓRTICE Y SÚPER BARRIDO
                Box areaAtaque = player.getBoundingBox().expand(3.0, 1.0, 3.0);
                List<LivingEntity> enemigosCercanos = world.getNonSpectatingEntities(LivingEntity.class, areaAtaque);

                for (LivingEntity enemigo : enemigosCercanos) {
                    // Filtramos: No atacarte a ti mismo, ni al objetivo que ya golpeamos, ni aliados
                    if (enemigo != player && enemigo != mainTarget && !player.isTeammate(enemigo) && player.squaredDistanceTo(enemigo) < 16.0) {

                        // Succión (Vacuum)
                        Vec3d direccionTiron = player.getPos().subtract(enemigo.getPos()).normalize();
                        enemigo.addVelocity(direccionTiron.x * 0.5, 0.2, direccionTiron.z * 0.5);
                        enemigo.velocityModified = true;

                        // Armor Shred para los atrapados en el barrido
                        EntityAttributeInstance armorSec = enemigo.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
                        double armaduraOriginalSec = 0;
                        boolean tieneArmaduraSec = armorSec != null;

                        if (tieneArmaduraSec) {
                            armaduraOriginalSec = armorSec.getBaseValue();
                            armorSec.setBaseValue(armaduraOriginalSec * 0.70);
                        }

                        try {
                            // Daño total al grupo
                            enemigo.damage(source, amount);
                        } finally {
                            if (tieneArmaduraSec) {
                                armorSec.setBaseValue(armaduraOriginalSec);
                            }
                        }
                    }
                }
            }
            return golpeAcertado;
        }

        // Comportamiento normal si usas cualquier otra arma
        return instance.damage(source, amount);
    }

    // INYECCIÓN 2: Cancelamos el daño secundario (débil) del barrido vanilla
    @Redirect(
            method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"),
            require = 1
    )
    private boolean cancelarBarridoVanilla(LivingEntity instance, DamageSource source, float amount) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getMainHandStack().isOf(ModItems.ESPADA_MECANICA_MEJORADA)) {
            return false;
        }
        return instance.damage(source, amount);
    }

    // INYECCIÓN 3: Apagamos el generador de partículas de barrido vanilla (Limpio, sin bucles)
    @Inject(method = "spawnSweepAttackParticles", at = @At("HEAD"), cancellable = true)
    private void cancelarParticulasVanilla(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getMainHandStack().isOf(ModItems.ESPADA_MECANICA_MEJORADA)) {
            ci.cancel();
        }
    }

    // INYECCIÓN 4: Apagamos el sonido original del barrido
    @Redirect(
            method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"),
            require = 1
    )
    private void cancelarSonidoBarridoVanilla(World world, PlayerEntity player, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        if (sound == SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP && player.getMainHandStack().isOf(ModItems.ESPADA_MECANICA_MEJORADA)) {
            return;
        }
        world.playSound(player, x, y, z, sound, category, volume, pitch);
    }
}