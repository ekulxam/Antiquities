package net.hollowed.antique.items;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.entities.MyriadShovelEntity;
import net.hollowed.antique.index.AntiqueItems;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MyriadShovelBit extends MyriadToolBitItem{

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected static final Map<Block, BlockState> PATH_STATES = Maps.<Block, BlockState>newHashMap(
            new ImmutableMap.Builder()
                    .put(Blocks.GRASS_BLOCK, Blocks.DIRT_PATH.getDefaultState())
                    .put(Blocks.DIRT, Blocks.DIRT_PATH.getDefaultState())
                    .put(Blocks.PODZOL, Blocks.DIRT_PATH.getDefaultState())
                    .put(Blocks.COARSE_DIRT, Blocks.DIRT_PATH.getDefaultState())
                    .put(Blocks.MYCELIUM, Blocks.DIRT_PATH.getDefaultState())
                    .put(Blocks.ROOTED_DIRT, Blocks.DIRT_PATH.getDefaultState())
                    .build()
    );

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

    public MyriadShovelBit(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {

        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        PlayerEntity playerEntity = context.getPlayer();

        if (!shouldCancelStripAttempt(context)) {
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
                    itemStack.damage(1, playerEntity, context.getHand());
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
        if (playerEntity == null) return false;
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
    public ActionResult toolUse(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return ActionResult.PASS;
    }

    @Override
    public UseAction toolGetUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public boolean toolOnStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity playerEntity) {
            int i = stack.getMaxUseTime(user) - remainingUseTicks;
            if (i < 10) {
                return false;
            } else {
                float f = EnchantmentHelper.getTridentSpinAttackStrength(stack, playerEntity);
                if (f > 0.0F && !playerEntity.isTouchingWaterOrRain()) {
                    return false;
                } else if (stack.willBreakNextUse()) {
                    return false;
                } else {
                    RegistryEntry<SoundEvent> registryEntry = EnchantmentHelper.getEffect(stack, EnchantmentEffectComponentTypes.TRIDENT_SOUND)
                            .orElse(SoundEvents.ITEM_TRIDENT_THROW);
                    playerEntity.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
                    if (world instanceof ServerWorld serverWorld) {
                        stack.damage(1, playerEntity);
                        if (f == 0.0F) {
                            // Create and spawn the MyriadShovelEntity at the calculated position
                            MyriadShovelEntity tridentEntity = ProjectileEntity.spawnWithVelocity(MyriadShovelEntity::new, serverWorld, stack, playerEntity, 0.0F, 2.5F, 1.0F);

                            if (playerEntity.isInCreativeMode()) {
                                tridentEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                            } else {
                                playerEntity.getInventory().removeOne(stack);
                            }

                            world.playSoundFromEntity(null, tridentEntity, registryEntry.value(), SoundCategory.PLAYERS, 1.0F, 1.0F);
                            return true;
                        }
                    }

                    if (f > 0.0F) {
                        float g = playerEntity.getYaw();
                        float h = playerEntity.getPitch();
                        float j = -MathHelper.sin(g * (float) (Math.PI / 180.0)) * MathHelper.cos(h * (float) (Math.PI / 180.0));
                        float k = -MathHelper.sin(h * (float) (Math.PI / 180.0));
                        float l = MathHelper.cos(g * (float) (Math.PI / 180.0)) * MathHelper.cos(h * (float) (Math.PI / 180.0));
                        float m = MathHelper.sqrt(j * j + k * k + l * l);
                        j *= f / m;
                        k *= f / m;
                        l *= f / m;
                        playerEntity.addVelocity(j, k, l);
                        playerEntity.useRiptide(20, 8.0F, stack);
                        if (playerEntity.isOnGround()) {
                            playerEntity.move(MovementType.SELF, new Vec3d(0.0, 1.1999999F, 0.0));
                        }

                        world.playSoundFromEntity(null, playerEntity, registryEntry.value(), SoundCategory.PLAYERS, 1.0F, 1.0F);
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public ActionResult toolUseOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        PlayerEntity playerEntity = context.getPlayer();

        if (playerEntity == null) return ActionResult.FAIL;
        if (playerEntity.isSneaking()) {
            BlockState blockState = world.getBlockState(blockPos);
            if (context.getSide() == Direction.DOWN) {
                return ActionResult.PASS;
            } else {
                BlockState blockState2 = PATH_STATES.get(blockState.getBlock());
                BlockState blockState3 = null;
                if (blockState2 != null && world.getBlockState(blockPos.up()).isAir()) {
                    world.playSound(playerEntity, blockPos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    blockState3 = blockState2;
                } else if (blockState.getBlock() instanceof CampfireBlock && blockState.get(CampfireBlock.LIT)) {
                    if (!world.isClient()) {
                        world.syncWorldEvent(null, WorldEvents.FIRE_EXTINGUISHED, blockPos, 0);
                    }

                    CampfireBlock.extinguish(context.getPlayer(), world, blockPos, blockState);
                    blockState3 = blockState.with(CampfireBlock.LIT, Boolean.FALSE);
                }

                if (blockState3 != null) {
                    if (!world.isClient()) {
                        world.setBlockState(blockPos, blockState3, Block.NOTIFY_ALL_AND_REDRAW);
                        world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(playerEntity, blockState3));
                        context.getStack().damage(1, playerEntity, context.getHand());
                    }

                    return ActionResult.SUCCESS;
                } else {
                    return ActionResult.PASS;
                }
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public void setToolAttributes(ItemStack tool) {
        tool.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder()
                .add(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, 8, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
                .add(EntityAttributes.ATTACK_SPEED, new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, -2.9, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
                .add(EntityAttributes.ENTITY_INTERACTION_RANGE, new EntityAttributeModifier(Identifier.ofVanilla("base_attack_range"), 0.75, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
                .build());
        tool.set(DataComponentTypes.TOOL, new ToolComponent(
                List.of(
                        ToolComponent.Rule.ofNeverDropping(AntiqueItems.registryEntryLookup.getOrThrow(BlockTags.INCORRECT_FOR_IRON_TOOL)),
                        ToolComponent.Rule.ofAlwaysDropping(AntiqueItems.registryEntryLookup.getOrThrow(BlockTags.SHOVEL_MINEABLE), 20)
                ),
                1.0F,
                1,
                true
        ));
        tool.set(DataComponentTypes.ITEM_MODEL, Antiquities.id("myriad_shovel"));
    }
}
