package net.hollowed.antique.mixin.blocks;

import net.hollowed.antique.index.AntiqueItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.PowderSnowBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PowderSnowBlock.class)
public abstract class PowderSnowWalkableMixin {

    @Redirect(
            method = "canEntityWalkOnPowderSnow",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"
            )
    )
    private static boolean modifyBootCheck(ItemStack stack, net.minecraft.world.item.Item item) {
        return stack.is(Items.LEATHER_BOOTS) || stack.is(AntiqueItems.FUR_BOOTS);
    }
}

