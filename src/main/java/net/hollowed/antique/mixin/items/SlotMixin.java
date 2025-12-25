package net.hollowed.antique.mixin.items;

import net.hollowed.antique.index.AntiqueItems;
import net.hollowed.antique.items.BagOfTricksItem;
import net.hollowed.antique.items.SatchelItem;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Slot.class)
public abstract class SlotMixin {

    @Inject(method = "set", at = @At("HEAD"))
    public void setStack(ItemStack stack, CallbackInfo ci) {
        if (stack.is(AntiqueItems.SATCHEL)) {
            SatchelItem.setInternalIndex(stack, -1);
        }
        if (stack.is(AntiqueItems.BAG_OF_TRICKS)) {
            BagOfTricksItem.setInternalIndex(stack, -1);
        }
    }
}
