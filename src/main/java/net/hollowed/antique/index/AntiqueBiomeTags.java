package net.hollowed.antique.index;

import net.hollowed.antique.Antiquities;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class AntiqueBiomeTags {

    public static final TagKey<Biome> IS_WINDSWEPT = of("is_windswept");

    @SuppressWarnings("all")
    private static TagKey<Biome> of(String id) {
        return TagKey.create(Registries.BIOME, Antiquities.id(id));
    }
}
