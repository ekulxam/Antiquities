package net.hollowed.antique.entities.custom;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class IllusionerArrowEntity extends ArrowEntity {

    public IllusionerArrowEntity(EntityType<? extends ArrowEntity> entityType, World world) {
        super(entityType, world);
    }

    public static FireworksComponent randomFireworkBall() {
        Random random = new Random();

        // Generate 1–3 random colors
        IntList colors = IntArrayList.wrap(random.ints(1 + random.nextInt(3), 0, 0xFFFFFF + 1).toArray());

        // 50% chance of having fade colors
        IntList fadeColors = random.nextBoolean()
                ? IntArrayList.wrap(random.ints(1 + random.nextInt(2), 0, 0xFFFFFF + 1).toArray())
                : IntArrayList.of();

        FireworkExplosionComponent explosion = new FireworkExplosionComponent(
                FireworkExplosionComponent.Type.LARGE_BALL,
                colors,
                fadeColors,
                random.nextBoolean(), // hasTrail
                true  // hasTwinkle
        );

        return new FireworksComponent(-3, List.of(explosion));
    }

    public static FireworksComponent randomFirework() {
        Random random = new Random();

        // Generate 1–3 random colors
        IntList colors = IntArrayList.wrap(random.ints(1 + random.nextInt(3), 0, 0xFFFFFF + 1).toArray());

        // 50% chance of having fade colors
        IntList fadeColors = random.nextBoolean()
                ? IntArrayList.wrap(random.ints(1 + random.nextInt(2), 0, 0xFFFFFF + 1).toArray())
                : IntArrayList.of();

        FireworkExplosionComponent explosion = new FireworkExplosionComponent(
                randomType(),
                colors,
                fadeColors,
                random.nextBoolean(), // hasTrail
                true  // hasTwinkle
        );

        return new FireworksComponent(1, List.of(explosion));
    }

    private void explodeAndRemove(ServerWorld world) {
        world.sendEntityStatus(this, (byte)17);
        this.emitGameEvent(GameEvent.EXPLODE, this.getOwner());
        this.explode(world);
        this.discard();
    }

    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        World var3 = this.getWorld();
        if (var3 instanceof ServerWorld serverWorld) {
            this.explodeAndRemove(serverWorld);
        }

    }

    protected void onBlockHit(BlockHitResult blockHitResult) {
        BlockPos blockPos = new BlockPos(blockHitResult.getBlockPos());
        this.getWorld().getBlockState(blockPos).onEntityCollision(this.getWorld(), blockPos, this, EntityCollisionHandler.DUMMY);
        World var4 = this.getWorld();
        if (var4 instanceof ServerWorld serverWorld) {
            if (this.hasExplosionEffects()) {
                this.explodeAndRemove(serverWorld);
            }
        }

        super.onBlockHit(blockHitResult);
    }

    private static FireworkExplosionComponent.Type randomType() {
        return FireworkExplosionComponent.Type.BURST;
    }

    private List<FireworkExplosionComponent> getExplosions() {
        FireworksComponent fireworksComponent = randomFirework();
        return fireworksComponent.explosions();
    }

    private boolean hasExplosionEffects() {
        return !this.getExplosions().isEmpty();
    }

    private void explode(ServerWorld world) {
        float f = 0.0F;
        List<FireworkExplosionComponent> list = this.getExplosions();
        if (!list.isEmpty()) {
            f = 5.0F + (float)(list.size() * 2);
        }

        if (f > 0.0F) {
            if (this.getOwner() != null) {
                this.getOwner().damage(world, this.getDamageSources().fireworks(new FireworkRocketEntity(EntityType.FIREWORK_ROCKET, world), this.getOwner()), 5.0F + (float)(list.size() * 2));
            }

            Vec3d vec3d = this.getPos();
            List<LivingEntity> list2 = this.getWorld().getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox().expand(5.0));
            Iterator<LivingEntity> var8 = list2.iterator();

            while(true) {
                LivingEntity livingEntity;
                do {
                    do {
                        if (!var8.hasNext()) {
                            return;
                        }

                        livingEntity = var8.next();
                    } while(livingEntity == this.getOwner());
                } while(this.squaredDistanceTo(livingEntity) > 25.0);

                boolean bl = false;

                for(int i = 0; i < 2; ++i) {
                    Vec3d vec3d2 = new Vec3d(livingEntity.getX(), livingEntity.getBodyY(0.5 * (double)i), livingEntity.getZ());
                    HitResult hitResult = this.getWorld().raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
                    if (hitResult.getType() == HitResult.Type.MISS) {
                        bl = true;
                        break;
                    }
                }

                if (bl) {
                    float g = f * (float)Math.sqrt((5.0 - (double)this.distanceTo(livingEntity)) / 5.0);
                    livingEntity.damage(world, this.getDamageSources().fireworks(new FireworkRocketEntity(EntityType.FIREWORK_ROCKET, world), this.getOwner()), g);
                }
            }
        }
    }
}
