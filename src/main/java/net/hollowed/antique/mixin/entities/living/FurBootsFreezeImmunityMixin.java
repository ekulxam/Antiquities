package net.hollowed.antique.mixin.entities.living;

import net.hollowed.antique.index.AntiqueItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class FurBootsFreezeImmunityMixin {

    @Inject(method = "canFreeze", at = @At("HEAD"), cancellable = true)
    private void preventFreezing(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        ItemStack boots = entity.getItemBySlot(EquipmentSlot.FEET);
        if (boots.is(AntiqueItems.FUR_BOOTS)) {
            cir.setReturnValue(false);
        }
    }
}
