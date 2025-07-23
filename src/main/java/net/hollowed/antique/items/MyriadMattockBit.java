package net.hollowed.antique.items;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.index.AntiqueItems;
import net.minecraft.block.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MyriadMattockBit extends MyriadToolBitItem{

    protected static final Map<Block, Pair<Predicate<ItemUsageContext>, Consumer<ItemUsageContext>>> TILLING_ACTIONS = Maps.newHashMap(
            ImmutableMap.of(
                    Blocks.GRASS_BLOCK,
                    com.mojang.datafixers.util.Pair.of(HoeItem::canTillFarmland, createTillAction(Blocks.FARMLAND.getDefaultState())),
                    Blocks.DIRT_PATH,
                    com.mojang.datafixers.util.Pair.of(HoeItem::canTillFarmland, createTillAction(Blocks.FARMLAND.getDefaultState())),
                    Blocks.DIRT,
                    com.mojang.datafixers.util.Pair.of(HoeItem::canTillFarmland, createTillAction(Blocks.FARMLAND.getDefaultState())),
                    Blocks.COARSE_DIRT,
                    com.mojang.datafixers.util.Pair.of(HoeItem::canTillFarmland, createTillAction(Blocks.DIRT.getDefaultState())),
                    Blocks.ROOTED_DIRT,
                    Pair.of(itemUsageContext -> true, createTillAndDropAction(Blocks.DIRT.getDefaultState(), Items.HANGING_ROOTS))
            )
    );

    public MyriadMattockBit(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof SheepEntity) {
            user.swingHand(hand);
        }
        return super.useOnEntity(stack, user, entity, hand);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (state.getBlock() instanceof CropBlock cropBlock && !(state.getBlock() instanceof BeetrootsBlock)) {
            if (state.get(CropBlock.AGE) == cropBlock.getMaxAge() && !miner.isSneaking()) {
                BlockState newState = cropBlock.getDefaultState();
                world.setBlockState(pos, newState);
            }
        } else if (state.getBlock() instanceof BeetrootsBlock cropBlock) {
            if (state.get(BeetrootsBlock.AGE) == cropBlock.getMaxAge() && !miner.isSneaking()) {
                BlockState newState = cropBlock.getDefaultState();
                world.setBlockState(pos, newState);
            }
        } else if (state.getBlock() instanceof NetherWartBlock cropBlock) {
            if (state.get(NetherWartBlock.AGE) == NetherWartBlock.MAX_AGE && !miner.isSneaking()) {
                BlockState newState = cropBlock.getDefaultState();
                world.setBlockState(pos, newState);
            }
        } else if (state.getBlock() instanceof CocoaBlock) {
            if (state.get(CocoaBlock.AGE) == CocoaBlock.MAX_AGE && !miner.isSneaking()) {
                BlockState newState = state.with(CocoaBlock.AGE, 0);
                world.setBlockState(pos, newState);
            }
        }
        return super.postMine(stack, world, state, pos, miner);
    }

    @Override
    public ActionResult toolUse(World world, PlayerEntity user, Hand hand) {
        double d = -MathHelper.sin(user.getYaw() * (float) (Math.PI / 180.0));
        double e = MathHelper.cos(user.getYaw() * (float) (Math.PI / 180.0));
        if (user.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.SWEEP_ATTACK, user.getX() + d, user.getBodyY(0.5), user.getZ() + e, 0, d, 0.0, e, 0.0);
        }
        user.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
        user.swingHand(hand, true);
        user.getItemCooldownManager().set(user.getStackInHand(hand), 10);
        Vec3d forward = user.getPos().add(user.getRotationVector().multiply(2));
        Box box = new Box(
                forward.x - 1.5, forward.y - 1.5, forward.z - 1.5,
                forward.x + 1.5, forward.y + 1.5, forward.z + 1.5
        );
        for (Entity entity : world.getOtherEntities(user, box)) {
            entity.addVelocity(user.getRotationVector().multiply(-1));
            entity.velocityModified = true;
        }
        return ActionResult.PASS;
    }

    @Override
    public ActionResult toolUseOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        PlayerEntity playerEntity = context.getPlayer();
        if (playerEntity == null) return ActionResult.FAIL;

        if (playerEntity.isSneaking()) {
            Pair<Predicate<ItemUsageContext>, Consumer<ItemUsageContext>> pair = TILLING_ACTIONS.get(
                    world.getBlockState(blockPos).getBlock()
            );
            if (pair == null) {
                return ActionResult.PASS;
            } else {
                Predicate<ItemUsageContext> predicate = pair.getFirst();
                Consumer<ItemUsageContext> consumer = pair.getSecond();
                if (predicate.test(context)) {
                    world.playSound(playerEntity, blockPos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    if (!world.isClient) {
                        consumer.accept(context);
                        context.getStack().damage(1, playerEntity, LivingEntity.getSlotForHand(context.getHand()));
                    }

                    return ActionResult.SUCCESS;
                } else {
                    return ActionResult.PASS;
                }
            }
        } else {
            this.toolUse(world, playerEntity, context.getHand());
        }
        return super.toolUseOnBlock(context);
    }

    public static Consumer<ItemUsageContext> createTillAction(BlockState result) {
        return context -> {
            context.getWorld().setBlockState(context.getBlockPos(), result, Block.NOTIFY_ALL_AND_REDRAW);
            context.getWorld().emitGameEvent(GameEvent.BLOCK_CHANGE, context.getBlockPos(), GameEvent.Emitter.of(context.getPlayer(), result));
        };
    }

    public static Consumer<ItemUsageContext> createTillAndDropAction(BlockState result, ItemConvertible droppedItem) {
        return context -> {
            context.getWorld().setBlockState(context.getBlockPos(), result, Block.NOTIFY_ALL_AND_REDRAW);
            context.getWorld().emitGameEvent(GameEvent.BLOCK_CHANGE, context.getBlockPos(), GameEvent.Emitter.of(context.getPlayer(), result));
            Block.dropStack(context.getWorld(), context.getBlockPos(), context.getSide(), new ItemStack(droppedItem));
        };
    }

    @Override
    public void setToolAttributes(ItemStack tool) {
        tool.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder()
                .add(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, 5.0, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
                .add(EntityAttributes.ATTACK_SPEED, new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, -2.4, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
                .add(EntityAttributes.ENTITY_INTERACTION_RANGE, new EntityAttributeModifier(Identifier.ofVanilla("base_attack_range"), 0.75, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
                .build());
        tool.set(DataComponentTypes.TOOL, new ToolComponent(
                List.of(
                        ToolComponent.Rule.ofNeverDropping(AntiqueItems.registryEntryLookup.getOrThrow(BlockTags.INCORRECT_FOR_IRON_TOOL)),
                        ToolComponent.Rule.ofAlwaysDropping(AntiqueItems.registryEntryLookup.getOrThrow(TagKey.of(RegistryKeys.BLOCK, Identifier.of(Antiquities.MOD_ID, "mineable/mattock"))), 20)
                ),
                1.0F,
                1,
                true
        ));
        tool.set(DataComponentTypes.ITEM_MODEL, Antiquities.id("myriad_mattock"));
    }
}
