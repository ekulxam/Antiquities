package net.hollowed.antique.mixin;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.items.custom.MyriadToolBitItem;
import net.hollowed.antique.networking.WallJumpPacketPayload;
import net.hollowed.antique.util.FastAir;
import net.hollowed.antique.util.MovementUtilsClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class ClimbButNotFasterInsteadActuallyNotClimbAtAllMixin extends Entity implements Attackable {

    @Shadow protected abstract float getMovementSpeed(float slipperiness);

    @Shadow protected boolean jumping;

    @Shadow public abstract boolean isClimbing();

    @Shadow public abstract void setSprinting(boolean sprinting);

    @Shadow protected abstract float getJumpVelocity();

    @Shadow public float sidewaysSpeed;

    public ClimbButNotFasterInsteadActuallyNotClimbAtAllMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    private int jumpingCooldown1;

    @Unique
    private int coyoteJumpCooldown;

    @Unique
    private int coyoteJumpTicks;

    @Unique
    private int coyoteWallJumpTicks;

    @SuppressWarnings("all")
    @Inject(method = "applyMovementInput", at = @At("HEAD"), cancellable = true)
    private void applyMovementInput(Vec3d movementInput, float slipperiness, CallbackInfoReturnable<Vec3d> cir) {
        if ((LivingEntity) (Object) this instanceof PlayerEntity player) {
            this.moveInput = movementInput;

            if (player.getInventory().contains(TagKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "walljumper"))) && this.coyoteWallJumpTicks > 0) {

                this.updateVelocity(this.getMovementSpeed(slipperiness), movementInput);
                this.setVelocity(MovementUtilsClass.applyAxeClimbingSpeed(this.getVelocity(), (LivingEntity) (Object) this));
                this.move(MovementType.SELF, this.getVelocity());
                Vec3d vec3d = this.getVelocity();
                if (this.isClimbing() && player.getInventory().contains(TagKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "walljumper")))) {
                    if (!this.jumping && !this.isSneaking()) vec3d = new Vec3d(vec3d.x, 0, vec3d.z);
                }


                cir.setReturnValue(vec3d);
            }
        }
    }

    @Unique
    private Vec3d moveInput;

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void onJumpWhileClimbing(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (this.isSprinting()) {
            this.sidewaysSpeed *= 1.5F;
        }

        if (this.coyoteJumpTicks > 0) {
            this.coyoteJumpTicks--;
        }

        if (this.isOnGround()) {
            this.coyoteJumpTicks = 4;
            this.coyoteWallJumpTicks = 0;
        }

        if (this.coyoteWallJumpTicks > 0) {
            this.coyoteWallJumpTicks--;
        }

        if (this.isClimbing()) {
            this.coyoteWallJumpTicks = 3;
        }

        if (this.coyoteJumpCooldown > 0) {
            this.coyoteJumpCooldown--;
        }

        if (entity instanceof PlayerEntity player) {
            if (player.getInventory().contains(TagKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "walljumper"))) && jumping && (this.isClimbing() || this.coyoteWallJumpTicks > 0) && this.jumpingCooldown1 == 0) {

                if (player instanceof ClientPlayerEntity) {
                    ClientPlayNetworking.send(new WallJumpPacketPayload(entity.getId()));
                }

                // Create a push vector that moves the player away from the wall
                Vec3d pushVector = new Vec3d(0, 0.8, 0); // Apply horizontal push and vertical boost

                // Determine the direction of the collided wall and add velocity
                Box box = entity.getBoundingBox();
                double offset = 0.35;

                // Check in each cardinal direction for solid blocks
                boolean collidingWest = MovementUtilsClass.collidesWithSolidBlock(entity.getWorld(), box.offset(-offset, 0, 0), entity);
                boolean collidingEast = MovementUtilsClass.collidesWithSolidBlock(entity.getWorld(), box.offset(offset, 0, 0), entity);
                boolean collidingNorth = MovementUtilsClass.collidesWithSolidBlock(entity.getWorld(), box.offset(0, 0, -offset), entity);
                boolean collidingSouth = MovementUtilsClass.collidesWithSolidBlock(entity.getWorld(), box.offset(0, 0, offset), entity);

                boolean ledgeWest = !MovementUtilsClass.collidesWithSolidBlock(entity.getWorld(), box.offset(-offset, 2.1, 0), entity);
                boolean ledgeEast = !MovementUtilsClass.collidesWithSolidBlock(entity.getWorld(), box.offset(offset, 2.1, 0), entity);
                boolean ledgeNorth = !MovementUtilsClass.collidesWithSolidBlock(entity.getWorld(), box.offset(0, 2.1, -offset), entity);
                boolean ledgeSouth = !MovementUtilsClass.collidesWithSolidBlock(entity.getWorld(), box.offset(0, 2.1, offset), entity);

                float correctedYaw = player.getYaw();

                while (correctedYaw > 360) {
                    correctedYaw -= 360;
                }
                while (correctedYaw < 0) {
                    correctedYaw += 360;
                }

                boolean lookingNorth = correctedYaw > 135 && correctedYaw < 225;
                boolean lookingEast = correctedYaw > 225 && correctedYaw < 315;
                boolean lookingSouth = correctedYaw < 45 || correctedYaw > 315;
                boolean lookingWest = correctedYaw > 45 && correctedYaw < 135;

                boolean movingNorth = this.moveInput.getHorizontal().z < 0;
                boolean movingEast = this.moveInput.getHorizontal().x > 0;
                boolean movingSouth = this.moveInput.getHorizontal().z > 0;
                boolean movingWest = this.moveInput.getHorizontal().x < 0;

                // Apply velocity in the opposite direction of the collision
                if (collidingWest) {
                    pushVector = pushVector.add(lookingWest && movingWest ? 0.5 : 0.3, 0, 0); // Push east
                    if (ledgeWest && lookingWest) {
                        pushVector = pushVector.multiply(0, 1, 0).add(this.getVelocity());
                    }
                }
                if (collidingEast) {
                    pushVector = pushVector.add(lookingEast && movingEast && player.isSprinting() ? -0.5 : -0.3, 0, 0); // Push west
                    if (ledgeEast && lookingEast) {
                        pushVector = pushVector.multiply(0, 1, 0).add(this.getVelocity());
                    }
                }
                if (collidingNorth) {
                    pushVector = pushVector.add(0, 0, lookingNorth && movingNorth ? 0.5 : 0.3); // Push south
                    if (ledgeNorth && lookingNorth) {
                        pushVector = pushVector.multiply(0, 1, 0).add(this.getVelocity());
                    }
                }
                if (collidingSouth) {
                    pushVector = pushVector.add(0, 0, lookingSouth && movingSouth ? -0.5 : -0.3); // Push north
                    if (ledgeSouth && lookingSouth) {
                        pushVector = pushVector.multiply(0, 1, 0).add(this.getVelocity());
                    }
                }

                // Apply the velocity to push the player off the wall
                this.setVelocity(entity.getVelocity().add(pushVector));

                if (entity.getActiveItem().getItem() instanceof MyriadToolBitItem item) {
                    if (!entity.isInCreativeMode()) {
                        item.decrementStamina();
                    }
                }

                // Cooldown management to prevent spamming jumps
                this.jumpingCooldown1 = 10;
                entity.velocityDirty = true;
                if (entity instanceof FastAir access) {
                    access.antique$setFast(true);
                }
            }
        }
        if (entity.isOnGround()) {
            this.jumpingCooldown1 = 10;
            if (entity instanceof FastAir access) {
                access.antique$setFast(false);
            }
        }
        if (this.jumpingCooldown1 > 0) {
            this.jumpingCooldown1--;
            if (this.jumpingCooldown1 == 5) {
                if (entity instanceof FastAir access) {
                    access.antique$setFast(false);
                }
            }
        }

        if (this.coyoteJumpTicks > 0 && this.jumping && this.coyoteJumpCooldown == 0 && !this.isOnGround() && (!this.isTouchingWater() || this.isSubmergedInWater())) {
            this.coyoteJumpCooldown = 6;
            this.coyoteJump();
        }
    }

    @Unique
    public void coyoteJump() {
        float f = this.getJumpVelocity() * 1.15F;
        if (!(f <= 1.0E-5F)) {
            Vec3d vec3d = this.getVelocity();
            this.setVelocity(vec3d.x, Math.max(f, vec3d.y), vec3d.z);
            if (this.isSprinting()) {
                float g = this.getYaw() * (float) (Math.PI / 180.0);
                this.addVelocityInternal(new Vec3d((double)(-MathHelper.sin(g)) * 0.2, 0.0, (double)MathHelper.cos(g) * 0.2));
            }

            this.velocityDirty = true;
        }
    }

    @Inject(method = "jump", at = @At("HEAD"))
    public void jumpCooldown(CallbackInfo ci) {
        this.coyoteJumpCooldown = 6;
    }
}
