package net.hollowed.antique.client.armor.models;

import net.hollowed.antique.index.AntiqueItems;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.entity.state.*;
import net.minecraft.entity.player.PlayerSkinType;

public class AdventureArmor<S extends BipedEntityRenderState> extends BipedEntityModel<S> {
	public final ModelPart satchel;
	public final ModelPart rightArmArmorThick;
	public final ModelPart leftArmArmorThick;
	public final ModelPart rightArmArmor;
	public final ModelPart leftArmArmor;
	public final ModelPart realBody;
	public final ModelPart rightBoot;
	public final ModelPart leftBoot;

	public AdventureArmor(ModelPart root) {
		super(root);
		this.realBody = root.getChild(EntityModelPartNames.BODY).getChild("realBody");
		this.satchel = root.getChild(EntityModelPartNames.BODY).getChild("satchel");
		this.rightArmArmor = root.getChild(EntityModelPartNames.RIGHT_ARM).getChild("rightArmArmor");
		this.rightArmArmorThick = root.getChild(EntityModelPartNames.RIGHT_ARM).getChild("rightArmArmorThick");
		this.leftArmArmor = root.getChild(EntityModelPartNames.LEFT_ARM).getChild("leftArmArmor");
		this.leftArmArmorThick = root.getChild(EntityModelPartNames.LEFT_ARM).getChild("leftArmArmorThick");
		this.rightBoot = root.getChild(EntityModelPartNames.RIGHT_LEG).getChild("rightBoot");
		this.leftBoot = root.getChild(EntityModelPartNames.LEFT_LEG).getChild("leftBoot");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		ModelPartData head = modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create(), ModelTransform.NONE);
		head.addChild(EntityModelPartNames.HAT, ModelPartBuilder.create(), ModelTransform.NONE);

		ModelPartData body = modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create(), ModelTransform.NONE);
		ModelPartData rightArm = modelPartData.addChild(EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create(), ModelTransform.origin(-5, 2, 0));
		ModelPartData leftArm = modelPartData.addChild(EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create(), ModelTransform.origin(5, 2, 0));
		ModelPartData rightLeg = modelPartData.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create(), ModelTransform.origin(-1.9F, 12, 0));
		ModelPartData leftLeg = modelPartData.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create(), ModelTransform.origin(1.9F, 12, 0));

		body.addChild("realBody", ModelPartBuilder.create().uv(0, 16).cuboid(-4.0F, -24.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.27F))
				.uv(24, 0).cuboid(2.0F, -26.0F, -3.0F, 3.0F, 3.0F, 8.0F, new Dilation(0.0F))
				.uv(24, 11).cuboid(-5.0F, -26.0F, -3.0F, 3.0F, 3.0F, 8.0F, new Dilation(0.0F))
				.uv(46, 16).cuboid(-2.0F, -26.0F, 2.0F, 4.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.origin(0, 24, 0));

		body.addChild("satchel", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -24.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.32F))
				.uv(54, 22).cuboid(-6.0F, -13.0F, -2.0F, 2.0F, 5.0F, 4.0F, new Dilation(0.15F)), ModelTransform.origin(0, 24, 0));

		rightArm.addChild("rightArmArmor", ModelPartBuilder.create().uv(14, 54).cuboid(-6.0F, -24.0F, -2.0F, 3.0F, 12.0F, 4.0F, new Dilation(0.8F))
				.uv(0, 84).cuboid(-6.0F, -24.0F, -2.0F, 3.0F, 12.0F, 4.0F, new Dilation(0.99F))
				.uv(48, 38).cuboid(-6.0F, -24.0F, -2.0F, 3.0F, 12.0F, 4.0F, new Dilation(0.47F)), ModelTransform.origin(4, 22, 0));

		leftArm.addChild("leftArmArmor", ModelPartBuilder.create().uv(46, 0).cuboid(3.0F, -24.0F, -2.0F, 3.0F, 12.0F, 4.0F, new Dilation(0.27F))
				.uv(0, 48).cuboid(3.0F, -24.0F, -2.0F, 3.0F, 12.0F, 4.0F, new Dilation(0.47F)), ModelTransform.origin(-4, 22, 0));

		rightArm.addChild("rightArmArmorThick", ModelPartBuilder.create().uv(14, 54).cuboid(-7F, -24.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.8F))
				.uv(26, 96).cuboid(-7F, -24.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.99F))
				.uv(48, 38).cuboid(-7F, -24.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.47F)), ModelTransform.origin(4, 22, 0));

		leftArm.addChild("leftArmArmorThick", ModelPartBuilder.create().uv(46, 0).cuboid(3.0F, -24.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.27F))
				.uv(0, 83).cuboid(3.0F, -24.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.47F)), ModelTransform.origin(-4, 22, 0));

		leftLeg.addChild("leftBoot", ModelPartBuilder.create().uv(32, 38).mirrored().cuboid(-4.0F, -12.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.27F)).mirrored(false)
				.uv(41, 66).mirrored().cuboid(-4.0F, -12.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.47F)).mirrored(false)
				.uv(14, 79).mirrored().cuboid(-4.0F, -2.0F, -4.0F, 4.0F, 2.0F, 2.0F, new Dilation(0.27F)).mirrored(false), ModelTransform.origin(2.0F, 12.0F, 0.0F));

		rightLeg.addChild("rightBoot", ModelPartBuilder.create().uv(32, 38).cuboid(0.0F, -12.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.27F))
				.uv(41, 66).cuboid(0.0F, -12.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.47F))
				.uv(14, 79).cuboid(0.0F, -2.0F, -4.0F, 4.0F, 2.0F, 2.0F, new Dilation(0.27F)), ModelTransform.origin(-2.0F, 12.0F, 0.0F));
		return TexturedModelData.of(modelData, 128, 128);
	}

	@Override
	public void setAngles(S state) {
		boolean slim = state instanceof PlayerEntityRenderState playerState && playerState.skinTextures.model() == PlayerSkinType.SLIM || state instanceof SkeletonEntityRenderState;
		rightArmArmorThick.visible = leftArmArmorThick.visible = !slim;
		rightArmArmor.visible = leftArmArmor.visible = slim;

		realBody.visible = rightArm.visible = leftArm.visible = state.equippedChestStack.isOf(AntiqueItems.NETHERITE_PAULDRONS);
		satchel.visible = state.equippedLegsStack.isOf(AntiqueItems.SATCHEL);
		rightBoot.visible = leftBoot.visible = state.equippedFeetStack.isOf(AntiqueItems.FUR_BOOTS);
	}
}