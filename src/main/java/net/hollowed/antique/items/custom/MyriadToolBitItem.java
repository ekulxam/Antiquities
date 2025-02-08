package net.hollowed.antique.items.custom;

import com.google.common.collect.ImmutableMap;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;


public class MyriadToolBitItem extends ShearsItem {
    private final int id;
    private int stamina = 6;
    private int counter;
    private static final int ITEM_BAR_COLOR = ColorHelper.fromFloats(1.0F, 0.79F, 0.15F, 0.34F);

    protected static final Map<Block, Block> CHISEL_BLOCKS = new ImmutableMap.Builder<Block, Block>()
            .put(Blocks.STONE, Blocks.STONE_BRICKS)
            .put(Blocks.INFESTED_STONE, Blocks.INFESTED_STONE_BRICKS)
            .put(Blocks.STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS)
            .put(Blocks.INFESTED_STONE_BRICKS, Blocks.INFESTED_CHISELED_STONE_BRICKS)
            .put(Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_MOSAIC)
            .put(Blocks.ANDESITE, Blocks.POLISHED_ANDESITE)
            .put(Blocks.DIORITE, Blocks.POLISHED_DIORITE)
            .put(Blocks.GRANITE, Blocks.POLISHED_GRANITE)
            .put(Blocks.TUFF, Blocks.POLISHED_TUFF)
            .put(Blocks.POLISHED_TUFF, Blocks.TUFF_BRICKS)
            .put(Blocks.TUFF_BRICKS, Blocks.CHISELED_TUFF_BRICKS)
            .put(Blocks.DEEPSLATE, Blocks.POLISHED_DEEPSLATE)
            .put(Blocks.POLISHED_DEEPSLATE, Blocks.CHISELED_DEEPSLATE)
            .put(Blocks.RESIN_BRICKS, Blocks.CHISELED_RESIN_BRICKS)
            .put(Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE)
            .put(Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE)
            .put(Blocks.NETHER_BRICKS, Blocks.CHISELED_NETHER_BRICKS)
            .put(Blocks.BLACKSTONE, Blocks.POLISHED_BLACKSTONE)
            .put(Blocks.POLISHED_BLACKSTONE, Blocks.CHISELED_POLISHED_BLACKSTONE)
            .put(Blocks.QUARTZ_PILLAR, Blocks.CHISELED_QUARTZ_BLOCK)
            .put(Blocks.COPPER_BLOCK, Blocks.CUT_COPPER)
            .put(Blocks.CUT_COPPER, Blocks.CHISELED_COPPER)
            .put(Blocks.EXPOSED_COPPER, Blocks.EXPOSED_CUT_COPPER)
            .put(Blocks.EXPOSED_CUT_COPPER, Blocks.EXPOSED_CHISELED_COPPER)
            .put(Blocks.WEATHERED_COPPER, Blocks.WEATHERED_CUT_COPPER)
            .put(Blocks.WEATHERED_CUT_COPPER, Blocks.WEATHERED_CHISELED_COPPER)
            .put(Blocks.OXIDIZED_COPPER, Blocks.OXIDIZED_CUT_COPPER)
            .put(Blocks.OXIDIZED_CUT_COPPER, Blocks.OXIDIZED_CHISELED_COPPER)
            .put(Blocks.WAXED_COPPER_BLOCK, Blocks.WAXED_CUT_COPPER)
            .put(Blocks.WAXED_CUT_COPPER, Blocks.WAXED_CHISELED_COPPER)
            .put(Blocks.WAXED_EXPOSED_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER)
            .put(Blocks.WAXED_EXPOSED_CUT_COPPER, Blocks.WAXED_EXPOSED_CHISELED_COPPER)
            .put(Blocks.WAXED_WEATHERED_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER)
            .put(Blocks.WAXED_WEATHERED_CUT_COPPER, Blocks.WAXED_WEATHERED_CHISELED_COPPER)
            .put(Blocks.WAXED_OXIDIZED_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER)
            .put(Blocks.WAXED_OXIDIZED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CHISELED_COPPER)
            .put(Blocks.PACKED_MUD, Blocks.MUD_BRICKS)
            .put(Blocks.PRISMARINE, Blocks.PRISMARINE_BRICKS)
            .put(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR)
            .put(Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_BRICKS)
            .put(Blocks.QUARTZ_BRICKS, Blocks.QUARTZ_PILLAR)
            .put(Blocks.END_STONE, Blocks.END_STONE_BRICKS)
            .build();

