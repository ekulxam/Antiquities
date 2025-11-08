package net.hollowed.antique.index;

import net.hollowed.antique.worldgen.features.MyriadOreFeature;
import net.hollowed.antique.worldgen.features.MyriadOreFeatureConfig;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class AntiqueFeatures {

    @SuppressWarnings("unused")
    public static final Feature<MyriadOreFeatureConfig> MYRIAD_ORE = register("antique:myriad_ore", new MyriadOreFeature(MyriadOreFeatureConfig.CODEC));

    @SuppressWarnings("all")
    private static <C extends FeatureConfig, F extends Feature<C>> F register(String name, F feature) {
        return Registry.register(Registries.FEATURE, name, feature);
    }

    public static void init() {

    }
}
