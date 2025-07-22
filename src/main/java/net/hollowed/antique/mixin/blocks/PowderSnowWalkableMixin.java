package net.hollowed.antique.mixin.blocks;

import net.hollowed.antique.index.AntiqueItems;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PowderSnowBlock.class)
public abstract class PowderSnowWalkableMixin {

    @Redirect(
            method = "canWalkOnPowderSnow",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"
            )
    )
    private static boolean modifyBootCheck(ItemStack stack, net.minecraft.item.Item item) {
        return stack.isOf(Items.LEATHER_BOOTS) || stack.isOf(AntiqueItems.FUR_BOOTS);
    }
}

