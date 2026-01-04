package net.hollowed.antique.enchantments;

import static net.minecraft.core.component.DataComponents.ENCHANTMENTS;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class EnchantmentListener {

    public static boolean hasEnchantment(ItemStack stack, String enchantKey) {
        var enchantments = stack.getOrDefault(ENCHANTMENTS, ItemEnchantments.EMPTY).entrySet();

        for (var entry : enchantments) {
            String enchant = entry.getKey().getRegisteredName();

            if (enchant.contains(enchantKey)) {
                return true;
            }
        }

        return false;
    }
}
