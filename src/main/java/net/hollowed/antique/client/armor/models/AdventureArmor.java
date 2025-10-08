package net.hollowed.antique.client.armor.models;

import net.hollowed.antique.index.AntiqueItems;
import net.hollowed.antique.mixin.accessors.TexturedModelDataDataAccessor;
import net.hollowed.antique.util.interfaces.duck.IsHuskGetter;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.entity.state.*;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;

public class AdventureArmor<S extends BipedEntityRenderState> extends BipedEntityModel<S> {
	public final ModelPart satchel;
	public final ModelPart rightArmArmorThick;
	public final ModelPart leftArmArmorThick;
	public final ModelPart rightArmArmor;
	public final ModelPart leftArmArmor;
	public final ModelPart realBody;
	public final ModelPart rightBoot;
	public final ModelPart leftBoot;

	public final ModelPart rightBodyStick;
	public final ModelPart leftBodyStick;
	public final ModelPart shoulderStick;
	public final ModelPart basePlate;

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

		this.rightBodyStick = root.getChild("right_body_stick");
		this.leftBodyStick = root.getChild("left_body_stick");
		this.shoulderStick = root.getChild("shoulder_stick");
		this.basePlate = root.getChild("base_plate");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = ((TexturedModelDataDataAccessor) ArmorStandEntityModel.getTexturedModelData()).getData();
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
		super.setAngles(state);

		if (state instanceof ZombieEntityRenderState zombieEntityRenderState) {
			if (state instanceof IsHuskGetter access && access.antiquities$getHusk()) {
				this.root.moveOrigin(new Vector3f(0, -1.5F, 0));
				this.root.scale(new Vector3f(0.05F, 0.05F, 0.05F));
			}
			float f = zombieEntityRenderState.handSwingProgress;
			ArmPosing.zombieArms(this.leftArm, this.rightArm, zombieEntityRenderState.attacking, f, zombieEntityRenderState.age);
		}

		if (state instanceof SkeletonEntityRenderState skeletonEntityRenderState) {
			if (skeletonEntityRenderState.attacking && !skeletonEntityRenderState.holdingBow) {
				float f = skeletonEntityRenderState.handSwingProgress;
				float g = MathHelper.sin(f * (float) Math.PI);
				float h = MathHelper.sin((1.0F - (1.0F - f) * (1.0F - f)) * (float) Math.PI);
				this.rightArm.roll = 0.0F;
				this.leftArm.roll = 0.0F;
				this.rightArm.yaw = -(0.1F - g * 0.6F);
				this.leftArm.yaw = 0.1F - g * 0.6F;
				this.rightArm.pitch = (float) (-Math.PI / 2);
				this.leftArm.pitch = (float) (-Math.PI / 2);
				this.rightArm.pitch -= g * 1.2F - h * 0.4F;
				this.leftArm.pitch -= g * 1.2F - h * 0.4F;
				ArmPosing.swingArms(this.rightArm, this.leftArm, skeletonEntityRenderState.age);
			}
		}

		if (state instanceof ArmorStandEntityRenderState armorStandEntityRenderState) {
			EulerAngle leftAngle = armorStandEntityRenderState.leftArmRotation;
			EulerAngle rightAngle = armorStandEntityRenderState.rightArmRotation;

			this.leftArm.setAngles((float) Math.toRadians(leftAngle.pitch()), (float) Math.toRadians(leftAngle.yaw()), (float) Math.toRadians(leftAngle.roll()));
			this.rightArm.setAngles((float) Math.toRadians(rightAngle.pitch()), (float) Math.toRadians(rightAngle.yaw()), (float) Math.toRadians(rightAngle.roll()));
		}

		rightBodyStick.visible = false;
		leftBodyStick.visible = false;
		shoulderStick.visible = false;
		basePlate.visible = false;

		boolean slim = state instanceof PlayerEntityRenderState playerState && playerState.skinTextures.model() == PlayerSkinType.SLIM || state instanceof SkeletonEntityRenderState;
		rightArmArmorThick.visible = leftArmArmorThick.visible = !slim;
		rightArmArmor.visible = leftArmArmor.visible = slim;

		realBody.visible = rightArm.visible = leftArm.visible = state.equippedChestStack.isOf(AntiqueItems.NETHERITE_PAULDRONS);
		satchel.visible = state.equippedLegsStack.isOf(AntiqueItems.SATCHEL);
		rightBoot.visible = leftBoot.visible = state.equippedFeetStack.isOf(AntiqueItems.FUR_BOOTS);
	}
}