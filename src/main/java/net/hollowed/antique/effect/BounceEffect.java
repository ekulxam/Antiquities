package net.hollowed.antique.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class BounceEffect extends MobEffect {

	public BounceEffect() {
		super(MobEffectCategory.NEUTRAL, 0xe9b8b3); // Replace with desired color
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true; // Update effect every tick
	}

	@Override
	public boolean applyEffectTick(@NotNull ServerLevel world, LivingEntity entity, int amplifier) {
		entity.fallDistance = 0;
		return true;
	}
}
