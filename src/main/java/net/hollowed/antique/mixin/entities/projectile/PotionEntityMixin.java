package net.hollowed.antique.mixin.entities.projectile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;

@Mixin(AbstractThrownPotion.class)
public abstract class PotionEntityMixin extends ThrowableItemProjectile {

    public PotionEntityMixin(EntityType<? extends ThrowableItemProjectile> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/throwableitemprojectile/ThrowableItemProjectile;onHit(Lnet/minecraft/world/phys/HitResult;)V"), cancellable = true)
    public void onCollision(HitResult hitResult, CallbackInfo ci) {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            AABB box = this.getBoundingBox().inflate(0.3);
            List<Entity> list = this.level().getEntities(this, box);
            for (Entity entity : list) {
                if (entity instanceof AbstractThrownPotion) {
                    ci.cancel();
                }
            }
        }
    }
}
