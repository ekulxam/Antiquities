package net.hollowed.antique.blocks;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;

import java.util.Map;

public class BlockUtil {

    public static final Map<Block, Block> TARNISHING_BLOCKS = new ImmutableMap.Builder<Block, Block>()
            .put(ModBlocks.MYRIAD_BLOCK, ModBlocks.EXPOSED_MYRIAD_BLOCK)
            .put(ModBlocks.EXPOSED_MYRIAD_BLOCK, ModBlocks.WEATHERED_MYRIAD_BLOCK)
            .put(ModBlocks.WEATHERED_MYRIAD_BLOCK, ModBlocks.TARNISHED_MYRIAD_BLOCK)
            .build();

    public static final Map<Block, Block> TARNISHING_BLOCKS_REVERSE = new ImmutableMap.Builder<Block, Block>()
            .put(ModBlocks.TARNISHED_MYRIAD_BLOCK, ModBlocks.WEATHERED_MYRIAD_BLOCK)
            .put(ModBlocks.WEATHERED_MYRIAD_BLOCK, ModBlocks.EXPOSED_MYRIAD_BLOCK)
            .put(ModBlocks.EXPOSED_MYRIAD_BLOCK, ModBlocks.MYRIAD_BLOCK)
            .build();

    public static final Map<Block, Block> COATED_MYRIAD_BLOCKS = new ImmutableMap.Builder<Block, Block>()
            .put(ModBlocks.MYRIAD_BLOCK, ModBlocks.COATED_MYRIAD_BLOCK)
            .put(ModBlocks.EXPOSED_MYRIAD_BLOCK, ModBlocks.COATED_EXPOSED_MYRIAD_BLOCK)
            .put(ModBlocks.WEATHERED_MYRIAD_BLOCK, ModBlocks.COATED_WEATHERED_MYRIAD_BLOCK)
            .put(ModBlocks.TARNISHED_MYRIAD_BLOCK, ModBlocks.COATED_TARNISHED_MYRIAD_BLOCK)
            .build();

    public static final Map<Block, Block> COATED_MYRIAD_BLOCKS_REVERSE = new ImmutableMap.Builder<Block, Block>()
            .put(ModBlocks.COATED_MYRIAD_BLOCK, ModBlocks.MYRIAD_BLOCK)
            .put(ModBlocks.COATED_EXPOSED_MYRIAD_BLOCK, ModBlocks.EXPOSED_MYRIAD_BLOCK)
            .put(ModBlocks.COATED_WEATHERED_MYRIAD_BLOCK, ModBlocks.WEATHERED_MYRIAD_BLOCK)
            .put(ModBlocks.COATED_TARNISHED_MYRIAD_BLOCK, ModBlocks.TARNISHED_MYRIAD_BLOCK)
            .build();
}