    protected static final Map<Block, Block> CRACK_BLOCKS = new ImmutableMap.Builder<Block, Block>()
            .put(Blocks.STONE, Blocks.COBBLESTONE)
            .put(Blocks.STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS)
            .put(Blocks.DEEPSLATE_BRICKS, Blocks.CRACKED_DEEPSLATE_BRICKS)
            .put(Blocks.DEEPSLATE_TILES, Blocks.CRACKED_DEEPSLATE_TILES)
            .put(Blocks.NETHER_BRICKS, Blocks.CRACKED_NETHER_BRICKS)
            .put(Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS)
            .put(Blocks.INFESTED_STONE_BRICKS, Blocks.INFESTED_CRACKED_STONE_BRICKS)
            .put(Blocks.INFESTED_STONE, Blocks.INFESTED_COBBLESTONE)
            .put(Blocks.DEEPSLATE, Blocks.COBBLED_DEEPSLATE)
            .put(Blocks.TUFF, Blocks.CHISELED_TUFF)
            .put(Blocks.BLACKSTONE, Blocks.POLISHED_BLACKSTONE_BRICKS)
            .build();

    public boolean isItemBarVisible(ItemStack stack) {
        return false;
    }

    public int getItemBarStep(ItemStack stack) {
        return Math.round((float) this.stamina / 6 * 13);
    }

    public int getItemBarColor(ItemStack stack) {
        return ITEM_BAR_COLOR;
    }

    public MyriadToolBitItem(Settings settings, int id) {
        super(settings);
        this.id = id;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (entity.isOnGround()) {
            this.counter++;
        }
        if (this.counter >= 15) {
            if (this.stamina < 6 && entity.isOnGround()) {
                this.stamina++;
            }
            this.counter = 0;
        }
    }

    public void decrementStamina() {
        if (this.stamina > 0) {
            this.stamina--;
        }
    }

    public int getStamina() {
        return this.stamina;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {

        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        PlayerEntity playerEntity = context.getPlayer();

        if (!shouldCancelStripAttempt(context) && this.id == 3) {
            Optional<BlockState> optional = this.tryStrip(world, blockPos, playerEntity, world.getBlockState(blockPos));
            if (optional.isEmpty()) {
                return ActionResult.PASS;
            } else {
                ItemStack itemStack = context.getStack();
                if (playerEntity instanceof ServerPlayerEntity) {
                    Criteria.ITEM_USED_ON_BLOCK.trigger((ServerPlayerEntity) playerEntity, blockPos, itemStack);
                }

                world.setBlockState(blockPos, optional.get(), Block.NOTIFY_ALL_AND_REDRAW);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(playerEntity, optional.get()));
                if (playerEntity != null) {
                    itemStack.damage(1, playerEntity, LivingEntity.getSlotForHand(context.getHand()));
                }

                return ActionResult.SUCCESS;
            }
        } else {
            return ActionResult.PASS;
        }
    }

    private static boolean shouldCancelStripAttempt(ItemUsageContext context) {
        PlayerEntity playerEntity = context.getPlayer();
        if (!context.getHand().equals(Hand.MAIN_HAND)) return false;
        assert playerEntity != null;
        return playerEntity.getOffHandStack().isOf(Items.SHIELD) && !playerEntity.shouldCancelInteraction();
    }

