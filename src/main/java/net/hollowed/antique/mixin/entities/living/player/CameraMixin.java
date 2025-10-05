package net.hollowed.antique.mixin.entities.living.player;

import net.hollowed.antique.index.AntiqueItems;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow private float pitch;
    @Shadow private float yaw;

    @Shadow @Final private static Vector3f HORIZONTAL;
    @Shadow @Final private static Vector3f VERTICAL;
    @Shadow @Final private static Vector3f DIAGONAL;
    @Shadow @Final private Quaternionf rotation;
    @Shadow @Final private Vector3f horizontalPlane;
    @Shadow @Final private Vector3f verticalPlane;
    @Shadow @Final private Vector3f diagonalPlane;

    @Unique
    private float roll = 0.0F;

    @Inject(method = "update", at = @At("TAIL"))
    public void update(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (focusedEntity instanceof PlayerEntity player) {
            Box box = player.getBoundingBox();
            double offset = 0.1;

            boolean collidingWest = collidesWithSolidBlock(player.getEntityWorld(), box.offset(-offset, 0, 0), player);
            boolean collidingEast = collidesWithSolidBlock(player.getEntityWorld(), box.offset(offset, 0, 0), player);
            boolean collidingNorth = collidesWithSolidBlock(player.getEntityWorld(), box.offset(0, 0, -offset), player);
            boolean collidingSouth = collidesWithSolidBlock(player.getEntityWorld(), box.offset(0, 0, offset), player);

            float correctedYaw = this.yaw;

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

            float targetRoll = 0.0F;

            if (player.isClimbing() && (this.pitch < 30 && this.pitch > -30) && !thirdPerson && !player.isOnGround() && player.isHolding(AntiqueItems.MYRIAD_AXE_HEAD)) {
                if (collidingNorth) {
                    if (lookingEast) {
                        targetRoll = -15;
                    } else if (lookingWest) {
                        targetRoll = 15;
                    } else {
                        targetRoll = 0;
                    }
                } else if (collidingEast) {
                    if (lookingSouth) {
                        targetRoll = -15;
                    } else if (lookingNorth) {
                        targetRoll = 15;
                    } else {
                        targetRoll = 0;
                    }
                } else if (collidingSouth) {
                    if (lookingWest) {
                        targetRoll = -15;
                    } else if (lookingEast) {
                        targetRoll = 15;
                    } else {
                        targetRoll = 0;
                    }
                } else if (collidingWest) {
                    if (lookingNorth) {
                        targetRoll = -15;
                    } else if (lookingSouth) {
                        targetRoll = 15;
                    } else {
                        targetRoll = 0;
                    }
                }
            }

            this.roll += (targetRoll - this.roll) * 0.05F;
            this.setRotationWithRoll(this.yaw, this.pitch, this.roll);
        }
    }

    @Unique
    private void setRotationWithRoll(float yaw, float pitch, float roll) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
        this.rotation.rotationYXZ((float) Math.PI - yaw * (float) (Math.PI / 180.0), -pitch * (float) (Math.PI / 180.0), roll * (float) (Math.PI / 180.0));
        HORIZONTAL.rotate(this.rotation, this.horizontalPlane);
        VERTICAL.rotate(this.rotation, this.verticalPlane);
        DIAGONAL.rotate(this.rotation, this.diagonalPlane);
    }

    @Unique
    private boolean collidesWithSolidBlock(World world, Box box, Entity entity) {
        return world.getBlockCollisions(entity, box).iterator().hasNext();
    }
}