package net.hollowed.antique.client.armor.models;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;

public class VanillaArmorModel<S extends BipedEntityRenderState> extends BipedEntityModel<S> {

    public final ModelPart rightBoot;
    public final ModelPart leftBoot;
    public final ModelPart leggingsBody;

	public VanillaArmorModel(ModelPart root) {
        super(root);
        this.rightBoot = root.getChild("right_boot");
        this.leftBoot = root.getChild("left_boot");
        this.leggingsBody = root.getChild("leggings_body");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = BipedEntityModel.getModelData(Dilation.NONE, 0.0F);
		ModelPartData modelPartData = modelData.getRoot();

        modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.27F))
                .uv(0, 16).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(1.0F))
                .uv(0, 32).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(1.5F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        modelPartData.addChild("body", ModelPartBuilder.create().uv(32, 0).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.27F))
                .uv(32, 16).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(1.02F))
                .uv(32, 32).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(1.5F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        modelPartData.addChild("right_arm", ModelPartBuilder.create().uv(56, 32).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.27F))
                .uv(16, 64).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(1.03F))
                .uv(72, 0).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(1.4F)), ModelTransform.origin(-4.0F, 2.0F, 0.0F));

        modelPartData.addChild("left_arm", ModelPartBuilder.create().uv(0, 64).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.27F))
                .uv(32, 64).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(1.03F))
                .uv(72, 16).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(1.4F)), ModelTransform.origin(4.0F, 2.0F, 0.0F));

        modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(0, 80).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.27F))
                .uv(16, 80).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.5F))
                .uv(32, 80).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(1.0F)), ModelTransform.origin(-2.0F, 0.0F, 0.0F));

        modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(48, 80).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.27F))
                .uv(64, 80).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.5F))
                .uv(80, 64).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(1.0F)), ModelTransform.origin(2.0F, 0.0F, 0.0F));

        modelPartData.addChild("leggings_body", ModelPartBuilder.create().uv(0, 48).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.28F))
                .uv(24, 48).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.51F))
                .uv(48, 48).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(1.01F)), ModelTransform.origin(0.0F, -12.0F, 0.0F));

        modelPartData.addChild("right_boot", ModelPartBuilder.create().uv(56, 0).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.27F))
                .uv(64, 64).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.49F)), ModelTransform.origin(-2.0F, 0.0F, 0.0F));

        modelPartData.addChild("left_boot", ModelPartBuilder.create().uv(56, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.27F))
                .uv(48, 64).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.49F)), ModelTransform.origin(2.0F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 128, 128);
	}

    @Override
    public void setAngles(S bipedEntityRenderState) {

    }
}