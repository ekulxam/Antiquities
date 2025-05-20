package net.hollowed.antique.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class AnimeEffect extends StatusEffect {

	public AnimeEffect() {
		super(StatusEffectCategory.NEUTRAL, 0xe9b8b3); // Replace with desired color
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return true;
	}
}
