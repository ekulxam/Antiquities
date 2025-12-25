package net.hollowed.antique.mixin.entities.projectile;

import net.hollowed.antique.entities.IllusionerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkMoreDamageMixin extends Projectile implements ItemSupplier {

    @Shadow protected abstract List<FireworkExplosion> getExplosions();

    @Shadow @Nullable private LivingEntity attachedToEntity;

    public FireworkMoreDamageMixin(EntityType<? extends Projectile> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "dealExplosionDamage", at = @At("HEAD"))
    public void explode(ServerLevel world, CallbackInfo ci) {
        float f = 0.0F;
        List<FireworkExplosion> list = this.getExplosions();
        if (!list.isEmpty()) {
            f = 5.0F + (float)(list.size() * 2);
        }

        if (f > 0.0F) {

            Vec3 vec3d = this.position();
            List<LivingEntity> list2 = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.0));
            Iterator<LivingEntity> var8 = list2.iterator();

            while(true) {
                LivingEntity livingEntity;
                do {
                    do {
                        if (!var8.hasNext()) {
                            return;
                        }

                        livingEntity = var8.next();
                    } while(livingEntity == this.attachedToEntity);
                } while(this.distanceToSqr(livingEntity) > 25.0);

                boolean bl = false;

                for(int i = 0; i < 2; ++i) {
                    Vec3 vec3d2 = new Vec3(livingEntity.getX(), livingEntity.getY(0.5 * (double)i), livingEntity.getZ());
                    HitResult hitResult = this.level().clip(new ClipContext(vec3d, vec3d2, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
                    if (hitResult.getType() == HitResult.Type.MISS) {
                        bl = true;
                        break;
                    }
                }

                if (bl) {
                    float g = f * (float)Math.sqrt((5.0 - (double)this.distanceTo(livingEntity)) / 5.0);
                    if (this.getOwner() instanceof IllusionerEntity) {
                        livingEntity.hurtServer(world, this.damageSources().fireworks((FireworkRocketEntity) (Object) this, this.getOwner()), g * 2);
                    }
                }
            }
        }
    }
}
