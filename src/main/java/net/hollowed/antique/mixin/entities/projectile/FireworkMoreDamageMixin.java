package net.hollowed.antique.mixin.entities.projectile;

import net.hollowed.antique.entities.IllusionerEntity;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkMoreDamageMixin extends ProjectileEntity implements FlyingItemEntity {

    @Shadow protected abstract List<FireworkExplosionComponent> getExplosions();

    @Shadow @Nullable private LivingEntity shooter;

    public FireworkMoreDamageMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "explode", at = @At("HEAD"))
    public void explode(ServerWorld world, CallbackInfo ci) {
        float f = 0.0F;
        List<FireworkExplosionComponent> list = this.getExplosions();
        if (!list.isEmpty()) {
            f = 5.0F + (float)(list.size() * 2);
        }

        if (f > 0.0F) {

            Vec3d vec3d = this.getEntityPos();
            List<LivingEntity> list2 = this.getEntityWorld().getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox().expand(1.0));
            Iterator<LivingEntity> var8 = list2.iterator();

            while(true) {
                LivingEntity livingEntity;
                do {
                    do {
                        if (!var8.hasNext()) {
                            return;
                        }

                        livingEntity = var8.next();
                    } while(livingEntity == this.shooter);
                } while(this.squaredDistanceTo(livingEntity) > 25.0);

                boolean bl = false;

                for(int i = 0; i < 2; ++i) {
                    Vec3d vec3d2 = new Vec3d(livingEntity.getX(), livingEntity.getBodyY(0.5 * (double)i), livingEntity.getZ());
                    HitResult hitResult = this.getEntityWorld().raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
                    if (hitResult.getType() == HitResult.Type.MISS) {
                        bl = true;
                        break;
                    }
                }

                if (bl) {
                    float g = f * (float)Math.sqrt((5.0 - (double)this.distanceTo(livingEntity)) / 5.0);
                    if (this.getOwner() instanceof IllusionerEntity) {
                        livingEntity.damage(world, this.getDamageSources().fireworks((FireworkRocketEntity) (Object) this, this.getOwner()), g * 2);
                    }
                }
            }
        }
    }
}
