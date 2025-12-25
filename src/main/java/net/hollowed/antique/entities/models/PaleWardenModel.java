package net.hollowed.antique.entities.models;

import net.hollowed.antique.entities.animations.PaleWardenAnimations;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.hollowed.antique.entities.PaleWardenEntity;
import net.hollowed.antique.entities.renderer.PaleWardenRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.HumanoidArm;

public class PaleWardenModel extends EntityModel<PaleWardenRenderState> implements ArmedModel<PaleWardenRenderState> {
	public final ModelPart big_guy;
	public final ModelPart right_leg;
	public final ModelPart left_leg;
	public final ModelPart body;
	public final ModelPart right_arm;
	public final ModelPart right_lower_arm;
	public final ModelPart sword;
	public final ModelPart left_arm;
	public final ModelPart left_lower_arm;
	public final ModelPart head;
	public final ModelPart kilt;
	public final KeyframeAnimation awaken;
	public final KeyframeAnimation idle;

	public PaleWardenModel(ModelPart root) {
        super(root);
        this.big_guy = root.getChild("big_guy");
		this.right_leg = this.big_guy.getChild("right_leg");
		this.left_leg = this.big_guy.getChild("left_leg");
		this.body = this.big_guy.getChild("body");
		this.right_arm = this.body.getChild("right_arm");
		this.right_lower_arm = this.right_arm.getChild("right_lower_arm");
		this.sword = this.right_lower_arm.getChild("sword");
		this.left_arm = this.body.getChild("left_arm");
		this.left_lower_arm = this.left_arm.getChild("left_lower_arm");
		this.head = this.body.getChild("head");
		this.kilt = this.big_guy.getChild("kilt");
		this.awaken = PaleWardenAnimations.awaken.bake(root);
		this.idle = PaleWardenAnimations.idle.bake(root);
	}

	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition big_guy = modelPartData.addOrReplaceChild("big_guy", CubeListBuilder.create(), PartPose.offset(19.0F, -16.25F, 1.0F));

		big_guy.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(30, 47).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 18.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-21.0F, 22.25F, -1.0F));

		big_guy.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(46, 47).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 18.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-17.0F, 22.25F, -1.0F));

		PartDefinition body = big_guy.addOrReplaceChild("body", CubeListBuilder.create().texOffs(32, 20).addBox(-5.0F, -9.0F, -2.0F, 10.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-19.0F, 20.25F, -1.0F));

		body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-11.0F, -4.0F, -6.0F, 16.0F, 10.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -13.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 48).addBox(-4.0F, 0.0F, -3.0F, 4.0F, 12.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, -15.0F, 0.5F));

		right_arm.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(32, 35).addBox(-4.0F, -5.0F, -3.5F, 8.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 1.0F, -0.5F, 0.0F, 3.1416F, 0.0F));

		PartDefinition right_lower_arm = right_arm.addOrReplaceChild("right_lower_arm", CubeListBuilder.create().texOffs(52, 0).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 12.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 12.0F, -0.5F));

		PartDefinition sword = right_lower_arm.addOrReplaceChild("sword", CubeListBuilder.create(), PartPose.offset(0.0F, 10.0F, 0.0F));

		sword.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(3, 87).addBox(-1.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -1.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 36).addBox(-2.0F, -4.0F, -4.0F, 8.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, -15.0F, 0.5F));

		left_arm.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(62, 34).addBox(-2.0F, -11.0F, -2.5F, 4.0F, 12.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 11.0F, -0.5F, 0.0F, 3.1416F, 0.0F));

		PartDefinition left_lower_arm = left_arm.addOrReplaceChild("left_lower_arm", CubeListBuilder.create(), PartPose.offset(2.0F, 12.0F, -0.5F));

		left_lower_arm.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(60, 17).addBox(-2.0F, -11.0F, -2.5F, 4.0F, 12.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 11.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(18, 48).addBox(-11.0F, -12.0F, -1.0F, 6.0F, 11.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(32, 69).addBox(5.0F, -12.0F, -1.0F, 6.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -19.25F, 0.0F));

		head.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(80, 32).addBox(-4.5F, -4.5F, -4.5F, 9.0F, 9.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(78, 16).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.75F, -1.0F, 0.3622F, -0.7519F, -0.2533F));

		PartDefinition kilt = big_guy.addOrReplaceChild("kilt", CubeListBuilder.create(), PartPose.offset(-19.0F, 20.25F, 1.0F));

		kilt.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(16, 69).addBox(-3.0F, 0.0F, -1.0F, 6.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

		kilt.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(62, 51).addBox(0.0F, 0.0F, -3.5F, 2.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 0.0F, -1.5F, 0.0F, 0.0F, 0.1745F));

		kilt.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(62, 65).addBox(-3.0F, 0.0F, -1.0F, 6.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, -0.1745F, 0.0F, 0.0F));

		kilt.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(0, 65).addBox(-2.0F, 0.0F, -3.5F, 2.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 0.0F, -1.5F, 0.0F, 0.0F, -0.1745F));
		return LayerDefinition.create(modelData, 128, 128);
	}

	public void setAngles(PaleWardenRenderState state) {
		this.resetPose();
		PaleWardenEntity entity = (PaleWardenEntity) state.entity;
		this.setHeadAngle(state.yRot, state.xRot);
		this.awaken.apply(entity.awakenAnimationState, entity.tickCount + Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true), 1F);
		this.idle.apply(entity.idleAnimationState, entity.tickCount + Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true), 1F);
	}

	private void setHeadAngle(float yaw, float pitch) {
		this.head.xRot = pitch * 0.017453292F;
		this.head.yRot = yaw * 0.017453292F;
	}

	@SuppressWarnings("unused")
	public void render(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		big_guy.render(matrices, vertexConsumer, light, overlay);
	}

	@Override
	public void translateToHand(PaleWardenRenderState state, HumanoidArm arm, PoseStack matrices) {
		this.root.translateAndRotate(matrices);
		if (arm == HumanoidArm.RIGHT) {
			this.right_arm.translateAndRotate(matrices);
			this.right_lower_arm.translateAndRotate(matrices);
			this.sword.translateAndRotate(matrices);
		} else {
			this.left_arm.translateAndRotate(matrices);
			this.left_lower_arm.translateAndRotate(matrices);
		}
	}
}