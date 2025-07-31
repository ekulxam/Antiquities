package net.hollowed.antique.items;

import net.hollowed.antique.entities.SmokeBombEntity;
import net.hollowed.antique.index.AntiqueEntities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

public class SmokeBombItem extends Item implements ProjectileItem {

	public SmokeBombItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		world.playSound(
			null,
			user.getX(),
			user.getY(),
			user.getZ(),
			SoundEvents.ENTITY_WITCH_THROW,
			SoundCategory.NEUTRAL,
			0.5F,
			1.0F
		);
		if (world instanceof ServerWorld serverWorld) {
			SmokeBombEntity smokeBomb = new SmokeBombEntity(AntiqueEntities.SMOKE_BOMB, serverWorld);
			smokeBomb.setPosition(user.getEyePos());
			smokeBomb.setVelocity(user, user.getPitch(), user.getYaw(), 0, 0.5F, 1.0F);
			serverWorld.spawnEntity(smokeBomb);
		}

		user.incrementStat(Stats.USED.getOrCreateStat(this));
		itemStack.decrementUnlessCreative(1, user);
		user.getItemCooldownManager().set(this.getDefaultStack(), 20);
		return ActionResult.SUCCESS;
	}

	@Override
	public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
		SmokeBombEntity smokeBomb = new SmokeBombEntity(AntiqueEntities.SMOKE_BOMB, world);
		smokeBomb.setPosition(pos.getX(), pos.getY(), pos.getZ());
		return smokeBomb;
	}
}
