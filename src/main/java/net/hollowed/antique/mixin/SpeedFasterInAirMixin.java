package net.hollowed.antique.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(PlayerEntity.class)
public abstract class SpeedFasterInAirMixin extends LivingEntity {

    @Shadow @Final private PlayerAbilities abilities;

    protected SpeedFasterInAirMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "getOffGroundSpeed", at = @At("HEAD"), cancellable = true)
    public void getAirSpeed(CallbackInfoReturnable<Float> cir) {
        float speed = 0.025999999F;
        if (this.isSprinting() && !(this.abilities.flying && !this.hasVehicle())) {
            if (this.hasStatusEffect(StatusEffects.SPEED)) {
                speed += Objects.requireNonNull(this.getStatusEffect(StatusEffects.SPEED)).getAmplifier() * 0.03F;
            }
            if (this.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
                speed += Objects.requireNonNull(this.getStatusEffect(StatusEffects.JUMP_BOOST)).getAmplifier() * 0.015F;
            }
            cir.setReturnValue(speed);
        }
    }
}
