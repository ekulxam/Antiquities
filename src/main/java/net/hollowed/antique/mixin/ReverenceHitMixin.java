package net.hollowed.antique.mixin;

import net.hollowed.antique.items.custom.ReverenceItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class ReverenceHitMixin {

    @Inject(method = "damage", at = @At("HEAD"))
    public void damage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getSource() instanceof LivingEntity entity && entity.getStackInHand(Hand.MAIN_HAND).getItem() instanceof ReverenceItem) {
            ((LivingEntity) (Object) this).setFireTicks(320);
        }
    }
}
