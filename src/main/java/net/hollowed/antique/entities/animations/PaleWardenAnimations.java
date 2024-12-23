package net.hollowed.antique.entities.animations;

import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;

public class PaleWardenAnimations {
	public static final Animation idle = Animation.Builder.create(0.0417F).looping()
		.addBoneAnimation("right_arm", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("right_arm", new Transformation(Transformation.Targets.TRANSLATE, 
			new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, -0.5F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("right_lower_arm", new Transformation(Transformation.Targets.ROTATE, 
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, -45.0F, -90.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("left_arm", new Transformation(Transformation.Targets.ROTATE, 
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("left_arm", new Transformation(Transformation.Targets.TRANSLATE, 
			new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, -0.5F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("left_lower_arm", new Transformation(Transformation.Targets.ROTATE, 
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(-0.0024F, 45.0545F, 86.4633F), Transformation.Interpolations.LINEAR)
		))
		.build();

	public static final Animation awaken = Animation.Builder.create(3.0F)
		.addBoneAnimation("right_arm", new Transformation(Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3333F, AnimationHelper.createRotationalVector(-42.7342F, 15.6999F, 16.3249F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.6667F, AnimationHelper.createRotationalVector(-87.7342F, 15.6999F, 16.3249F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.8333F, AnimationHelper.createRotationalVector(-120.1553F, 0.2551F, 12.856F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.9167F, AnimationHelper.createRotationalVector(-120.1553F, 0.2551F, 12.856F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(-120.1553F, 0.2551F, 12.856F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.5F, AnimationHelper.createRotationalVector(-120.1553F, 0.2551F, 12.856F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.7083F, AnimationHelper.createRotationalVector(0.9798F, -7.0614F, 25.5217F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.7917F, AnimationHelper.createRotationalVector(8.4798F, -7.0614F, 25.5217F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.875F, AnimationHelper.createRotationalVector(0.9798F, -7.0614F, 25.5217F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("right_arm", new Transformation(Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, -0.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, -0.5F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("right_lower_arm", new Transformation(Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, -45.0F, -90.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, -45.0F, -90.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.3333F, AnimationHelper.createRotationalVector(0.0F, -67.5F, -67.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, -90.0F, -67.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5407F, AnimationHelper.createRotationalVector(0.0F, -90.0F, -67.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.5417F, AnimationHelper.createRotationalVector(-180.0F, -87.5F, -247.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.8333F, AnimationHelper.createRotationalVector(-160.882F, -26.5547F, -273.794F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.9167F, AnimationHelper.createRotationalVector(-165.7444F, -27.1796F, -274.004F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(-160.882F, -26.5547F, -273.794F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.5F, AnimationHelper.createRotationalVector(-164.6437F, -28.7812F, -265.7051F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.5833F, AnimationHelper.createRotationalVector(-68.4502F, -28.8797F, -276.484F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.7083F, AnimationHelper.createRotationalVector(-4.3617F, 2.5378F, -359.087F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.7917F, AnimationHelper.createRotationalVector(0.6383F, 2.5378F, -359.087F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.875F, AnimationHelper.createRotationalVector(-4.3617F, 2.5378F, -359.087F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("left_arm", new Transformation(Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.125F, AnimationHelper.createRotationalVector(-45.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.3333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.4167F, AnimationHelper.createRotationalVector(2.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("left_arm", new Transformation(Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, -0.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, -0.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.125F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, -0.5F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("left_lower_arm", new Transformation(Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(-0.0024F, 45.0545F, 86.4633F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(-0.0024F, 45.0545F, 86.4633F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.125F, AnimationHelper.createRotationalVector(-0.0024F, 45.0545F, 86.4633F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.3333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.4167F, AnimationHelper.createRotationalVector(2.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("body", new Transformation(Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.0833F, AnimationHelper.createRotationalVector(0.0F, 2.5F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.1667F, AnimationHelper.createRotationalVector(0.0F, -5.5F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(0.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.8333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.9167F, AnimationHelper.createRotationalVector(0.0F, -1.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.3333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.4167F, AnimationHelper.createRotationalVector(0.0F, 5.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("head", new Transformation(Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("sword", new Transformation(Transformation.Targets.ROTATE,
				new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createRotationalVector(-4.7693F, 2.9167F, 4.7693F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.5F, AnimationHelper.createRotationalVector(-4.7693F, 2.9167F, 4.7693F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.7083F, AnimationHelper.createRotationalVector(31.9576F, 9.7834F, 39.9487F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.7917F, AnimationHelper.createRotationalVector(36.9643F, 9.0513F, 41.0299F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.875F, AnimationHelper.createRotationalVector(31.9576F, 9.7834F, 39.9487F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("sword", new Transformation(Transformation.Targets.TRANSLATE,
				new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.0F, AnimationHelper.createTranslationalVector(-1.7F, 1.4F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.5F, AnimationHelper.createTranslationalVector(-1.7F, 1.4F, 0.0F), Transformation.Interpolations.LINEAR),
				new Keyframe(2.7083F, AnimationHelper.createTranslationalVector(0.3F, 0.4F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.build();
}