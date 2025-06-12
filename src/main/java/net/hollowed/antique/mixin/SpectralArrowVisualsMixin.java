package net.hollowed.antique.mixin;

import net.hollowed.antique.particles.ModParticles;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SpectralArrowEntity.class)
public abstract class SpectralArrowVisualsMixin extends PersistentProjectileEntity {

    protected SpectralArrowVisualsMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ModParticles.HIT_MARKER, this.getX(), this.getY(), this.getZ(), 1, 0.0, 0.0, 0.0, 0);
        }
    }
}
