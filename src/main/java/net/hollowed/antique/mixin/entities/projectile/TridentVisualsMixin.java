package net.hollowed.antique.mixin.entities.projectile;

import net.hollowed.combatamenities.particles.ModParticles;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TridentEntity.class)
public abstract class TridentVisualsMixin extends PersistentProjectileEntity {

    protected TridentVisualsMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ModParticles.RING, this.getX(), this.getY(), this.getZ(), 1, 0.0, 0.0, 0.0, 0);
        }
    }
}
