package net.hollowed.antique.blocks.custom.screen;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.featuretoggle.ToggleableFeature;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class AntiqueScreenHandlerType<T extends ScreenHandler> implements ToggleableFeature {
	public static final ScreenHandlerType<DyeingScreenHandler> DYE_TABLE = register("dye_table", DyeingScreenHandler::new);

	private static <T extends ScreenHandler> ScreenHandlerType<T> register(String id, ScreenHandlerType.Factory<T> factory) {
		return Registry.register(Registries.SCREEN_HANDLER, id, new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES));
	}

	@Override
	public FeatureSet getRequiredFeatures() {
		return null;
	}

	public static void init() {}
}
