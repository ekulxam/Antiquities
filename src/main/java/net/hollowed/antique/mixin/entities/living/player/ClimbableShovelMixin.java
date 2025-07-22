package net.hollowed.antique.mixin.entities.living.player;

import net.hollowed.antique.Antiquities;
import net.hollowed.antique.entities.parts.MyriadShovelPart;
import net.hollowed.antique.util.ItemHoldingUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class ClimbableShovelMixin extends LivingEntity {

    @Unique
    private int coyoteTicks;

    @Unique
    private int lastGroundTime;

    @Unique
    private static final TrackedData<Byte> SPIDER_FLAGS = DataTracker.registerData(ClimbableShovelMixin.class, TrackedDataHandlerRegistry.BYTE);

    protected ClimbableShovelMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "isClimbing", at = @At("RETURN"), cancellable = true)
    public void isClimbing(CallbackInfoReturnable<Boolean> cir) {
        if (this.isClimbingWall()) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    public boolean isClimbingWall() {
        return (this.dataTracker.get(SPIDER_FLAGS) & 1) != 0;
    }

    @Unique
    public void setClimbingWall(boolean climbing) {
        byte b = this.dataTracker.get(SPIDER_FLAGS);
        if (climbing) {
            b = (byte) (b | 1);
        } else {
            b = (byte) (b & -2);
        }

        this.dataTracker.set(SPIDER_FLAGS, b);
    }

    @Inject(method = "initDataTracker", at = @At("HEAD"))
    protected void initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(SPIDER_FLAGS, (byte) 0);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (!this.getWorld().isClient) {
            if (this.horizontalCollision && ItemHoldingUtil.isHoldingItem(this, Identifier.of(Antiquities.MOD_ID, "walljumper"))) {
                this.coyoteTicks = 5;
            }
            if (this.isOnGround()) {
                this.lastGroundTime = 5;
            }

            boolean isCollidingWithSpecificEntity = this.getWorld()
                    .getOtherEntities(this, this.getBoundingBox().expand(0.01))
                    .stream()
                    .anyMatch(entity -> entity instanceof MyriadShovelPart);

            if (isCollidingWithSpecificEntity && this.horizontalCollision) {
                this.setClimbingWall(true);
            } else {
                this.setClimbingWall(ItemHoldingUtil.isHoldingItem(this, Identifier.of(Antiquities.MOD_ID, "walljumper")) && (this.horizontalCollision || this.coyoteTicks > 0) && !this.isOnGround() && !isCollidingWithSpecificEntity && this.lastGroundTime == 0 && this.getVelocity().y <= 0);
            }

            if (this.coyoteTicks > 0) {
                this.coyoteTicks--;
            }

            if (this.lastGroundTime > 0) {
                this.lastGroundTime--;
            }
        }
    }
}

