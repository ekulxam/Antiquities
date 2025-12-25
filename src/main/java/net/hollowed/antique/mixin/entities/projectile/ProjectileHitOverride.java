package net.hollowed.antique.mixin.entities.projectile;

import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Projectile.class)
public class ProjectileHitOverride {

    @Inject(method = "isPickable()Z", at = @At("HEAD"), cancellable = true)
    public void canHit(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
