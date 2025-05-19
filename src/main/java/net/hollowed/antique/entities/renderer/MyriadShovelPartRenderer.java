package net.hollowed.antique.entities.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.antique.entities.parts.MyriadShovelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class MyriadShovelPartRenderer extends EntityRenderer<MyriadShovelPart, MyriadShovelRenderState> {

	public MyriadShovelPartRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	public void render(MyriadShovelRenderState myriadShovelRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {

	}

	public MyriadShovelRenderState createRenderState() {
		return new MyriadShovelRenderState();
	}

	public void updateRenderState(MyriadShovelPart myriadShovelEntity, MyriadShovelRenderState myriadShovelRenderState, float f) {
		super.updateRenderState(myriadShovelEntity, myriadShovelRenderState, f);
		myriadShovelRenderState.entity = myriadShovelEntity;
	}
}