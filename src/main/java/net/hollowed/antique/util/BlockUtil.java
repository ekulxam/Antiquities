package net.hollowed.antique.util;

import com.google.common.collect.ImmutableMap;
import net.hollowed.antique.index.AntiqueBlocks;
import net.minecraft.block.Block;

import java.util.Map;

public class BlockUtil {

    public static final Map<Block, Block> TARNISHING_BLOCKS = new ImmutableMap.Builder<Block, Block>()
            .put(AntiqueBlocks.MYRIAD_BLOCK, AntiqueBlocks.EXPOSED_MYRIAD_BLOCK)
            .put(AntiqueBlocks.EXPOSED_MYRIAD_BLOCK, AntiqueBlocks.WEATHERED_MYRIAD_BLOCK)
            .put(AntiqueBlocks.WEATHERED_MYRIAD_BLOCK, AntiqueBlocks.TARNISHED_MYRIAD_BLOCK)
            .build();

    public static final Map<Block, Block> TARNISHING_BLOCKS_REVERSE = new ImmutableMap.Builder<Block, Block>()
            .put(AntiqueBlocks.TARNISHED_MYRIAD_BLOCK, AntiqueBlocks.WEATHERED_MYRIAD_BLOCK)
            .put(AntiqueBlocks.WEATHERED_MYRIAD_BLOCK, AntiqueBlocks.EXPOSED_MYRIAD_BLOCK)
            .put(AntiqueBlocks.EXPOSED_MYRIAD_BLOCK, AntiqueBlocks.MYRIAD_BLOCK)
            .build();

    public static final Map<Block, Block> COATED_MYRIAD_BLOCKS = new ImmutableMap.Builder<Block, Block>()
            .put(AntiqueBlocks.MYRIAD_BLOCK, AntiqueBlocks.COATED_MYRIAD_BLOCK)
            .put(AntiqueBlocks.EXPOSED_MYRIAD_BLOCK, AntiqueBlocks.COATED_EXPOSED_MYRIAD_BLOCK)
            .put(AntiqueBlocks.WEATHERED_MYRIAD_BLOCK, AntiqueBlocks.COATED_WEATHERED_MYRIAD_BLOCK)
            .put(AntiqueBlocks.TARNISHED_MYRIAD_BLOCK, AntiqueBlocks.COATED_TARNISHED_MYRIAD_BLOCK)
            .build();

    public static final Map<Block, Block> COATED_MYRIAD_BLOCKS_REVERSE = new ImmutableMap.Builder<Block, Block>()
            .put(AntiqueBlocks.COATED_MYRIAD_BLOCK, AntiqueBlocks.MYRIAD_BLOCK)
            .put(AntiqueBlocks.COATED_EXPOSED_MYRIAD_BLOCK, AntiqueBlocks.EXPOSED_MYRIAD_BLOCK)
            .put(AntiqueBlocks.COATED_WEATHERED_MYRIAD_BLOCK, AntiqueBlocks.WEATHERED_MYRIAD_BLOCK)
            .put(AntiqueBlocks.COATED_TARNISHED_MYRIAD_BLOCK, AntiqueBlocks.TARNISHED_MYRIAD_BLOCK)
            .build();
}
