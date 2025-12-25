package net.hollowed.antique.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class AnimeEffect extends MobEffect {

	public AnimeEffect() {
		super(MobEffectCategory.NEUTRAL, 0xe9b8b3); // Replace with desired color
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}
}
