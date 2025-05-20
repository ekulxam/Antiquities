package net.hollowed.antique.mixin;

import net.hollowed.antique.Antiquities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class BreakBlocksIfFastMixin extends Entity {

    public BreakBlocksIfFastMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract Box getBoundingBox(EntityPose pose);

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        LivingEntity user = (LivingEntity) (Object) this;
        World world = user.getWorld();

        this.setNoGravity(user.hasStatusEffect(Antiquities.ANIME_EFFECT));
        if (user.hasStatusEffect(Antiquities.ANIME_EFFECT)) {
            int radius = 2;

            // First sphere: around the entity
            BlockPos center = user.getBlockPos();
            breakSphere(world, center, radius);

            // Second sphere: in front of the entity
            Vec3d velocity = user.getVelocity();
            if (velocity.lengthSquared() > 0.01) {
                Vec3d forwardCenter = user.getPos().add(velocity.normalize().multiply(2));
                BlockPos forwardBlockPos = new BlockPos((int) forwardCenter.x, (int) forwardCenter.y, (int) forwardCenter.z);
                breakSphere(world, forwardBlockPos, radius);
            }
        }
    }

    @Unique
    private void breakSphere(World world, BlockPos center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);
                    if (distance <= radius) {
                        BlockPos pos = center.add(x, y, z);
                        if (!world.getBlockState(pos).isAir()) {
                            world.breakBlock(pos, false);
                        }
                    }
                }
            }
        }
    }
}
