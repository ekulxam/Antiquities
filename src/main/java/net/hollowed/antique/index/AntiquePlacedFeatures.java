package net.hollowed.antique.index;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.hollowed.antique.Antiquities;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;

public class AntiquePlacedFeatures {

    public static final RegistryKey<PlacedFeature> MYRIAD_ORE_MIDDLE_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Antiquities.id("ore_myriad_middle"));
    public static final RegistryKey<PlacedFeature> MYRIAD_ORE_SMALL_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Antiquities.id("ore_myriad_small"));
    public static final RegistryKey<PlacedFeature> MYRIAD_ORE_UPPER_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Antiquities.id("ore_myriad_upper"));
    
    public static void init() {
        BiomeModifications.addFeature(BiomeSelectors.tag(AntiqueBiomeTags.IS_WINDSWEPT), GenerationStep.Feature.UNDERGROUND_ORES, MYRIAD_ORE_MIDDLE_KEY);
        BiomeModifications.addFeature(BiomeSelectors.tag(AntiqueBiomeTags.IS_WINDSWEPT), GenerationStep.Feature.UNDERGROUND_ORES, MYRIAD_ORE_SMALL_KEY);
        BiomeModifications.addFeature(BiomeSelectors.tag(AntiqueBiomeTags.IS_WINDSWEPT), GenerationStep.Feature.UNDERGROUND_ORES, MYRIAD_ORE_UPPER_KEY);
    }
}
