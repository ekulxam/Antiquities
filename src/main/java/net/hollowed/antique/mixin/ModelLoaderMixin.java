package net.hollowed.antique.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.antique.util.ClientSetup;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(ModelBaker.BakedModels.class)
public abstract class ModelLoaderMixin {

	@Inject(method = "itemStackModels", at = @At("RETURN"))
	private void getBakedModelMap(CallbackInfoReturnable<Map<Identifier, ItemModel>> cir) {
		// Log all baked models to check what's available

		System.out.println("Model baking completed, modifying result");
		ClientSetup.modifyBakingResult(cir.getReturnValue());
	}
}