    private Optional<BlockState> tryStrip(World world, BlockPos pos, @Nullable PlayerEntity player, BlockState state) {
        Optional<BlockState> optional = this.getChiselState(state);
        if (optional.isPresent() && player != null && !player.isSneaking()) {
            world.playSound(player, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return optional;
        } else {
            Optional<BlockState> optional1 = this.getCrackState(state);
            if (optional1.isPresent() && player != null && player.isSneaking()) {
                world.playSound(player, pos, SoundEvents.BLOCK_NETHER_BRICKS_BREAK, SoundCategory.BLOCKS, 1.0F, 0.6F);
                return optional1;
            }
            return Optional.empty();
        }
    }

    private Optional<BlockState> getChiselState(BlockState state) {
        return Optional.ofNullable(CHISEL_BLOCKS.get(state.getBlock()))
                .map(Block::getDefaultState);
    }

    private Optional<BlockState> getCrackState(BlockState state) {
        return Optional.ofNullable(CRACK_BLOCKS.get(state.getBlock()))
                .map(Block::getDefaultState);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (this.id == 1) {
            if (entity instanceof SheepEntity) {
                user.swingHand(hand);
            }
        }
        return super.useOnEntity(stack, user, entity, hand);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (this.id == 2) {
            user.setCurrentHand(hand);
            return ActionResult.PASS;
        }
        return ActionResult.FAIL;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (this.id == 1) {
            if (state.getBlock() instanceof CropBlock cropBlock && !(state.getBlock() instanceof BeetrootsBlock)) {
                // Check if the crop is at its maximum age
                if (state.get(CropBlock.AGE) == cropBlock.getMaxAge() && !miner.isSneaking()) {
                    // Reset the crop to its default state
                    BlockState newState = cropBlock.getDefaultState();
                    world.setBlockState(pos, newState);
                }
            } else if (state.getBlock() instanceof BeetrootsBlock cropBlock) {
                // Check if the crop is at its maximum age
                if (state.get(BeetrootsBlock.AGE) == cropBlock.getMaxAge() && !miner.isSneaking()) {
                    // Reset the crop to its default state
                    BlockState newState = cropBlock.getDefaultState();
                    world.setBlockState(pos, newState);
                }
            } else if (state.getBlock() instanceof NetherWartBlock cropBlock) {
                // Check if the crop is at its maximum age
                if (state.get(NetherWartBlock.AGE) == NetherWartBlock.MAX_AGE && !miner.isSneaking()) {
                    // Reset the crop to its default state
                    BlockState newState = cropBlock.getDefaultState();
                    world.setBlockState(pos, newState);
                }
            } else if (state.getBlock() instanceof CocoaBlock) {
                // Check if the crop is at its maximum age
                if (state.get(CocoaBlock.AGE) == CocoaBlock.MAX_AGE && !miner.isSneaking()) {
                    // Reset the crop to its default state
                    BlockState newState = state.with(CocoaBlock.AGE, 0);
                    world.setBlockState(pos, newState);
                }
            }
        } else {
            return false;
        }
        return super.postMine(stack, world, state, pos, miner);
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        if (this.id == 1) {
            if (state.getBlock() instanceof CropBlock cropBlock && !(state.getBlock() instanceof BeetrootsBlock)) {
                if (state.get(CropBlock.AGE) != cropBlock.getMaxAge() && !miner.isSneaking()) {
                    return false;
                }
            } else if (state.getBlock() instanceof BeetrootsBlock cropBlock) {
                if (state.get(BeetrootsBlock.AGE) != cropBlock.getMaxAge() && !miner.isSneaking()) {
                    return false;
                }
            } else if (state.getBlock() instanceof NetherWartBlock) {
                if (state.get(NetherWartBlock.AGE) != NetherWartBlock.MAX_AGE && !miner.isSneaking()) {
                    return false;
                }
            } else if (state.getBlock() instanceof CocoaBlock) {
                if (state.get(CocoaBlock.AGE) != CocoaBlock.MAX_AGE && !miner.isSneaking()) {
                    return false;
                }
            }
        }
        return super.canMine(state, world, pos, miner);
    }

    public int getId() {
        return this.id;
    }
}
