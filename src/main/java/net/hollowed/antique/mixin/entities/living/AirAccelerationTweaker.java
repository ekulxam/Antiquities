package net.hollowed.antique.mixin.entities.living;

import net.hollowed.antique.index.AntiqueEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class AirAccelerationTweaker extends Entity {

    @Shadow public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);

    @Shadow public abstract boolean damage(ServerWorld world, DamageSource source, float amount);

    @Shadow public abstract boolean isUsingRiptide();

    @Shadow
    public abstract @Nullable StatusEffectInstance getStatusEffect(RegistryEntry<StatusEffect> effect);

    public AirAccelerationTweaker(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "travelMidAir", at = @At("HEAD"))
    private void boostHorizontalAcceleration(Vec3d movementInput, CallbackInfo ci) {
        Entity entity = this;

        if ((LivingEntity) (Object) this instanceof PlayerEntity player && !entity.isOnGround() && player.getGlidingTicks() == 0 && !(player.getAbilities().flying || player.isSpectator())) {
            double horizontalBoost = 0.01;
            if (this.isSprinting()) horizontalBoost = 0.015;
            if (this.isUsingRiptide()) horizontalBoost = 0.125;
            if (this.hasStatusEffect(AntiqueEffects.BOUNCE_EFFECT)) horizontalBoost = 0.15;
            double maxHorizontalSpeed = 0.55;
            if (this.isSprinting()) maxHorizontalSpeed = 0.95;

            Vec3d newVelocity = getVec3d(movementInput, entity, horizontalBoost);

            double horizontalSpeed = Math.sqrt(newVelocity.x * newVelocity.x + newVelocity.z * newVelocity.z);
            if (horizontalSpeed > maxHorizontalSpeed) {
                double scale = maxHorizontalSpeed / horizontalSpeed;
                newVelocity = new Vec3d(newVelocity.x * scale, newVelocity.y, newVelocity.z * scale);
            }

            if (this.hasStatusEffect(AntiqueEffects.BOUNCE_EFFECT)) {
                this.setVelocity(newVelocity);
            } else if (movementInput.length() > 0.1) {
                this.setVelocity(newVelocity);
            }
        }
    }

    @Unique
    private Vec3d getVec3d(Vec3d movementInput, Entity entity, double horizontalBoost) {
        float yaw = entity.getYaw();
        double yawRad = Math.toRadians(yaw);

        double globalX = movementInput.x * Math.cos(yawRad) - movementInput.z * Math.sin(yawRad);
        double globalZ = movementInput.x * Math.sin(yawRad) + movementInput.z * Math.cos(yawRad);

        Vec3d boostedInput = new Vec3d(globalX * horizontalBoost, 0, globalZ * horizontalBoost);

        Vec3d currentVelocity = this.getVelocity();
        return currentVelocity.add(boostedInput);
    }

    @Inject(method = "travelInFluid", at = @At("HEAD"))
    private void boostHorizontalAccelerationInWater(Vec3d movementInput, CallbackInfo ci) {
        Entity entity = this;

        if ((LivingEntity) (Object) this instanceof PlayerEntity player && !entity.isOnGround() && player.getGlidingTicks() == 0 && !(player.getAbilities().flying || player.isSpectator())) {
            double horizontalBoost = 0.01;
            if (this.isSprinting()) horizontalBoost = 0.011;
            if (this.isUsingRiptide()) horizontalBoost = 0.125;
            if (this.hasStatusEffect(AntiqueEffects.BOUNCE_EFFECT)) horizontalBoost = 0.15;
            double maxHorizontalSpeed = 0.75;
            if (this.isSprinting()) maxHorizontalSpeed = 1.15;

            Vec3d newVelocity = getVec3d(movementInput, entity, horizontalBoost);

            double horizontalSpeed = Math.sqrt(newVelocity.x * newVelocity.x + newVelocity.z * newVelocity.z);
            if (horizontalSpeed > maxHorizontalSpeed) {
                double scale = maxHorizontalSpeed / horizontalSpeed;
                newVelocity = new Vec3d(newVelocity.x * scale, newVelocity.y, newVelocity.z * scale);
            }

            if (this.hasStatusEffect(AntiqueEffects.BOUNCE_EFFECT)) {
                this.setVelocity(newVelocity);
            } else if (movementInput.length() > 0.1) {
                this.setVelocity(newVelocity);
            }
        }
    }

    @Inject(method = "getJumpBoostVelocityModifier", at = @At("HEAD"), cancellable = true)
    public void jumpModifier(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(this.hasStatusEffect(StatusEffects.JUMP_BOOST) ? 0.3F * (Objects.requireNonNull(this.getStatusEffect(StatusEffects.JUMP_BOOST)).getAmplifier() + 1.0F) : 0.0F);
    }
}
