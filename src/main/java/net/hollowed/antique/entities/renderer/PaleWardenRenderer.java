package net.hollowed.antique.entities.renderer;

import net.hollowed.antique.Antiquities;
import net.hollowed.antique.AntiquitiesClient;
import net.hollowed.antique.entities.PaleWardenEntity;
import net.hollowed.antique.entities.models.PaleWardenModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.util.Identifier;

public class PaleWardenRenderer extends MobEntityRenderer<PaleWardenEntity, PaleWardenRenderState, PaleWardenModel> {

    public PaleWardenRenderer(EntityRendererFactory.Context context) {
        super(context, new PaleWardenModel(context.getPart(AntiquitiesClient.PALE_WARDEN_LAYER)), 0.5f);
        this.addFeature(new PaleWardenHeldItemFeatureRenderer<>(this));
    }

    @Override
    public PaleWardenRenderState createRenderState() {
        return new PaleWardenRenderState();
    }

    @Override
    public void updateRenderState(PaleWardenEntity livingEntity, PaleWardenRenderState livingEntityRenderState, float f) {
        super.updateRenderState(livingEntity, livingEntityRenderState, f);
        ArmedEntityRenderState.updateRenderState(livingEntity, livingEntityRenderState, this.itemModelResolver);
        livingEntityRenderState.entity = livingEntity;
    }

    @Override
    public Identifier getTexture(PaleWardenRenderState state) {
        return Identifier.of(Antiquities.MOD_ID, "textures/entity/pale_warden.png");
    }
}
