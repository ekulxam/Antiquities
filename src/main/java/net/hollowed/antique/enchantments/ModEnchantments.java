package net.hollowed.antique.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class ModEnchantments {
  public static final RegistryKey<Enchantment> PROJECTING = of("projecting");

  public static final RegistryKey<Enchantment> JUMBLING = of("jumbling");

  public static final RegistryKey<Enchantment> CURSE_OF_VOIDING = of("curse_of_voiding");

  public static final RegistryKey<Enchantment> KINEMATIC = of("kinematic");

  public static final RegistryKey<Enchantment> IMPETUS = of("impetus");
 
  private static RegistryKey<Enchantment> of(String name) {
    return RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of("antique", name));
  }
 
  public static void initialize() {

  }
}