package net.hollowed.antique.mixin;

import net.hollowed.antique.items.ModItems;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class FurBootsFreezeImmunityMixin {

    @Inject(
            method = "canFreeze", // Target the canFreeze method
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventFreezing(CallbackInfoReturnable<Boolean> cir) {
        // Check if the entity is wearing fur boots
        LivingEntity entity = (LivingEntity) (Object) this;
        ItemStack boots = entity.getEquippedStack(EquipmentSlot.FEET);
        if (boots.isOf(ModItems.FUR_BOOTS)) {
            cir.setReturnValue(false); // Prevent freezing if wearing fur boots
        }
    }
}
