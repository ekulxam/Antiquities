package net.hollowed.antique.mixin;

import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ModelBaker.class)
public class ModelBakerMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(LoadedEntityModels entityModels, Map blockModels, Map itemModels, Map allModels, UnbakedModel missingModel, CallbackInfo ci) {
        for (Object unbakedModel : allModels.values()) {
            if (unbakedModel instanceof JsonUnbakedModel model && !model.getTextures().values().values().stream().toList().isEmpty()) {
                //something something idk
            }
        }
    }
}
