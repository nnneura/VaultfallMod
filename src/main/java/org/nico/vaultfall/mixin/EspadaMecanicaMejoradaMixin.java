package org.nico.vaultfall.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.nico.vaultfall.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerEntity.class)
public class EspadaMecanicaMejoradaMixin {

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
        if (player.getMainHandStack().isOf(ModItems.ESPADA_MECANICA_MEJORADA) && !player.getWorld().isClient()) {
            currentMainTarget.set(target);
        }
    }

    @Redirect(
            method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z")
    )
    private boolean interceptarDanio(Entity instance, DamageSource source, float amount) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if (!player.getMainHandStack().isOf(ModItems.ESPADA_MECANICA_MEJORADA) || player.getWorld().isClient()) {
            return instance.damage(source, amount);
        }

        if (instance == currentMainTarget.get() && instance instanceof LivingEntity mainTarget) {
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
    private void aplicarVorticeYLimpiar(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        Float amount = capturedDamage.get();

        currentMainTarget.remove();
        capturedDamage.remove();

        if (amount == null || player.getWorld().isClient() || !player.getMainHandStack().isOf(ModItems.ESPADA_MECANICA_MEJORADA)) {
            return;
        }

        if (!(target instanceof LivingEntity mainTarget)) return;

        ServerWorld world = (ServerWorld) player.getWorld();

        // Efectos visuales y sonoros (Pitch reducido a 0.15f para dar sensación de arma pesada)
        world.spawnParticles(ParticleTypes.SWEEP_ATTACK,
                mainTarget.getX(), mainTarget.getY() + 0.5, mainTarget.getZ(),
                1, 0.0, 0.0, 0.0, 0.0);
        world.playSound(null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS,
                1.0f, 0.15f);

        // Búsqueda de entidades secundarias
        Box areaAtaque = mainTarget.getBoundingBox().expand(3.0, 1.0, 3.0);
        List<LivingEntity> enemigosCercanos = world.getEntitiesByClass(LivingEntity.class, areaAtaque,
                entity -> entity != player && entity != mainTarget && !player.isTeammate(entity));

        for (LivingEntity enemigo : enemigosCercanos) {
            EntityAttributeInstance armorSec = enemigo.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
            if (armorSec != null) {
                armorSec.addTemporaryModifier(ARMOR_SHRED_MODIFIER);
            }

            boolean hit;
            try {
                hit = enemigo.damage(world.getDamageSources().playerAttack(player), amount);
            } finally {
                if (armorSec != null) {
                    armorSec.removeModifier(ARMOR_SHRED_ID);
                }
            }

            if (hit) {
                // Forzar animación de daño y partículas de corazones en el cliente (Status ID 2 = HURT)
                world.sendEntityStatus(enemigo, (byte) 2);

                // VACUUM: Succión limpia sin interferencias de vanilla
                Vec3d centro = player.getPos().add(player.getRotationVector().multiply(1.5));
                Vec3d tiron = centro.subtract(enemigo.getPos());

                if (tiron.length() > 0.5) {
                    tiron = tiron.normalize().multiply(0.8);
                    enemigo.setVelocity(tiron.x, 0.1, tiron.z);
                    enemigo.velocityModified = true;
                }
            }
        }
    }
}