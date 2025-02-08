package net.hollowed.antique.entities.models;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

// Made with Blockbench 4.12.2
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class ExplosiveSpearCloth extends EntityModel<EntityRenderState> {
	private final ModelPart segment;
	private final ModelPart segment2;
	private final ModelPart segment3;
	private final ModelPart segment4;
	public ExplosiveSpearCloth(ModelPart root) {
        super(root);
        this.segment = root.getChild("segment");
		this.segment2 = root.getChild("segment2");
		this.segment3 = root.getChild("segment3");
		this.segment4 = root.getChild("segment4");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData segment = modelPartData.addChild("segment", ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, 0.0F, -1.5F, 0.0F, 4.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData segment2 = modelPartData.addChild("segment2", ModelPartBuilder.create().uv(6, 0).cuboid(0.0F, 0.0F, -1.5F, 0.0F, 4.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 28.0F, 0.0F));

		ModelPartData segment3 = modelPartData.addChild("segment3", ModelPartBuilder.create().uv(0, 7).cuboid(0.0F, 0.0F, -1.5F, 0.0F, 4.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 32.0F, 0.0F));

		ModelPartData segment4 = modelPartData.addChild("segment4", ModelPartBuilder.create().uv(6, 7).cuboid(0.0F, 0.0F, -1.5F, 0.0F, 4.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 36.0F, 0.0F));
		return TexturedModelData.of(modelData, 16, 16);
	}

	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		segment.render(matrices, vertexConsumer, light, overlay);
		segment2.render(matrices, vertexConsumer, light, overlay);
		segment3.render(matrices, vertexConsumer, light, overlay);
		segment4.render(matrices, vertexConsumer, light, overlay);
	}
}