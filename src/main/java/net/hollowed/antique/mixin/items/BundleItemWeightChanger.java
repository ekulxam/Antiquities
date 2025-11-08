package net.hollowed.antique.mixin.items;

import net.minecraft.item.*;
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
            if (stack.getItem() instanceof PotionItem) {
                occupancy = occupancy.multiplyBy(Fraction.getFraction(1, 3));
            } else if (!stack.isStackable()) {
                occupancy = occupancy.multiplyBy(Fraction.getFraction(1, 4));
            }
        }

        cir.setReturnValue(occupancy);
    }
}
