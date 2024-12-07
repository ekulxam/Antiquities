package net.hollowed.antique.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;

public class BounceEffect extends StatusEffect {

	public BounceEffect() {
		super(StatusEffectCategory.NEUTRAL, 0xe9b8b3); // Replace with desired color
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return true; // Update effect every tick
	}

	@Override
	public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
		entity.fallDistance = 0;
		return true;
	}
}
