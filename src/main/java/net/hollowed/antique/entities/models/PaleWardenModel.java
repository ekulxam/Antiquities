package net.hollowed.antique.entities.models;

import net.hollowed.antique.entities.animations.PaleWardenAnimations;
import net.hollowed.antique.entities.PaleWardenEntity;
import net.hollowed.antique.entities.renderer.PaleWardenRenderState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;

public class PaleWardenModel extends EntityModel<PaleWardenRenderState> implements ModelWithArms {
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
	public final Animation awaken;
	public final Animation idle;

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
		this.awaken = PaleWardenAnimations.awaken.createAnimation(root);
		this.idle = PaleWardenAnimations.idle.createAnimation(root);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData big_guy = modelPartData.addChild("big_guy", ModelPartBuilder.create(), ModelTransform.origin(19.0F, -16.25F, 1.0F));

		big_guy.addChild("right_leg", ModelPartBuilder.create().uv(30, 47).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 18.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(-21.0F, 22.25F, -1.0F));

		big_guy.addChild("left_leg", ModelPartBuilder.create().uv(46, 47).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 18.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(-17.0F, 22.25F, -1.0F));

		ModelPartData body = big_guy.addChild("body", ModelPartBuilder.create().uv(32, 20).cuboid(-5.0F, -9.0F, -2.0F, 10.0F, 11.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(-19.0F, 20.25F, -1.0F));

		body.addChild("cube_r1", ModelPartBuilder.create().uv(0, 0).cuboid(-11.0F, -4.0F, -6.0F, 16.0F, 10.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(3.0F, -13.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData right_arm = body.addChild("right_arm", ModelPartBuilder.create().uv(0, 48).cuboid(-4.0F, 0.0F, -3.0F, 4.0F, 12.0F, 5.0F, new Dilation(0.0F)), ModelTransform.origin(-8.0F, -15.0F, 0.5F));

		right_arm.addChild("cube_r2", ModelPartBuilder.create().uv(32, 35).cuboid(-4.0F, -5.0F, -3.5F, 8.0F, 5.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(-2.0F, 1.0F, -0.5F, 0.0F, 3.1416F, 0.0F));

		ModelPartData right_lower_arm = right_arm.addChild("right_lower_arm", ModelPartBuilder.create().uv(52, 0).cuboid(-2.0F, 0.0F, -2.5F, 4.0F, 12.0F, 5.0F, new Dilation(0.0F)), ModelTransform.origin(-2.0F, 12.0F, -0.5F));

		ModelPartData sword = right_lower_arm.addChild("sword", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 10.0F, 0.0F));

		sword.addChild("cube_r3", ModelPartBuilder.create().uv(3, 87).cuboid(-1.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, -1.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		ModelPartData left_arm = body.addChild("left_arm", ModelPartBuilder.create().uv(0, 36).cuboid(-2.0F, -4.0F, -4.0F, 8.0F, 5.0F, 7.0F, new Dilation(0.0F)), ModelTransform.origin(8.0F, -15.0F, 0.5F));

		left_arm.addChild("cube_r4", ModelPartBuilder.create().uv(62, 34).cuboid(-2.0F, -11.0F, -2.5F, 4.0F, 12.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, 11.0F, -0.5F, 0.0F, 3.1416F, 0.0F));

		ModelPartData left_lower_arm = left_arm.addChild("left_lower_arm", ModelPartBuilder.create(), ModelTransform.origin(2.0F, 12.0F, -0.5F));

		left_lower_arm.addChild("cube_r5", ModelPartBuilder.create().uv(60, 17).cuboid(-2.0F, -11.0F, -2.5F, 4.0F, 12.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 11.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		ModelPartData head = body.addChild("head", ModelPartBuilder.create().uv(18, 48).cuboid(-11.0F, -12.0F, -1.0F, 6.0F, 11.0F, 0.0F, new Dilation(0.0F))
		.uv(32, 69).cuboid(5.0F, -12.0F, -1.0F, 6.0F, 11.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -19.25F, 0.0F));

		head.addChild("cube_r6", ModelPartBuilder.create().uv(80, 32).cuboid(-4.5F, -4.5F, -4.5F, 9.0F, 9.0F, 9.0F, new Dilation(0.0F))
		.uv(78, 16).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.75F, -1.0F, 0.3622F, -0.7519F, -0.2533F));

		ModelPartData kilt = big_guy.addChild("kilt", ModelPartBuilder.create(), ModelTransform.origin(-19.0F, 20.25F, 1.0F));

		kilt.addChild("cube_r7", ModelPartBuilder.create().uv(16, 69).cuboid(-3.0F, 0.0F, -1.0F, 6.0F, 9.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

		kilt.addChild("cube_r8", ModelPartBuilder.create().uv(62, 51).cuboid(0.0F, 0.0F, -3.5F, 2.0F, 8.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-5.0F, 0.0F, -1.5F, 0.0F, 0.0F, 0.1745F));

		kilt.addChild("cube_r9", ModelPartBuilder.create().uv(62, 65).cuboid(-3.0F, 0.0F, -1.0F, 6.0F, 9.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -4.0F, -0.1745F, 0.0F, 0.0F));

		kilt.addChild("cube_r10", ModelPartBuilder.create().uv(0, 65).cuboid(-2.0F, 0.0F, -3.5F, 2.0F, 8.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(5.0F, 0.0F, -1.5F, 0.0F, 0.0F, -0.1745F));
		return TexturedModelData.of(modelData, 128, 128);
	}

	public void setAngles(PaleWardenRenderState state) {
		this.resetTransforms();
		PaleWardenEntity entity = (PaleWardenEntity) state.entity;
		this.setHeadAngle(state.relativeHeadYaw, state.pitch);
		this.awaken.apply(entity.awakenAnimationState, entity.age + MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(true), 1F);
		this.idle.apply(entity.idleAnimationState, entity.age + MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(true), 1F);
	}

	private void setHeadAngle(float yaw, float pitch) {
		this.head.pitch = pitch * 0.017453292F;
		this.head.yaw = yaw * 0.017453292F;
	}

	@SuppressWarnings("unused")
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		big_guy.render(matrices, vertexConsumer, light, overlay);
	}

	@Override
	public void setArmAngle(Arm arm, MatrixStack matrices) {
		this.root.applyTransform(matrices);
		if (arm == Arm.RIGHT) {
			this.right_arm.applyTransform(matrices);
			this.right_lower_arm.applyTransform(matrices);
			this.sword.applyTransform(matrices);
		} else {
			this.left_arm.applyTransform(matrices);
			this.left_lower_arm.applyTransform(matrices);
		}
	}
}