package net.hollowed.antique.index;

import net.hollowed.antique.Antiquities;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class AntiqueBlockTags {

    public static final TagKey<Block> WATER_OR_AIR = of("water_or_air");

    private AntiqueBlockTags() {

    }

    @SuppressWarnings("all")
    private static TagKey<Block> of(String id) {
        return TagKey.of(RegistryKeys.BLOCK, Antiquities.id(id));
    }
}
