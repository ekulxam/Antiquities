package net.hollowed.antique.mixin.items;

import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {

    @Shadow
    public abstract String toString();

    @Inject(method = "canEnchant", at = @At("HEAD"), cancellable = true)
    private void disableDisallowedEnchantments(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.get(DataComponents.UNBREAKABLE) != null) {
            if (isDisallowedEnchantment(((Enchantment) (Object) this).toString())) {
                cir.setReturnValue(false);
            }
        }
    }

    @Unique
    private static boolean isDisallowedEnchantment(String enchantment) {
        return CombatAmenities.DURABILITY_ENCHANTMENTS.contains(enchantment);
    }
}
