package net.hollowed.antique.client.armor.models;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;

public class ArmorStandAdventureArmor extends BipedEntityModel<ArmorStandEntityRenderState> {

	public final ModelPart satchel;
	public final ModelPart leftArmThick;
	public final ModelPart rightArmThick;

	public ArmorStandAdventureArmor(ModelPart root) {
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

		modelPartData.addChild("rightArmThick", ModelPartBuilder.create().uv(40, 16).cuboid(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new Dilation(0.0F))
				.uv(40, 32).cuboid(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new Dilation(0.25F)), ModelTransform.pivot(-5.0F, 2.0F, 0.0F));

		modelPartData.addChild("leftArmThick", ModelPartBuilder.create().uv(32, 48).cuboid(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new Dilation(0.0F))
				.uv(48, 48).cuboid(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new Dilation(0.25F)), ModelTransform.pivot(5.0F, 2.0F, 0.0F));

		modelPartData.addChild("satchel")
				.addChild("satchel",
						ModelPartBuilder.create()
								.uv(40, 0)
								.cuboid(3.0F, -0.5F, -2.0F, 2.0F, 5.0F, 4.0F, new Dilation(0.26F))
								.uv(0, 43)
								.cuboid(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.26F)),
						ModelTransform.pivot(0.0F, 12.0F, 0.0F)
				);

		modelPartData.addChild("body")
				.addChild("pauldrons",
						ModelPartBuilder.create()
								.uv(22, 16)
								.cuboid(6.0F, -2.0F, -1.0F, 3.0F, 3.0F, 8.0F, new Dilation(0.0F))
								.uv(40, 9)
								.cuboid(2.0F, -2.0F, 4.0F, 4.0F, 3.0F, 3.0F, new Dilation(0.0F))
								.uv(0, 16)
								.cuboid(-1.0F, -2.0F, -1.0F, 3.0F, 3.0F, 8.0F, new Dilation(0.0F))
								.uv(0, 0)
								.cuboid(0.0F, -1.0F, 0.25F, 8.0F, 12.0F, 4.0F, new Dilation(0.5F)),
						ModelTransform.pivot(-4.0F, 1.0F, -2.25F)
				);

		modelPartData.addChild("left_arm")
				.addChild("leftArm",
						ModelPartBuilder.create()
								.uv(30, 27)
								.cuboid(-16.0F, -13.0F, -4.25F, 3.0F, 12.0F, 4.0F, new Dilation(0.26F)),
						ModelTransform.pivot(15.0F, 11.0F, 2.25F)
				);

		modelPartData.addChild("right_arm")
				.addChild("rightArm",
						ModelPartBuilder.create()
								.uv(16, 27)
								.cuboid(-6.0F, -13.0F, -4.25F, 3.0F, 12.0F, 4.0F, new Dilation(0.26F)),
						ModelTransform.pivot(4.0F, 11.0F, 2.25F)
				);

		modelPartData.addChild("left_leg")
				.addChild("leftLeg",
						ModelPartBuilder.create()
								.uv(24, 0)
								.cuboid(-10.0F, -12.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.4F)),
						ModelTransform.pivot(8.0F, 12.0F, 0.0F)
				);

		modelPartData.addChild("right_leg")
				.addChild("rightLeg",
						ModelPartBuilder.create()
								.uv(0, 27)
								.cuboid(-6.0F, -12.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.4F)),
						ModelTransform.pivot(4.0F, 12.0F, 0.0F)
				);

		return TexturedModelData.of(modelData, 64, 64);
	}
}
