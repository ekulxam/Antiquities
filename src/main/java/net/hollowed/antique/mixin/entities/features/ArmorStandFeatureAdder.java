package net.hollowed.antique.mixin.entities.features;

import net.hollowed.antique.client.armor.renderers.AdventureArmorArmorStandFeatureRenderer;
import net.minecraft.client.render.entity.ArmorStandEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;
import net.minecraft.entity.decoration.ArmorStandEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorStandEntityRenderer.class)
public abstract class ArmorStandFeatureAdder extends LivingEntityRenderer<ArmorStandEntity, ArmorStandEntityRenderState, ArmorStandEntityModel> {

    public ArmorStandFeatureAdder(EntityRendererFactory.Context ctx, ArmorStandEntityModel model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addCustomFeature(EntityRendererFactory.Context context, CallbackInfo ci) {
        this.addFeature(new AdventureArmorArmorStandFeatureRenderer(this, context.getEntityModels()));
    }
}
