package net.hollowed.antique.index;

import net.hollowed.antique.Antiquities;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class AntiqueItemTags {

    public static final TagKey<Item> BETTER_ARMOR = of("better_armor");

    public static final TagKey<Item> IRON_ARMOR = of("iron_armor");
    public static final TagKey<Item> CHAIN_ARMOR = of("chain_armor");
    public static final TagKey<Item> GOLD_ARMOR = of("gold_armor");
    public static final TagKey<Item> DIAMOND_ARMOR = of("diamond_armor");
    public static final TagKey<Item> NETHERITE_ARMOR = of("netherite_armor");

    private AntiqueItemTags() {

    }

    private static TagKey<Item> of(String id) {
        return TagKey.of(RegistryKeys.ITEM, Antiquities.id(id));
    }
}
