package net.hollowed.antique.mixin.entities.projectile;

import net.hollowed.combatamenities.index.CAParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ThrownTrident.class)
public abstract class TridentVisualsMixin extends AbstractArrow {

    protected TridentVisualsMixin(EntityType<? extends AbstractArrow> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void onHit(@NotNull HitResult hitResult) {
        super.onHit(hitResult);
        if (this.level() instanceof ServerLevel serverWorld) {
            Vec3 pos = this.position().add(this.getLookAngle().multiply(1, 1, -1));
            serverWorld.sendParticles(CAParticles.RING, pos.x(), pos.y(), pos.z(), 1, 0.0, 0.0, 0.0, 0);
        }
    }
}
