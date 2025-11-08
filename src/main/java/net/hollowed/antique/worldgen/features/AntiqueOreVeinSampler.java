package net.hollowed.antique.worldgen.features;

import net.hollowed.antique.index.AntiqueBlocks;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.densityfunction.DensityFunction;

public final class AntiqueOreVeinSampler {

	private static final float DENSITY_THRESHOLD = 0.4F;
	private static final int MAX_DENSITY_INTRUSION = 20;
	private static final double LIMINAL_DENSITY_REDUCTION = -0.2;
	private static final float BLOCK_GENERATION_CHANCE = 0.7F;
	private static final float MIN_ORE_CHANCE = 0.1F;
	private static final float MAX_ORE_CHANCE = 0.3F;
	private static final float DENSITY_FOR_MAX_ORE_CHANCE = 0.6F;
	private static final float RAW_ORE_BLOCK_CHANCE = 0.02F;
	private static final float VEIN_GAP_THRESHOLD = -0.3F;

	private AntiqueOreVeinSampler() {

	}

	public static ChunkNoiseSampler.BlockStateSampler create(
		DensityFunction veinToggle, DensityFunction veinRidged, DensityFunction veinGap, RandomSplitter randomDeriver
	) {
		BlockState blockState = SharedConstants.ORE_VEINS ? Blocks.AIR.getDefaultState() : null;
		return pos -> {
			double d = veinToggle.sample(pos);
			int i = pos.blockY();
			AntiqueOreVeinSampler.VeinType veinType = VeinType.MYRIAD;
			double e = Math.abs(d);
			int j = veinType.maxY - i;
			int k = i - veinType.minY;
			if (k >= 0 && j >= 0) {
				int l = Math.min(j, k);
				double f = MathHelper.clampedMap(l, 0.0, MAX_DENSITY_INTRUSION, LIMINAL_DENSITY_REDUCTION, 0.0);
				if (e + f < DENSITY_THRESHOLD) {
					return blockState;
				} else {
					Random random = randomDeriver.split(pos.blockX(), i, pos.blockZ());
					if (random.nextFloat() > BLOCK_GENERATION_CHANCE) {
						return blockState;
					} else if (veinRidged.sample(pos) >= 0.0) {
						return blockState;
					} else {
						double g = MathHelper.clampedMap(e, DENSITY_THRESHOLD, DENSITY_FOR_MAX_ORE_CHANCE, MIN_ORE_CHANCE, MAX_ORE_CHANCE);
						if (random.nextFloat() < g && veinGap.sample(pos) > VEIN_GAP_THRESHOLD) {
							return random.nextFloat() < RAW_ORE_BLOCK_CHANCE ? veinType.rawOreBlock : i > 0 ? veinType.ore : veinType.deepslateOre;
						} else {
							return SharedConstants.ORE_VEINS ? Blocks.OAK_BUTTON.getDefaultState() : i > 0 ? random.nextFloat() < 0.3 ? veinType.stoneExtra : veinType.stone : random.nextFloat() < 0.3 ? veinType.deepslateStoneExtra : veinType.deepslateStone;
						}
					}
				}
			} else {
				return blockState;
			}
		};
	}

	protected enum VeinType {
		MYRIAD(AntiqueBlocks.MYRIAD_ORE.getDefaultState(), AntiqueBlocks.DEEPSLATE_MYRIAD_ORE.getDefaultState(), AntiqueBlocks.RAW_MYRIAD_BLOCK.getDefaultState(), Blocks.ANDESITE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), Blocks.TUFF.getDefaultState(), Blocks.BASALT.getDefaultState(), -35, 45);

		final BlockState ore;
		final BlockState deepslateOre;
		final BlockState rawOreBlock;
		final BlockState stone;
		final BlockState stoneExtra;
		final BlockState deepslateStone;
		final BlockState deepslateStoneExtra;
		protected final int minY;
		protected final int maxY;

		VeinType(final BlockState ore, final BlockState deepslateOre, final BlockState rawOreBlock, final BlockState stone, final BlockState stoneExtra, final BlockState deepslateStone, final BlockState deepslateStoneExtra, final int minY, final int maxY) {
			this.ore = ore;
			this.deepslateOre = deepslateOre;
			this.rawOreBlock = rawOreBlock;
			this.stone = stone;
			this.stoneExtra = stoneExtra;
			this.deepslateStone = deepslateStone;
			this.deepslateStoneExtra = deepslateStoneExtra;
			this.minY = minY;
			this.maxY = maxY;
		}
	}
}
