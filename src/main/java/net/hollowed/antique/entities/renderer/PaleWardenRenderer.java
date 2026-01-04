package net.hollowed.antique.entities.renderer;

import net.hollowed.antique.Antiquities;
import net.hollowed.antique.AntiquitiesClient;
import net.hollowed.antique.entities.PaleWardenEntity;
import net.hollowed.antique.entities.models.PaleWardenModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class PaleWardenRenderer extends MobRenderer<@NotNull PaleWardenEntity, @NotNull PaleWardenRenderState, @NotNull PaleWardenModel> {

    public PaleWardenRenderer(EntityRendererProvider.Context context) {
        super(context, new PaleWardenModel(context.bakeLayer(AntiquitiesClient.PALE_WARDEN_LAYER)), 0.5f);
        this.addLayer(new PaleWardenHeldItemFeatureRenderer<>(this));
    }

    @Override
    public PaleWardenRenderState createRenderState() {
        return new PaleWardenRenderState();
    }

    @Override
    public void extractRenderState(PaleWardenEntity livingEntity, PaleWardenRenderState livingEntityRenderState, float f) {
        super.extractRenderState(livingEntity, livingEntityRenderState, f);
        ArmedEntityRenderState.extractArmedEntityRenderState(livingEntity, livingEntityRenderState, this.itemModelResolver, f);
        livingEntityRenderState.entity = livingEntity;
    }

    @Override
    public @NotNull Identifier getTextureLocation(PaleWardenRenderState state) {
        return Identifier.fromNamespaceAndPath(Antiquities.MOD_ID, "textures/entity/pale_warden.png");
    }
}
