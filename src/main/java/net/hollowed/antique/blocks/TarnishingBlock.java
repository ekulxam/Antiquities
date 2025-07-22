package net.hollowed.antique.blocks;

import net.hollowed.antique.index.AntiqueBlocks;
import net.hollowed.antique.util.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

import java.util.Optional;

public class TarnishingBlock extends Block {

	public TarnishingBlock(Settings settings) {
		super(settings);
	}

	@Override
	protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (stack.isOf(Items.MAGMA_CREAM)) {
			Optional<BlockState> optional = this.getCoat(state);
			if (optional.isPresent()) {
				world.setBlockState(pos, optional.get(), Block.NOTIFY_ALL_AND_REDRAW);
				world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(null, optional.get()));
				world.playSound(player, pos, SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS, 1.0F, 1.0F);
				world.syncWorldEvent(player, WorldEvents.BLOCK_WAXED, pos, 0);
				stack.decrementUnlessCreative(1, player);
				return ActionResult.SUCCESS;
			}
		}
		return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
	}

	@Override
	protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (world.getDimension().ultrawarm()) {
			Optional<BlockState> optional = this.getNextTarnishLevel(state);
			if (optional.isPresent()) {
				world.setBlockState(pos, optional.get(), Block.NOTIFY_ALL_AND_REDRAW);
				world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(null, optional.get()));
			}
		}
	}

	@Override
	protected boolean hasRandomTicks(BlockState state) {
		return !state.isOf(AntiqueBlocks.TARNISHED_MYRIAD_BLOCK);
	}

	private Optional<BlockState> getNextTarnishLevel(BlockState state) {
		return Optional.ofNullable(BlockUtil.TARNISHING_BLOCKS.get(state.getBlock()))
				.map(Block::getDefaultState);
	}

	private Optional<BlockState> getCoat(BlockState state) {
		return Optional.ofNullable(BlockUtil.COATED_MYRIAD_BLOCKS.get(state.getBlock()))
				.map(Block::getDefaultState);
	}
}
