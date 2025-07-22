package net.hollowed.antique.mixin.entities.projectile;

import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PersistentProjectileEntity.class)
public class AttackableProjectiles {

    @Inject(method = "isAttackable", at = @At("HEAD"), cancellable = true)
    public void attackable(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
