package net.hollowed.antique.entities.animations;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

public class PaleWardenAnimations {
	public static final AnimationDefinition idle = AnimationDefinition.Builder.withLength(0.0417F).looping()
		.addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(-45.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.POSITION,
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -1.0F, -0.5F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("right_lower_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, -45.0F, -90.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("left_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(-45.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("left_arm", new AnimationChannel(AnimationChannel.Targets.POSITION,
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -1.0F, -0.5F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("left_lower_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(-0.0024F, 45.0545F, 86.4633F), AnimationChannel.Interpolations.LINEAR)
		))
		.build();

	public static final AnimationDefinition awaken = AnimationDefinition.Builder.withLength(3.0F)
		.addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(-45.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.0F, KeyframeAnimations.degreeVec(-45.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.3333F, KeyframeAnimations.degreeVec(-42.7342F, 15.6999F, 16.3249F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.6667F, KeyframeAnimations.degreeVec(-87.7342F, 15.6999F, 16.3249F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.8333F, KeyframeAnimations.degreeVec(-120.1553F, 0.2551F, 12.856F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.9167F, KeyframeAnimations.degreeVec(-120.1553F, 0.2551F, 12.856F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.0F, KeyframeAnimations.degreeVec(-120.1553F, 0.2551F, 12.856F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.5F, KeyframeAnimations.degreeVec(-120.1553F, 0.2551F, 12.856F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.7083F, KeyframeAnimations.degreeVec(0.9798F, -7.0614F, 25.5217F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.7917F, KeyframeAnimations.degreeVec(8.4798F, -7.0614F, 25.5217F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.875F, KeyframeAnimations.degreeVec(0.9798F, -7.0614F, 25.5217F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -1.0F, -0.5F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F, -1.0F, -0.5F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("right_lower_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, -45.0F, -90.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, -45.0F, -90.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.3333F, KeyframeAnimations.degreeVec(0.0F, -67.5F, -67.5F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, -90.0F, -67.5F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.5407F, KeyframeAnimations.degreeVec(0.0F, -90.0F, -67.5F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.5417F, KeyframeAnimations.degreeVec(-180.0F, -87.5F, -247.5F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.8333F, KeyframeAnimations.degreeVec(-160.882F, -26.5547F, -273.794F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.9167F, KeyframeAnimations.degreeVec(-165.7444F, -27.1796F, -274.004F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.0F, KeyframeAnimations.degreeVec(-160.882F, -26.5547F, -273.794F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.5F, KeyframeAnimations.degreeVec(-164.6437F, -28.7812F, -265.7051F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.5833F, KeyframeAnimations.degreeVec(-68.4502F, -28.8797F, -276.484F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.7083F, KeyframeAnimations.degreeVec(-4.3617F, 2.5378F, -359.087F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.7917F, KeyframeAnimations.degreeVec(0.6383F, 2.5378F, -359.087F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.875F, KeyframeAnimations.degreeVec(-4.3617F, 2.5378F, -359.087F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("left_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(-45.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.0F, KeyframeAnimations.degreeVec(-45.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.125F, KeyframeAnimations.degreeVec(-45.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.3333F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.4167F, KeyframeAnimations.degreeVec(2.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("left_arm", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -1.0F, -0.5F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F, -1.0F, -0.5F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.125F, KeyframeAnimations.posVec(0.0F, -1.0F, -0.5F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.3333F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("left_lower_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(-0.0024F, 45.0545F, 86.4633F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.0F, KeyframeAnimations.degreeVec(-0.0024F, 45.0545F, 86.4633F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.125F, KeyframeAnimations.degreeVec(-0.0024F, 45.0545F, 86.4633F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.3333F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.4167F, KeyframeAnimations.degreeVec(2.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(0.0833F, KeyframeAnimations.degreeVec(0.0F, 2.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(0.1667F, KeyframeAnimations.degreeVec(0.0F, -5.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.8333F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.9167F, KeyframeAnimations.degreeVec(0.0F, -1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.3333F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.4167F, KeyframeAnimations.degreeVec(0.0F, 5.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("sword", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.0F, KeyframeAnimations.degreeVec(-4.7693F, 2.9167F, 4.7693F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.5F, KeyframeAnimations.degreeVec(-4.7693F, 2.9167F, 4.7693F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.7083F, KeyframeAnimations.degreeVec(31.9576F, 9.7834F, 39.9487F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.7917F, KeyframeAnimations.degreeVec(36.9643F, 9.0513F, 41.0299F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.875F, KeyframeAnimations.degreeVec(31.9576F, 9.7834F, 39.9487F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("sword", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.0F, KeyframeAnimations.posVec(-1.7F, 1.4F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.5F, KeyframeAnimations.posVec(-1.7F, 1.4F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.7083F, KeyframeAnimations.posVec(0.3F, 0.4F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.build();
}