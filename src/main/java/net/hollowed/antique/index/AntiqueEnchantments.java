package net.hollowed.antique.index;

import net.hollowed.antique.Antiquities;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

@SuppressWarnings("unused")
public interface AntiqueEnchantments {
    RegistryKey<Enchantment> PROJECTING = of("projecting");

    RegistryKey<Enchantment> JUMBLING = of("jumbling");

    RegistryKey<Enchantment> CURSE_OF_VOIDING = of("curse_of_voiding");

    RegistryKey<Enchantment> KINEMATIC = of("kinematic");

    RegistryKey<Enchantment> IMPETUS = of("impetus");

    private static RegistryKey<Enchantment> of(String name) {
      return RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(Antiquities.MOD_ID, name));
    }

    static void initialize() {}
}