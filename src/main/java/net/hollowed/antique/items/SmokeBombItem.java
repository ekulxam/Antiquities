package net.hollowed.antique.items;

import net.hollowed.antique.entities.SmokeBombEntity;
import net.hollowed.antique.index.AntiqueEntities;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;

public class SmokeBombItem extends Item implements ProjectileItem {

	public SmokeBombItem(Item.Properties settings) {
		super(settings);
	}

	@Override
	public InteractionResult use(Level world, Player user, InteractionHand hand) {
		ItemStack itemStack = user.getItemInHand(hand);
		world.playSound(
			null,
			user.getX(),
			user.getY(),
			user.getZ(),
			SoundEvents.WITCH_THROW,
			SoundSource.NEUTRAL,
			0.5F,
			1.0F
		);
		if (world instanceof ServerLevel serverWorld) {
			SmokeBombEntity smokeBomb = new SmokeBombEntity(AntiqueEntities.SMOKE_BOMB, serverWorld);
			smokeBomb.setItem(itemStack);
			smokeBomb.setPos(user.getEyePosition());
			smokeBomb.shootFromRotation(user, user.getXRot(), user.getYRot(), 0, 0.5F, 1.0F);
			serverWorld.addFreshEntity(smokeBomb);
		}

		user.awardStat(Stats.ITEM_USED.get(this));
		itemStack.consume(1, user);
		user.getCooldowns().addCooldown(this.getDefaultInstance(), 20);
		return InteractionResult.SUCCESS;
	}

	@Override
	public Projectile asProjectile(Level world, Position pos, ItemStack stack, Direction direction) {
		SmokeBombEntity smokeBomb = new SmokeBombEntity(AntiqueEntities.SMOKE_BOMB, world);
		smokeBomb.setPos(pos.x(), pos.y(), pos.z());
		return smokeBomb;
	}
}
