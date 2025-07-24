package net.hollowed.antique.mixin.entities.living.player;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.index.AntiqueEffects;
import net.hollowed.antique.index.AntiqueItems;
import net.hollowed.antique.networking.WallJumpPacketPayload;
import net.hollowed.antique.util.interfaces.duck.FastAir;
import net.hollowed.antique.util.ItemHoldingUtil;
import net.hollowed.antique.util.MovementUtilsClass;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class WallJumpMixin extends Entity implements Attackable {

    @Unique
    private static final TrackedData<Vector3f> MOVEMENT_INPUT = DataTracker.registerData(WallJumpMixin.class, TrackedDataHandlerRegistry.VECTOR_3F);

    @Shadow protected abstract float getMovementSpeed(float slipperiness);

    @Shadow protected boolean jumping;

    @Shadow public abstract boolean isClimbing();

    @Shadow public abstract void setSprinting(boolean sprinting);

    @Shadow protected abstract float getJumpVelocity();

    @Shadow public float sidewaysSpeed;

    public WallJumpMixin(EntityType<?> type, World world) {
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

    @Unique
    private boolean ran = false;

    @Unique
    private Vec3d playerVelocity = new Vec3d(0, 0, 0);

    @Unique
    private Vec3d startTickPosition = new Vec3d(0, 0, 0);

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    public void damage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (this.playerVelocity.length() > 4 && source.isOf(DamageTypes.FALL) && !ran) {
            LivingEntity self = (LivingEntity) (Object) this;

            float reducedAmount = amount * 0.5F;

            this.ran = true;
            self.damage(world, source, reducedAmount);
            cir.setReturnValue(false);
        } else {
            this.ran = false;
        }
    }

    @SuppressWarnings("all")
    @Inject(method = "applyMovementInput", at = @At("HEAD"), cancellable = true)
    private void applyMovementInput(Vec3d movementInput, float slipperiness, CallbackInfoReturnable<Vec3d> cir) {
        if ((LivingEntity) (Object) this instanceof PlayerEntity player) {
            this.dataTracker.set(MOVEMENT_INPUT, new Vector3f((float) movementInput.x, (float) movementInput.y, (float) movementInput.z));

            if (ItemHoldingUtil.isHoldingItem(player, Identifier.of(Antiquities.MOD_ID, "walljumper")) && this.coyoteWallJumpTicks > 0) {

                this.updateVelocity(this.getMovementSpeed(slipperiness), movementInput);
                this.setVelocity(MovementUtilsClass.applyAxeClimbingSpeed(this.getVelocity(), (LivingEntity) (Object) this));
                this.move(MovementType.SELF, this.getVelocity());
                Vec3d vec3d = this.getVelocity();
                if (this.isClimbing() && ItemHoldingUtil.isHoldingItem(player, Identifier.of(Antiquities.MOD_ID, "walljumper"))) {
                    if (!this.jumping && !this.isSneaking()) vec3d = new Vec3d(vec3d.x, 0, vec3d.z);
                }

                cir.setReturnValue(vec3d);
            }
        }
    }

    @Inject(method = "initDataTracker", at = @At("HEAD"))
    protected void initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(MOVEMENT_INPUT, new Vector3f());
    }

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void onJumpWhileClimbing(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (startTickPosition == null) {
            startTickPosition = entity.getPos();
        }

        Vec3d endTickPosition = entity.getPos();

        if (!startTickPosition.equals(endTickPosition)) {
            playerVelocity = endTickPosition.subtract(startTickPosition);
        }

        startTickPosition = endTickPosition;

        if (!entity.isOnGround() && entity.isSneaking() && entity.getVelocity().y < 0 && entity.fallDistance > 15 && entity.getVelocity().length() < 10 && entity instanceof PlayerEntity player && !player.getAbilities().flying) {
            if ((!player.hasStatusEffect(AntiqueEffects.BOUNCE_EFFECT) && !player.hasStatusEffect(AntiqueEffects.VOLATILE_BOUNCE_EFFECT)) || player.isCreative()) {
                if (entity.isHolding(AntiqueItems.MYRIAD_AXE_HEAD) && !this.isClimbing()) {
                    entity.addVelocity(new Vec3d(0, -0.25, 0));
                } else if (!entity.isHolding(AntiqueItems.MYRIAD_AXE_HEAD)) {
                    entity.addVelocity(new Vec3d(0, -0.25, 0));
                }
            }
        }

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
            if (ItemHoldingUtil.isHoldingItem(player, Identifier.of(Antiquities.MOD_ID, "walljumper")) && jumping && (this.isClimbing() || this.coyoteWallJumpTicks > 0) && this.jumpingCooldown1 == 0) {

                if (player instanceof ClientPlayerEntity) {
                    ClientPlayNetworking.send(new WallJumpPacketPayload(entity.getId()));
                }

                Vec3d pushVector = new Vec3d(0, 0.8, 0);

                Box box = entity.getBoundingBox();
                double offset = 0.35;

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

                Vec3d moveInput = new Vec3d(this.dataTracker.get(MOVEMENT_INPUT));

                boolean movingForward = moveInput.getHorizontal().z > 0;

                if (collidingWest) {
                    pushVector = pushVector.add(lookingWest && movingForward ? 0.5 : 0.3, 0, 0); // Push east
                    if (ledgeWest && lookingWest) {
                        pushVector = pushVector.multiply(0, 1, 0).add(this.getVelocity());
                    }
                }
                if (collidingEast) {
                    pushVector = pushVector.add(lookingEast && movingForward ? -0.5 : -0.3, 0, 0); // Push west
                    if (ledgeEast && lookingEast) {
                        pushVector = pushVector.multiply(0, 1, 0).add(this.getVelocity());
                    }
                }
                if (collidingNorth) {
                    pushVector = pushVector.add(0, 0, lookingNorth && movingForward ? 0.5 : 0.3); // Push south
                    if (ledgeNorth && lookingNorth) {
                        pushVector = pushVector.multiply(0, 1, 0).add(this.getVelocity());
                    }
                }
                if (collidingSouth) {
                    pushVector = pushVector.add(0, 0, lookingSouth && movingForward ? -0.5 : -0.3); // Push north
                    if (ledgeSouth && lookingSouth) {
                        pushVector = pushVector.multiply(0, 1, 0).add(this.getVelocity());
                    }
                }

                this.setVelocity(entity.getVelocity().add(pushVector));
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

    @Inject(method = "canGlide", at = @At("HEAD"), cancellable = true)
    protected void canGlide(CallbackInfoReturnable<Boolean> cir) {
        if (this.coyoteWallJumpTicks > 0 || this.coyoteJumpTicks > 0) {
            cir.setReturnValue(false);
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
