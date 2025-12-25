package net.hollowed.antique.index;

import net.hollowed.antique.Antiquities;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

@SuppressWarnings("unused")
public class AntiqueItemTags {

    public static final TagKey<Item> IRON_ARMOR = of("iron_armor");
    public static final TagKey<Item> CHAIN_ARMOR = of("chain_armor");
    public static final TagKey<Item> GOLD_ARMOR = of("gold_armor");
    public static final TagKey<Item> DIAMOND_ARMOR = of("diamond_armor");
    public static final TagKey<Item> NETHERITE_ARMOR = of("netherite_armor");

    public static final TagKey<Item> LARGE_CLOTH = of("large_cloth");

    private AntiqueItemTags() {

    }

    private static TagKey<Item> of(String id) {
        return TagKey.create(Registries.ITEM, Antiquities.id(id));
    }
}
