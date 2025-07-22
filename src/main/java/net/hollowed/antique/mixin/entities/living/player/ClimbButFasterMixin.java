package net.hollowed.antique.mixin.entities.living.player;

import net.hollowed.antique.entities.parts.MyriadShovelPart;
import net.minecraft.block.Blocks;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.entity.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class ClimbButFasterMixin extends Entity implements Attackable {

    @Shadow protected abstract Vec3d applyClimbingSpeed(Vec3d motion);

    @Shadow protected abstract float getMovementSpeed(float slipperiness);

    @Shadow protected boolean jumping;

    @Shadow public abstract boolean isClimbing();

    public ClimbButFasterMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "applyMovementInput", at = @At("HEAD"), cancellable = true)
    private void applyMovementInput(Vec3d movementInput, float slipperiness, CallbackInfoReturnable<Vec3d> cir) {
        boolean isCollidingWithSpecificEntity = this.getWorld()
                .getOtherEntities(this, this.getBoundingBox().expand(0.1))
                .stream()
                .anyMatch(entity -> entity instanceof MyriadShovelPart);

        if (isCollidingWithSpecificEntity && this.horizontalCollision) {

            this.updateVelocity(this.getMovementSpeed(slipperiness), movementInput);
            this.setVelocity(this.applyClimbingSpeed(this.getVelocity()));
            this.move(MovementType.SELF, this.getVelocity());
            Vec3d vec3d = this.getVelocity();
            if ((this.horizontalCollision || this.jumping)
                    && (this.isClimbing() || this.getBlockStateAtPos().isOf(Blocks.POWDER_SNOW) && PowderSnowBlock.canWalkOnPowderSnow(this))) {
                vec3d = new Vec3d(vec3d.x, 0.4, vec3d.z);
            }

            cir.setReturnValue(vec3d);
        }
    }
}
