package net.hollowed.antique.mixin;

import net.hollowed.antique.enchantments.EnchantmentListener;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockItem.class)
public class BundleJumblingPreventStackDecrement {

    @Redirect(
            method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;decrementUnlessCreative(ILnet/minecraft/entity/LivingEntity;)V"
            )
    )
    private void conditionalDecrement(ItemStack instance, int amount, LivingEntity entity) {
        if (!EnchantmentListener.hasEnchantment(instance, "antique:jumbling")) {
            instance.decrementUnlessCreative(1, entity);
        }
    }
}
