package net.hollowed.antique.items;

import net.hollowed.antique.enchantments.EnchantmentListener;
import net.hollowed.antique.index.AntiqueDataComponentTypes;
import net.hollowed.antique.items.tooltips.SatchelTooltipData;
import net.hollowed.combatamenities.util.items.CAComponents;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SatchelItem extends Item {
    public static final int MAX_STACKS = 8;
    public static int index = 0;
    public static List<ItemStack> lastContents = null;

    private static final int FULL_ITEM_BAR_COLOR = ColorHelper.fromFloats(1.0F, 1.0F, 0.33F, 0.33F);
    private static final int ITEM_BAR_COLOR = ColorHelper.fromFloats(1.0F, 0.44F, 0.53F, 1.0F);

    public SatchelItem(Settings settings) {
        super(settings);
    }

    public boolean isItemBarVisible(ItemStack stack) {
        List<ItemStack> storedStacks = new ArrayList<>(getStoredStacks(stack));
        return !storedStacks.isEmpty();
    }

    public int getItemBarStep(ItemStack stack) {
        List<ItemStack> storedStacks = new ArrayList<>(getStoredStacks(stack));

        int maxStacks = 8;
        return Math.round((float) storedStacks.size() / maxStacks * 13);
    }

    public int getItemBarColor(ItemStack stack) {
        List<ItemStack> storedStacks = new ArrayList<>(getStoredStacks(stack));
        return storedStacks.size() == 8 ? FULL_ITEM_BAR_COLOR : ITEM_BAR_COLOR;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (EnchantmentListener.hasEnchantment(user.getStackInHand(hand), "antique:jumbling")) {
            return ActionResult.FAIL;
        }
        return super.use(world, user, hand);
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        Hand hand = context.getHand();
        PlayerEntity player = context.getPlayer();
        ItemStack stack = Objects.requireNonNull(player).getStackInHand(hand);

        if (EnchantmentListener.hasEnchantment(stack, "antique:jumbling")) {
            List<ItemStack> satchelStacks = stack.getOrDefault(AntiqueDataComponentTypes.SATCHEL_STACK, List.of());
            if (!satchelStacks.isEmpty()) {
                List<ItemStack> blockItems = new ArrayList<>(satchelStacks.stream()
                        .filter(itemStack -> itemStack.getItem() instanceof BlockItem)
                        .toList());

                if (!blockItems.isEmpty()) {
                    boolean hasNonBlockItem = blockItems.stream()
                            .anyMatch(itemStack -> !(itemStack.getItem() instanceof BlockItem));
                    if (hasNonBlockItem) {
                        return ActionResult.PASS;
                    }

                    int randomIndex = new Random().nextInt(blockItems.size());

                    ItemStack selectedBlockStack = blockItems.get(randomIndex);
                    if (selectedBlockStack != null) {
                        boolean canPlace = placeBlockFromSatchel(context, selectedBlockStack);
                        int counter = 0;
                        while (!canPlace && counter < blockItems.size()) {
                            counter++;
                            randomIndex += randomIndex == blockItems.size() - 1 ? -(blockItems.size() - 1) : 1;
                            selectedBlockStack = blockItems.get(randomIndex);
                            canPlace = placeBlockFromSatchel(context, selectedBlockStack);
                        }
                        if (!canPlace) {
                            return ActionResult.FAIL;
                        }
                        blockItems.set(randomIndex, selectedBlockStack.copyWithCount(selectedBlockStack.getCount() - (player.isCreative() ? 0 : 1)));
                        if (blockItems.get(randomIndex).isEmpty()) blockItems.remove(randomIndex);
                        stack.set(AntiqueDataComponentTypes.SATCHEL_STACK, blockItems);
                    }
                }
            }
            return ActionResult.SUCCESS;
        } else {
            return ActionResult.PASS;
        }
    }

    public boolean placeBlockFromSatchel(ItemUsageContext context, ItemStack stack) {
        BlockItem blockItem = (BlockItem) stack.getItem();
        ItemPlacementContext placementContext = new ItemPlacementContext(context);
        if (!placementContext.getWorld().isClient()) {
            ActionResult result = blockItem.place(placementContext);
            BlockPos pos = placementContext.getBlockPos();
            BlockState state = placementContext.getWorld().getBlockState(pos);
            BlockSoundGroup sound = state.getSoundGroup();

            if (result.isAccepted()) {
                placementContext.getWorld().playSound(null, pos, sound.getPlaceSound(), SoundCategory.BLOCKS,
                        (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
                Objects.requireNonNull(context.getPlayer()).swingHand(context.getHand(), true);
                return true;
            }
        }
        return false;
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        TooltipDisplayComponent tooltipDisplayComponent = stack.getOrDefault(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplayComponent.DEFAULT);
        return !tooltipDisplayComponent.shouldDisplay(AntiqueDataComponentTypes.SATCHEL_STACK)
                ? Optional.empty()
                : Optional.ofNullable(stack.get(AntiqueDataComponentTypes.SATCHEL_STACK)).map(items -> new SatchelTooltipData(items, stack));
    }

    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
        List<ItemStack> contents = entity.getStack().get(AntiqueDataComponentTypes.SATCHEL_STACK);
        if (contents != null) {
            entity.getStack().set(AntiqueDataComponentTypes.SATCHEL_STACK, List.of());
            ItemUsage.spawnItemContents(entity, contents);
        }
    }

    @Override
    public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        if (stack.getOrDefault(AntiqueDataComponentTypes.COUNTER, 2) < 1) {
            stack.set(AntiqueDataComponentTypes.COUNTER, stack.getOrDefault(AntiqueDataComponentTypes.COUNTER, 1) + 1);
        } else {
            setInternalIndex(stack, -1);
        }
        super.inventoryTick(stack, world, entity, slot);
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        List<ItemStack> storedStacks = new ArrayList<>(getStoredStacks(stack));  // Create a mutable copy of the list
        ItemStack otherStack = slot.getStack();
        if (clickType == ClickType.RIGHT) {
            if (otherStack.isEmpty()) {

                // Remove the internal selected stack :3
                if (!storedStacks.isEmpty() && !storedStacks.getFirst().isEmpty()) {
                    slot.setStack(storedStacks.getFirst().copy());
                    storedStacks.set(0, ItemStack.EMPTY);
                    player.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8F, 1.0F);
                    setStoredStacks(stack, storedStacks);
                    return true;
                }
            }
        } else {
            if (otherStack.isEmpty()) {
                return false;
            }

            if (isInvalidItem(otherStack)) {
                player.playSound(SoundEvents.ITEM_BUNDLE_INSERT_FAIL, 0.8F, 1.0F);
                return true;
            }

            for (int i = 0; i < MAX_STACKS; i++) {
                if (i >= storedStacks.size()) {
                    storedStacks.add(ItemStack.EMPTY);
                }
            }

            int otherStackCount = otherStack.getCount();
            ItemStack remainder = addToStoredStacks(storedStacks, otherStack);
            if (remainder.getCount() == otherStackCount) {
                player.playSound(SoundEvents.ITEM_BUNDLE_INSERT_FAIL, 0.8F, 1.0F);
            } else {
                player.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8F, 1.0F);
            }
            setStoredStacks(stack, storedStacks);
            return true;
        }
        return super.onStackClicked(stack, slot, clickType, player);
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        List<ItemStack> storedStacks = new ArrayList<>(getStoredStacks(stack));  // Create a mutable copy of the list
        if (clickType == ClickType.RIGHT) {
            if (otherStack.isEmpty()) {

                int index = !hasSelectedStack(stack) ? 0 : getInternalIndex(stack);

                // Remove the internal selected stack :3
                if (!storedStacks.isEmpty() && !storedStacks.get(index).isEmpty()) {
                    cursorStackReference.set(storedStacks.get(index).copy());
                    storedStacks.set(index, ItemStack.EMPTY);
                    player.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 1.0F, 1.0F);
                    setStoredStacks(stack, storedStacks);
                    return true;
                }
            }
        } else {
            if (cursorStackReference.get().isEmpty()) {
                return false;
            }

            if (isInvalidItem(otherStack)) {
                player.playSound(SoundEvents.ITEM_BUNDLE_INSERT_FAIL, 1.0F, 1.0F);
                return true;
            }

            for (int i = 0; i < MAX_STACKS; i++) {
                if (i >= storedStacks.size()) {
                    storedStacks.add(ItemStack.EMPTY);
                }
            }

            int otherStackCount = otherStack.getCount();
            ItemStack remainder = addToStoredStacks(storedStacks, otherStack);
            if (remainder.getCount() == otherStackCount) {
                player.playSound(SoundEvents.ITEM_BUNDLE_INSERT_FAIL, 1.0F, 1.0F);
            } else {
                player.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 1.0F, 1.0F);
            }
            setStoredStacks(stack, storedStacks);
            return true;
        }
        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    public static boolean hasSelectedStack(ItemStack stack) {
        return stack.getOrDefault(CAComponents.INTEGER_PROPERTY, -1) != -1;
    }

    public boolean isInvalidItem(ItemStack stack) {
        // Check if the item is a satchel, or shulker box
        Item item = stack.getItem();
        return item instanceof SatchelItem || item.getTranslationKey().contains("shulker_box");
    }

    public static ItemStack addToStoredStacks(List<ItemStack> storedStacks, ItemStack stack) {

        for (int i = 0; i < storedStacks.size(); i++) {
            ItemStack storedStack = storedStacks.get(i);
            int stackRoom = storedStack.getMaxCount() - storedStack.getCount();
            if (stackRoom > 0 && !storedStack.isEmpty()) {
                if (ItemStack.areItemsAndComponentsEqual(storedStack, stack)) {
                    int stackAmount = Math.min(stack.getCount(), stackRoom);
                    storedStack.increment(stackAmount);
                    stack.decrement(stackAmount);
                    storedStacks.set(i, storedStack);

                    if (!stack.isEmpty()) {
                        if (storedStacks.size() < MAX_STACKS) {
                            storedStacks.add(stack.copy());
                            stack.setCount(0);
                            return ItemStack.EMPTY;
                        } else if (storedStacks.contains(ItemStack.EMPTY)) {
                            storedStacks.set(storedStacks.indexOf(ItemStack.EMPTY), stack.copy());
                            stack.setCount(0);
                            return ItemStack.EMPTY;
                        } else {
                            for (ItemStack checkStack : storedStacks) {
                                if (checkStack.getCount() < checkStack.getMaxCount() && ItemStack.areItemsAndComponentsEqual(checkStack, stack)) {
                                    return addToStoredStacks(storedStacks, stack);
                                }
                            }
                        }
                    }
                }
            } else if (storedStack.isEmpty()) {
                storedStacks.set(i, stack.copy());
                stack.setCount(0);
                return ItemStack.EMPTY;
            }
        }

        if (storedStacks.size() < MAX_STACKS && storedStacks.stream().noneMatch(ItemStack::isEmpty)) {
            storedStacks.add(stack.copy());
            stack.setCount(0);
            return ItemStack.EMPTY;
        }

        return stack;
    }

    public static List<ItemStack> getStoredStacks(ItemStack satchel) {
        List<ItemStack> storedStacks = satchel.get(AntiqueDataComponentTypes.SATCHEL_STACK);
        return storedStacks != null ? storedStacks : new ArrayList<>();
    }

    public ItemStack getSelectedStack(ItemStack stack) {
        if (!Objects.requireNonNull(stack.get(AntiqueDataComponentTypes.SATCHEL_STACK)).isEmpty()
                && index < Objects.requireNonNull(stack.get(AntiqueDataComponentTypes.SATCHEL_STACK)).size()) {
            return Objects.requireNonNull(stack.get(AntiqueDataComponentTypes.SATCHEL_STACK)).get(index);
        }
        return ItemStack.EMPTY;
    }

    public void setSlot(ItemStack satchel, ItemStack otherStack) {
        List<ItemStack> storedStacks = new ArrayList<>(getStoredStacks(satchel));
        if (!Objects.requireNonNull(satchel.get(AntiqueDataComponentTypes.SATCHEL_STACK)).isEmpty()
                && index < Objects.requireNonNull(satchel.get(AntiqueDataComponentTypes.SATCHEL_STACK)).size()) {
            storedStacks.set(index, otherStack);
        } else if (Objects.requireNonNull(satchel.get(AntiqueDataComponentTypes.SATCHEL_STACK)).size() < 8 && index >= Objects.requireNonNull(satchel.get(AntiqueDataComponentTypes.SATCHEL_STACK)).size()) {
            storedStacks.add(otherStack);
        }
        setStoredStacks(satchel, storedStacks);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        SatchelItem.index = index;
    }

    public static void setInternalIndex(ItemStack stack, int internalIndex) {
        stack.set(CAComponents.INTEGER_PROPERTY, internalIndex);
    }

    public static int getInternalIndex(ItemStack stack) {
        return stack.getOrDefault(CAComponents.INTEGER_PROPERTY, -1);
    }

    public static void setStoredStacks(ItemStack satchel, List<ItemStack> stacks) {
        List<ItemStack> filteredStacks = new ArrayList<>();
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                filteredStacks.add(stack.copy());
            }
        }

        satchel.set(AntiqueDataComponentTypes.SATCHEL_STACK, filteredStacks);
    }
}