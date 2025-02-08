package net.hollowed.antique.mixin;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import it.unimi.dsi.fastutil.floats.FloatArraySet;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatSet;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.util.FastAir;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

@Mixin(Entity.class)
public abstract class EntityBounceMixin implements FastAir {

    @Shadow
    public abstract Vec3d getVelocity();

    @Shadow
    public abstract void setVelocity(double x, double y, double z);

    @Shadow
    public abstract void setOnGround(boolean onGround);

    @Shadow public abstract Box getBoundingBox();

    @Shadow public abstract World getWorld();

    @Shadow public abstract float getStepHeight();

    @Shadow public abstract boolean isOnGround();

    @Shadow private World world;

    @Shadow public abstract boolean isSpectator();

    @Shadow public abstract boolean isTouchingWater();

    @Shadow public abstract boolean isSubmergedInWater();

    @Unique
    private boolean isFastAir;

    @Shadow private Vec3d pos;
    @Unique
    private int tickCounter = 0; // Counter to track ticks

    @Unique
    private Vec3d lastTickPosition; // To store the entity's position at the end of the last tick

    public void antique$setFast(boolean bool) {
        this.isFastAir = bool;
    }

    public boolean antique$getFast() {
        return this.isFastAir;
    }

    /**
     * Capture the entity's position at the start of each tick (head inject).
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void captureStartPosition(CallbackInfo ci) {
        // Update lastTickPosition before the tick modifies entity position
        if (lastTickPosition == null) {
            lastTickPosition = this.pos; // Initialize to current position on first tick
        }
    }

    /**
     * Apply the bounce effect and calculate the simulated velocity (tail inject).
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void applyBounceEffect(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity livingEntity) {
            Vec3d velocity = this.getVelocity();
            Vec3d adjustedVec = this.adjustMovementForCollisions(velocity);

            boolean X = !MathHelper.approximatelyEquals(velocity.x, adjustedVec.x);
            boolean Z = !MathHelper.approximatelyEquals(velocity.z, adjustedVec.z);
            boolean Y = !MathHelper.approximatelyEquals(velocity.y, adjustedVec.y);

            if (livingEntity.hasStatusEffect(Antiquities.BOUNCE_EFFECT) || livingEntity.hasStatusEffect(Antiquities.VOLATILE_BOUNCE_EFFECT) || this.isFastAir && !this.isSpectator()) {
                if ((Object) this instanceof PlayerEntity player && player.isGliding()) {
                    return;
                }

                double airResistanceCounter = 1.05;

                if (!this.isOnGround()) {
                    // Reduce air resistance
                    this.setVelocity(velocity.x * airResistanceCounter, velocity.y, velocity.z * airResistanceCounter); // Slightly reduce resistance
                }

                if (this.isFastAir) {
                    return;
                }

                // Bounce logic
                if (X) {
                    this.setOnGround(false);
                    this.setVelocity(-velocity.x, velocity.y, velocity.z);
                }
                if (Z) {
                    this.setOnGround(false);
                    this.setVelocity(velocity.x, velocity.y, -velocity.z);
                }

                if (Y && velocity.length() > 0.4) {
                    this.setOnGround(false);
                    this.setVelocity(velocity.x, -velocity.y * 0.8 + 0.125, velocity.z);
                }
                if (this.isTouchingWater() && !this.isSubmergedInWater()) {
                    this.setOnGround(false);
                    this.setVelocity(velocity.x, -velocity.y * 0.99, velocity.z);
                }

                // Increment the tick counter and spawn particles every 10 ticks
                tickCounter++;

                // Calculate simulated velocity using last tick position
                Vec3d simulatedVelocity = this.pos.subtract(lastTickPosition);

                if (tickCounter >= 5 && simulatedVelocity.length() > 0.4) {
                    tickCounter = 0; // Reset counter
                    spawnParticleRing(entity, simulatedVelocity);
                } else if (tickCounter >= 10 && simulatedVelocity.length() > 0.2) {
                    tickCounter = 0; // Reset counter
                    spawnParticleRing(entity, simulatedVelocity);
                }
            }
        }

        // Update lastTickPosition for use in the next tick
        lastTickPosition = this.pos;
    }

    @Unique
    private void spawnParticleRing(Entity entity, Vec3d velocity) {
        World world = entity.getWorld();
        Vec3d entityPos = entity.getPos();
        Vec3d normalizedVelocity = velocity.normalize();

        int particleCount = 32; // Number of particles in the ring
        double radius = 0.75; // Radius of the particle ring

        // Define the white dust particle effect with a size of 1
        ParticleEffect whiteDustParticle = new DustParticleEffect(0xFFFFFF, 1.0f);

        // Find a perpendicular vector to the velocity to act as the "up" vector for the ring's plane
        Vec3d upVector = Math.abs(normalizedVelocity.y) < 0.999f ? new Vec3d(0, 1, 0) : new Vec3d(1, 0, 0);
        Vec3d axis1 = normalizedVelocity.crossProduct(upVector).normalize(); // Perpendicular to velocity
        Vec3d axis2 = normalizedVelocity.crossProduct(axis1).normalize(); // Perpendicular to both velocity and axis1

        for (int i = 0; i < particleCount; i++) {
            double angle = (2 * Math.PI / particleCount) * i;
            double xOffset = Math.cos(angle) * radius;
            double zOffset = Math.sin(angle) * radius;

            // Position the particle along the ring, rotated to align with velocity
            Vec3d particleOffset = axis1.multiply(xOffset).add(axis2.multiply(zOffset));
            Vec3d particlePos = entityPos.add(particleOffset);

            // Spawn the particle at the entity's position
            if (world instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(whiteDustParticle, particlePos.x, particlePos.y + 1, particlePos.z, 1, 0, 0, 0, 0);
            }
        }
    }

    @Unique
    private Vec3d adjustMovementForCollisions(Vec3d movement) {
        Box box = this.getBoundingBox();
        List<VoxelShape> list = this.getWorld().getEntityCollisions((Entity) (Object) this, box.stretch(movement));
        Vec3d vec3d = movement.lengthSquared() == 0.0 ? movement : adjustMovementForCollisions((Entity) (Object) this, movement, box, this.getWorld(), list);
        boolean bl = movement.x != vec3d.x;
        boolean bl2 = movement.y != vec3d.y;
        boolean bl3 = movement.z != vec3d.z;
        boolean bl4 = bl2 && movement.y < 0.0;
        if (this.getStepHeight() > 0.0F && (bl4 || this.isOnGround()) && (bl || bl3)) {
            Box box2 = bl4 ? box.offset(0.0, vec3d.y, 0.0) : box;
            Box box3 = box2.stretch(movement.x, (double)this.getStepHeight(), movement.z);
            if (!bl4) {
                box3 = box3.stretch(0.0, -9.999999747378752E-6, 0.0);
            }

            List<VoxelShape> list2 = findCollisionsForMovement((Entity) (Object) this, this.world, list, box3);
            float f = (float)vec3d.y;
            float[] fs = collectStepHeights(box2, list2, this.getStepHeight(), f);
            float[] var14 = fs;
            int var15 = fs.length;

            for(int var16 = 0; var16 < var15; ++var16) {
                float g = var14[var16];
                Vec3d vec3d2 = adjustMovementForCollisions(new Vec3d(movement.x, (double)g, movement.z), box2, list2);
                if (vec3d2.horizontalLengthSquared() > vec3d.horizontalLengthSquared()) {
                    double d = box.minY - box2.minY;
                    return vec3d2.add(0.0, -d, 0.0);
                }
            }
        }

        return vec3d;
    }

    @Unique
    private Vec3d adjustMovementForCollisions(@Nullable Entity entity, Vec3d movement, Box entityBoundingBox, World world, List<VoxelShape> collisions) {
        List<VoxelShape> list = findCollisionsForMovement(entity, world, collisions, entityBoundingBox.stretch(movement));
        return adjustMovementForCollisions(movement, entityBoundingBox, list);
    }

    @Unique
    private static Vec3d adjustMovementForCollisions(Vec3d movement, Box entityBoundingBox, List<VoxelShape> collisions) {
        if (collisions.isEmpty()) {
            return movement;
        } else {
            double d = movement.x;
            double e = movement.y;
            double f = movement.z;
            if (e != 0.0) {
                e = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, entityBoundingBox, collisions, e);
                if (e != 0.0) {
                    entityBoundingBox = entityBoundingBox.offset(0.0, e, 0.0);
                }
            }

            boolean bl = Math.abs(d) < Math.abs(f);
            if (bl && f != 0.0) {
                f = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, entityBoundingBox, collisions, f);
                if (f != 0.0) {
                    entityBoundingBox = entityBoundingBox.offset(0.0, 0.0, f);
                }
            }

            if (d != 0.0) {
                d = VoxelShapes.calculateMaxOffset(Direction.Axis.X, entityBoundingBox, collisions, d);
                if (!bl && d != 0.0) {
                    entityBoundingBox = entityBoundingBox.offset(d, 0.0, 0.0);
                }
            }

            if (!bl && f != 0.0) {
                f = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, entityBoundingBox, collisions, f);
            }

            return new Vec3d(d, e, f);
        }
    }

    @Unique
    private static float[] collectStepHeights(Box collisionBox, List<VoxelShape> collisions, float f, float stepHeight) {
        FloatSet floatSet = new FloatArraySet(4);
        Iterator var5 = collisions.iterator();

        while(var5.hasNext()) {
            VoxelShape voxelShape = (VoxelShape)var5.next();
            DoubleList doubleList = voxelShape.getPointPositions(Direction.Axis.Y);
            DoubleListIterator var8 = doubleList.iterator();

            while(var8.hasNext()) {
                double d = (Double)var8.next();
                float g = (float)(d - collisionBox.minY);
                if (!(g < 0.0F) && g != stepHeight) {
                    if (g > f) {
                        break;
                    }

                    floatSet.add(g);
                }
            }
        }

        float[] fs = floatSet.toFloatArray();
        FloatArrays.unstableSort(fs);
        return fs;
    }

    @Unique
    private static List<VoxelShape> findCollisionsForMovement(@Nullable Entity entity, World world, List<VoxelShape> regularCollisions, Box movingEntityBoundingBox) {
        ImmutableList.Builder<VoxelShape> builder = ImmutableList.builderWithExpectedSize(regularCollisions.size() + 1);
        if (!regularCollisions.isEmpty()) {
            builder.addAll(regularCollisions);
        }

        WorldBorder worldBorder = world.getWorldBorder();
        boolean bl = entity != null && worldBorder.canCollide(entity, movingEntityBoundingBox);
        if (bl) {
            builder.add(worldBorder.asVoxelShape());
        }

        builder.addAll(world.getBlockCollisions(entity, movingEntityBoundingBox));
        return builder.build();
    }
}
