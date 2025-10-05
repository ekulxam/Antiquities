package net.hollowed.antique.blocks;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hollowed.antique.index.AntiqueBlockEntities;
import net.hollowed.antique.blocks.entities.PedestalBlockEntity;
import net.hollowed.antique.mixin.accessors.MobEntitySoundAccessor;
import net.hollowed.antique.networking.PedestalPacketPayload;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

import java.util.List;
import java.util.stream.Stream;

public class PedestalBlock extends BlockWithEntity implements BlockEntityProvider, Waterloggable {

    public static final List<BlockPos> POWER_PROVIDER_OFFSETS = BlockPos.stream(-2, 0, -2, 2, 1, 2).filter((pos) -> Math.abs(pos.getX()) == 2 || Math.abs(pos.getZ()) == 2).map(BlockPos::toImmutable).toList();

    public static final BooleanProperty HELD_ITEM = BooleanProperty.of("held_item");
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public static final EnumProperty<PillarPart> PART = EnumProperty.of("part", PillarPart.class);

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (!world.isClient()) {
            return type == AntiqueBlockEntities.PEDESTAL_BLOCK_ENTITY
                    ? (world1, pos, state1, blockEntity) ->
                    PedestalBlockEntity.tick(world1, (PedestalBlockEntity) blockEntity)
                    : null;
        }
        return null;
    }

    public static final VoxelShape SHAPE_DEFAULT = Stream.of(
            Block.createCuboidShape(1, 0, 1, 15, 3, 15),
            Block.createCuboidShape(3, 3, 3, 13, 13, 13),
            Block.createCuboidShape(1, 13, 1, 15, 16, 15)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
    public static final VoxelShape SHAPE_TOP = Stream.of(
            Block.createCuboidShape(3, 0, 3, 13, 13, 13),
            Block.createCuboidShape(1, 13, 1, 15, 16, 15)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
    public static final VoxelShape SHAPE_MIDDLE = java.util.Optional.of(
            Block.createCuboidShape(3, 0, 3, 13, 16, 13)
    ).get();
    public static final VoxelShape SHAPE_BOTTOM = Stream.of(
            Block.createCuboidShape(1, 0, 1, 15, 3, 15),
            Block.createCuboidShape(3, 3, 3, 13, 16, 13)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    public PedestalBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(HELD_ITEM, false).with(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    private PillarPart getPillarPart(World world, BlockPos pos) {
        BlockState stateBelow = world.getBlockState(pos.down());
        BlockState stateAbove = world.getBlockState(pos.up());

        boolean isSameBelow = stateBelow.getBlock() instanceof PedestalBlock;
        boolean isSameAbove = stateAbove.getBlock() instanceof PedestalBlock;

        if (isSameBelow && isSameAbove) {
            return PillarPart.MIDDLE;
        } else if (isSameBelow) {
            return PillarPart.TOP;
        } else if (isSameAbove) {
            return PillarPart.BOTTOM;
        } else {
            return PillarPart.DEFAULT;
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HELD_ITEM, WATERLOGGED, PART);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        BlockState stateBelow = world.getBlockState(pos.down());

        if (stateBelow.getBlock() instanceof PedestalBlock) {
            BlockEntity entityBelow = world.getBlockEntity(pos.down());
            if (entityBelow instanceof PedestalBlockEntity) {
                ItemStack stack = ((PedestalBlockEntity) entityBelow).getStack(0);
                if (!stack.isEmpty()) {
                    return null;
                }
            }
        }

        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean waterlogged = fluidState.getFluid() == Fluids.WATER;
        return this.getDefaultState().with(HELD_ITEM, false).with(WATERLOGGED, waterlogged).with(PART, this.getPillarPart(world, pos));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (!world.isClient()) {
            if (blockEntity instanceof PedestalBlockEntity pedestalBlockEntity) {
                ServerWorld serverWorld = (ServerWorld) world;
                for (ServerPlayerEntity serverPlayerEntity : serverWorld.getPlayers()) {
                    ServerPlayNetworking.send(serverPlayerEntity, new PedestalPacketPayload(pos, pedestalBlockEntity.getItems().getFirst()));
                }
            }
        }
        world.setBlockState(pos, state.with(PART, this.getPillarPart(world, pos)), 3);
    }

    public static boolean canAccessPowerProvider(World world, BlockPos tablePos, BlockPos providerOffset) {
        return world.getBlockState(tablePos.add(providerOffset)).isIn(BlockTags.ENCHANTMENT_POWER_PROVIDER) && world.getBlockState(tablePos.add(providerOffset.getX() / 2, providerOffset.getY(), providerOffset.getZ() / 2)).isIn(BlockTags.ENCHANTMENT_POWER_TRANSMITTER);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        BlockEntity entity = world.getBlockEntity(pos);

        if (entity instanceof PedestalBlockEntity pedestalBlockEntity) {
            if (!pedestalBlockEntity.getItems().getFirst().isEmpty()
                    && pedestalBlockEntity.getItems().getFirst().hasEnchantments()) {
                for (BlockPos blockPos : POWER_PROVIDER_OFFSETS) {
                    if (random.nextInt(16) == 0 && canAccessPowerProvider(world, pos, blockPos)) {
                        world.addParticleClient(ParticleTypes.ENCHANT, (double) pos.getX() + 0.5, (double) pos.getY() + 2.75, (double) pos.getZ() + 0.5, (double) ((float) blockPos.getX() + random.nextFloat()) - 0.5, (float) blockPos.getY() - random.nextFloat() - 1.0F, (double) ((float) blockPos.getZ() + random.nextFloat()) - 0.5);
                    }
                }
            }
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return getShape(state);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return getShape(state);
    }

    private VoxelShape getShape(BlockState state) {
        PillarPart part = state.get(PART);

        return switch (part) {
            case TOP -> SHAPE_TOP;
            case MIDDLE -> SHAPE_MIDDLE;
            case BOTTOM -> SHAPE_BOTTOM;
            default -> SHAPE_DEFAULT;
        };
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PedestalBlockEntity(pos, state);
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof PedestalBlockEntity pedestalEntity && state.get(PART) != PillarPart.BOTTOM && state.get(PART) != PillarPart.MIDDLE) {
            ItemStack currentPedestalItem = pedestalEntity.getStack(0);
            ItemStack handItem = player.getStackInHand(hand);

            if (!handItem.isEmpty() && handItem.getCount() > 1 && !currentPedestalItem.isEmpty()) {
                return ActionResult.FAIL;
            }

            boolean itemChanged = false;

            if (!currentPedestalItem.isEmpty()) {
                player.setStackInHand(hand, currentPedestalItem);
                pedestalEntity.setStack(0, ItemStack.EMPTY);
                itemChanged = true;
            }

            ItemStack newItem = handItem.copy();
            newItem.setCount(1);

            if (!handItem.isEmpty()) {
                pedestalEntity.setStack(0, newItem);
                handItem.decrement(1);
                itemChanged = true;

                if (newItem.getItem() instanceof SpawnEggItem spawnEggItem) {
                    EntityType<?> entityType = spawnEggItem.getEntityType(newItem);
                    if (entityType != null && !world.isClient()) {
                        Entity entity1 = entityType.create(world, SpawnReason.MOB_SUMMONED);
                        if (entity1 instanceof MobEntity mobEntity) {
                            SoundEvent ambientSound = ((MobEntitySoundAccessor) mobEntity).invokeGetAmbientSound();
                            if (ambientSound != null) {
                                world.playSound(null, pos, ambientSound, mobEntity.getSoundCategory());
                            }
                        }
                    }
                }
            }

            if (itemChanged && !world.isClient()) {
                pedestalEntity.markDirty();
                world.playSound(null, pos, SoundEvents.BLOCK_SUSPICIOUS_SAND_PLACE, SoundCategory.BLOCKS, 0.75F, 1.0F);
                world.playSound(null, pos, SoundEvents.BLOCK_LODESTONE_PLACE, SoundCategory.BLOCKS, 0.75F, 1.0F);
                ((ServerWorld) world).getChunkManager().markForUpdate(pos);
                world.setBlockState(pos, state.with(PedestalBlock.HELD_ITEM, !pedestalEntity.getStack(0).isEmpty()), Block.NOTIFY_ALL);
                world.updateNeighborsAlways(pos, state.getBlock(), null);

                ServerWorld serverWorld = (ServerWorld) world;
                for (ServerPlayerEntity serverPlayerEntity : serverWorld.getPlayers()) {
                    ServerPlayNetworking.send(serverPlayerEntity, new PedestalPacketPayload(pedestalEntity.getPos(), newItem));
                }
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        if (state != world.getBlockState(pos)) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof PedestalBlockEntity) {
                ((PedestalBlockEntity) blockEntity).drops();
            }
        }
        super.onStateReplaced(state, world, pos, moved);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos, Direction direction) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof PedestalBlockEntity) {
            ItemStack stack = ((PedestalBlockEntity) blockEntity).getStack(0);
            switch (stack.getRarity()) {
                case Rarity.COMMON -> {
                    return !stack.isEmpty() ? 1 : 0;
                }
                case Rarity.UNCOMMON -> {
                    return !stack.isEmpty() ? 2 : 0;
                }
                case Rarity.RARE -> {
                    return !stack.isEmpty() ? 3 : 0;
                }
                case Rarity.EPIC -> {
                    return !stack.isEmpty() ? 4 : 0;
                }
            }
        }
        return 0;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (state.get(WATERLOGGED)) {
            tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        if (direction.getAxis() == Direction.Axis.Y) {
            return state.with(PART, this.getPillarPart((World) world, pos));
        }

        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    public enum PillarPart implements StringIdentifiable {
        BOTTOM("bottom"),
        MIDDLE("middle"),
        TOP("top"),
        DEFAULT("default");

        private final String name;

        PillarPart(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}