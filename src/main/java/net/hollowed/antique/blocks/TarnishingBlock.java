package net.hollowed.antique.blocks;

import net.hollowed.antique.index.AntiqueBlocks;
import net.hollowed.antique.util.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import java.util.Optional;

public class TarnishingBlock extends Block {

	public TarnishingBlock(Properties settings) {
		super(settings);
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (stack.is(Items.MAGMA_CREAM)) {
			Optional<BlockState> optional = this.getCoat(state);
			if (optional.isPresent()) {
				world.setBlock(pos, optional.get(), Block.UPDATE_ALL_IMMEDIATE);
				world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(null, optional.get()));
				world.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
				world.levelEvent(player, LevelEvent.PARTICLES_AND_SOUND_WAX_ON, pos, 0);
				stack.consume(1, player);
				return InteractionResult.SUCCESS;
			}
		}
		return super.useItemOn(stack, state, world, pos, player, hand, hit);
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		if (world.dimensionTypeRegistration().is(BuiltinDimensionTypes.NETHER)) {
			Optional<BlockState> optional = this.getNextTarnishLevel(state);
			if (optional.isPresent()) {
				world.setBlock(pos, optional.get(), Block.UPDATE_ALL_IMMEDIATE);
				world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(null, optional.get()));
			}
		}
	}

	@Override
	protected boolean isRandomlyTicking(BlockState state) {
		return !state.is(AntiqueBlocks.TARNISHED_MYRIAD_BLOCK);
	}

	private Optional<BlockState> getNextTarnishLevel(BlockState state) {
		return Optional.ofNullable(BlockUtil.TARNISHING_BLOCKS.get(state.getBlock()))
				.map(Block::defaultBlockState);
	}

	private Optional<BlockState> getCoat(BlockState state) {
		return Optional.ofNullable(BlockUtil.COATED_MYRIAD_BLOCKS.get(state.getBlock()))
				.map(Block::defaultBlockState);
	}
}
