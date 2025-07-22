package net.hollowed.antique.mixin.items;

import net.minecraft.block.TntBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ThrowablePotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.component.type.BundleContentsComponent;
import org.apache.commons.lang3.math.Fraction;

@Mixin(BundleContentsComponent.class)
public abstract class BundleItemWeightChanger {

    @Inject(method = "getOccupancy(Lnet/minecraft/item/ItemStack;)Lorg/apache/commons/lang3/math/Fraction;", at = @At("HEAD"), cancellable = true)
    private static void adjustOccupancyForNonStackableItems(ItemStack stack, CallbackInfoReturnable<Fraction> cir) {
        Fraction occupancy = Fraction.getFraction(1, stack.getMaxCount());

        if (!(stack.getItem() instanceof BundleItem)) {
            if (stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof TntBlock) {
                occupancy = occupancy.multiplyBy(Fraction.getFraction(4, 1));
            } else if (stack.getItem() instanceof ThrowablePotionItem) {
                occupancy = occupancy.multiplyBy(Fraction.getFraction(1, 3));
            } else if (!stack.isStackable()) {
                occupancy = occupancy.multiplyBy(Fraction.getFraction(1, 6));
            }
        }

        cir.setReturnValue(occupancy);
    }
}
