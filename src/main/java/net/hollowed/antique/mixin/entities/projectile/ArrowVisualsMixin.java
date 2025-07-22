package net.hollowed.antique.mixin.entities.projectile;

import net.hollowed.antique.index.AntiqueParticles;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ArrowEntity.class)
public abstract class ArrowVisualsMixin extends PersistentProjectileEntity {

    protected ArrowVisualsMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(AntiqueParticles.HIT_MARKER, this.getX(), this.getY(), this.getZ(), 1, 0.0, 0.0, 0.0, 0);
        }
    }
}
