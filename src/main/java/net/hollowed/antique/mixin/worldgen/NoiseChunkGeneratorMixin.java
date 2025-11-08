package net.hollowed.antique.mixin.worldgen;

import com.llamalad7.mixinextras.sugar.Local;
import net.hollowed.antique.index.AntiqueBlockTags;
import net.hollowed.antique.index.AntiqueBlocks;
import net.minecraft.block.AmethystClusterBlock;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseChunkGenerator.class)
public class NoiseChunkGeneratorMixin {

        @Inject(method = "populateNoise(Lnet/minecraft/world/gen/chunk/Blender;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/noise/NoiseConfig;Lnet/minecraft/world/chunk/Chunk;II)Lnet/minecraft/world/chunk/Chunk;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/ChunkSection;setBlockState(IIILnet/minecraft/block/BlockState;Z)Lnet/minecraft/block/BlockState;"))
    private void placeClusters(Blender blender, StructureAccessor structureAccessor, NoiseConfig noiseConfig, Chunk chunk, int minimumCellY, int cellHeight, CallbackInfoReturnable<Chunk> cir, @Local ChunkSection chunkSection, @Local BlockState state, @Local(ordinal = 17) int x1, @Local(ordinal = 13) int y1, @Local(ordinal = 20) int z1) {
        if (state.getBlock().equals(AntiqueBlocks.MYRIAD_ORE) || state.getBlock().equals(AntiqueBlocks.DEEPSLATE_MYRIAD_ORE)) {
            for (Direction direction : Direction.values()) {
                BlockPos blockPos = new BlockPos(x1, y1, z1).add(direction.getVector());

                if (chunkSection != null) {

                    int originX = ChunkSectionPos.getLocalCoord(x1);
                    int originY = ChunkSectionPos.getLocalCoord(y1);
                    int originZ = ChunkSectionPos.getLocalCoord(z1);

                    int x = ChunkSectionPos.getLocalCoord(blockPos.getX());
                    int y = ChunkSectionPos.getLocalCoord(blockPos.getY());
                    int z = ChunkSectionPos.getLocalCoord(blockPos.getZ());

                    BlockState cluster = state.getBlock().equals(AntiqueBlocks.MYRIAD_ORE) ? AntiqueBlocks.MYRIAD_CLUSTER.getDefaultState() : AntiqueBlocks.DEEPSLATE_MYRIAD_CLUSTER.getDefaultState();

                    cluster = cluster.with(AmethystClusterBlock.FACING, direction)
                            .with(AmethystClusterBlock.WATERLOGGED, chunkSection.getFluidState(x, y, z).getFluid() == Fluids.WATER);

                    boolean notOnBorder = Math.abs(originX - x) <= 1
                            && Math.abs(originY - y) <= 1
                            && Math.abs(originZ - z) <= 1;

                    if (chunkSection.getBlockState(x, y, z).isIn(AntiqueBlockTags.WATER_OR_AIR) && Math.random() < 0.3 && notOnBorder) {
                        chunkSection.setBlockState(x, y, z, cluster, false);
                    }
                }
            }
        }
    }
}
