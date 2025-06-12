package net.hollowed.antique.mixin;

import net.hollowed.antique.Antiquities;
import net.hollowed.antique.util.ModDamageTypes;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class BreakBlocksIfFastMixin extends Entity {

    public BreakBlocksIfFastMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract Box getBoundingBox(EntityPose pose);

    @Shadow private long lastDamageTime;

    @Shadow public abstract boolean damage(ServerWorld world, DamageSource source, float amount);

    @Shadow public abstract @Nullable LivingEntity getAttacker();

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        LivingEntity user = (LivingEntity) (Object) this;
        World world = user.getWorld();

        this.setNoGravity(user.hasStatusEffect(Antiquities.ANIME_EFFECT));
        if (user.hasStatusEffect(Antiquities.ANIME_EFFECT) && !world.isClient) {
            int radius = (int) (user.getBoundingBox().getLengthX() + 2) / 2 + Objects.requireNonNull(user.getStatusEffect(Antiquities.ANIME_EFFECT)).getAmplifier();

            // First sphere: around the entity
            BlockPos center = user.getBlockPos();
            breakSphere(world, center, radius);
            breakSphere(world, center.up(), radius);

            // Interpolate multiple points between lastPos and currentPos
            Vec3d from = this.lastX != 0 || this.lastY != 0 || this.lastZ != 0 ? new Vec3d(lastX, lastY, lastZ) : user.getPos();
            Vec3d to = user.getPos();
            int steps = (int) (from.distanceTo(to) * 4); // 4 steps per block for decent resolution

            for (int i = 0; i <= steps; i++) {
                double t = (double) i / steps;
                Vec3d point = from.lerp(to, t);
                BlockPos pos = new BlockPos((int) point.x, (int) point.y, (int) point.z);
                breakSphere(world, pos, radius);
                breakSphere(world, pos.up(), radius);
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
                        if (!world.getBlockState(pos).isAir() && world.getBlockState(pos).getBlock().getBlastResistance() < 500) {
                            float distanceToPlayer = 0;
                            for (PlayerEntity player : world.getPlayers()) {
                                distanceToPlayer = player.distanceTo(this);
                            }

                            if (distanceToPlayer < 16) {
                                world.breakBlock(pos, false);
                            } else {
                                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                            }

                            if (world instanceof ServerWorld serverWorld) {
                                if (this.lastDamageTime > 1) {
                                    this.damage(serverWorld, ModDamageTypes.of(world, ModDamageTypes.WALL_SLAM, this.getAttacker()), 8);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
