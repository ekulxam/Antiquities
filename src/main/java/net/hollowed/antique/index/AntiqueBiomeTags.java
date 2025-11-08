package net.hollowed.antique.index;

import net.hollowed.antique.Antiquities;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;

public class AntiqueBiomeTags {

    public static final TagKey<Biome> IS_WINDSWEPT = of("is_windswept");

    @SuppressWarnings("all")
    private static TagKey<Biome> of(String id) {
        return TagKey.of(RegistryKeys.BIOME, Antiquities.id(id));
    }
}
