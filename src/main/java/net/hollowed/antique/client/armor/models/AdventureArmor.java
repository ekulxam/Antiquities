package net.hollowed.antique.client.armor.models;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;

public class AdventureArmor<S extends BipedEntityRenderState> extends BipedEntityModel<S> {
	public final ModelPart satchel;
	public final ModelPart leftArmThick;
	public final ModelPart rightArmThick;

	public AdventureArmor(ModelPart root) {
		super(root);
		this.satchel = root.getChild("satchel");
		this.leftArmThick = root.getChild("leftArmThick");
		this.rightArmThick = root.getChild("rightArmThick");

		// Disable visibility of default biped parts
		this.head.visible = false;
		this.hat.visible = false;
		this.body.visible = true;
		this.rightArm.visible = true;
		this.leftArm.visible = true;
		this.rightLeg.visible = true;
		this.leftLeg.visible = true;
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = BipedEntityModel.getModelData(Dilation.NONE, 0.0F);
		ModelPartData modelPartData = modelData.getRoot();

		modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 16).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.27F))
				.uv(24, 0).cuboid(2.0F, -2.0F, -3.0F, 3.0F, 3.0F, 8.0F, new Dilation(0.0F))
				.uv(24, 11).cuboid(-5.0F, -2.0F, -3.0F, 3.0F, 3.0F, 8.0F, new Dilation(0.0F))
				.uv(46, 16).cuboid(-2.0F, -2.0F, 2.0F, 4.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

		modelPartData.addChild("satchel", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.32F))
				.uv(54, 22).cuboid(-6.0F, 11.0F, -2.0F, 2.0F, 5.0F, 4.0F, new Dilation(0.15F)), ModelTransform.origin(5.0F, 13.0F, 0.0F));

		modelPartData.addChild("right_arm", ModelPartBuilder.create().uv(14, 54).cuboid(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new Dilation(0.8F))
				.uv(0, 84).cuboid(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new Dilation(0.99F))
				.uv(48, 38).cuboid(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new Dilation(0.47F)), ModelTransform.origin(-4.0F, 2.0F, 0.0F));

		modelPartData.addChild("left_arm", ModelPartBuilder.create().uv(46, 0).cuboid(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new Dilation(0.27F))
		.uv(0, 48).cuboid(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new Dilation(0.47F)), ModelTransform.origin(4.0F, 2.0F, 0.0F));

		modelPartData.addChild("rightArmThick", ModelPartBuilder.create().uv(14, 54).cuboid(-2.75F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.8F))
				.uv(26, 96).cuboid(-2.75F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.99F))
				.uv(48, 38).cuboid(-2.75F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.47F)), ModelTransform.origin(-5.0F, 2.0F, 0.0F));

		modelPartData.addChild("leftArmThick", ModelPartBuilder.create().uv(46, 0).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.27F))
				.uv(0, 83).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.47F)), ModelTransform.origin(5.0F, 2.0F, 0.0F));

		modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(32, 38).mirrored().cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.27F)).mirrored(false)
		.uv(41, 66).mirrored().cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.47F)).mirrored(false)
		.uv(14, 79).mirrored().cuboid(-2.0F, 10.0F, -4.0F, 4.0F, 2.0F, 2.0F, new Dilation(0.27F)).mirrored(false), ModelTransform.origin(2.0F, 12.0F, 0.0F));

		modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(32, 38).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.27F))
		.uv(41, 66).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.47F))
		.uv(14, 79).cuboid(-2.0F, 10.0F, -4.0F, 4.0F, 2.0F, 2.0F, new Dilation(0.27F)), ModelTransform.origin(-2.0F, 12.0F, 0.0F));
		return TexturedModelData.of(modelData, 128, 128);
	}

	@Override
	public void setAngles(S bipedEntityRenderState) {

	}
}