package net.hollowed.antique.mixin.entities.projectile;

import net.minecraft.entity.projectile.ProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProjectileEntity.class)
public class ProjectileHitOverride {

    @Inject(method = "canHit()Z", at = @At("HEAD"), cancellable = true)
    public void canHit(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
