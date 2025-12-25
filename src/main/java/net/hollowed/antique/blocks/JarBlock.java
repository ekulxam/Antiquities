package net.hollowed.antique.blocks;

import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class JarBlock extends Block {

    public JarBlock(Properties settings) {
        super(settings);
    }

    public static final VoxelShape SHAPE_DEFAULT = Stream.of(
            Block.box(3, 0, 3, 13, 14, 13),
            Block.box(4, 14, 4, 12, 16, 12)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();


    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context) {
        return SHAPE_DEFAULT;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context) {
        return SHAPE_DEFAULT;
    }
}
