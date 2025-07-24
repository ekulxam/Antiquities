package net.hollowed.antique.items;

import net.hollowed.antique.index.AntiqueComponents;
import net.hollowed.antique.items.tooltips.SatchelTooltipData;
import net.hollowed.combatamenities.util.items.ModComponents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ClickType;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        List<ItemStack> storedStacks = new ArrayList<>(getStoredStacks(stack));  // Create a mutable copy of the list
        return !storedStacks.isEmpty();
    }

    public int getItemBarStep(ItemStack stack) {
        List<ItemStack> storedStacks = new ArrayList<>(getStoredStacks(stack));  // Create a mutable copy of the list

        int maxStacks = 8;
        return Math.round((float) storedStacks.size() / maxStacks * 13);
    }

    public int getItemBarColor(ItemStack stack) {
        List<ItemStack> storedStacks = new ArrayList<>(getStoredStacks(stack));  // Create a mutable copy of the list
        return storedStacks.size() == 8 ? FULL_ITEM_BAR_COLOR : ITEM_BAR_COLOR;
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        TooltipDisplayComponent tooltipDisplayComponent = stack.getOrDefault(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplayComponent.DEFAULT);
        return !tooltipDisplayComponent.shouldDisplay(AntiqueComponents.SATCHEL_STACK)
                ? Optional.empty()
                : Optional.ofNullable(stack.get(AntiqueComponents.SATCHEL_STACK)).map(items -> new SatchelTooltipData(items, stack));
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        if (stack.getOrDefault(AntiqueComponents.COUNTER, 2) < 1) {
            stack.set(AntiqueComponents.COUNTER, stack.getOrDefault(AntiqueComponents.COUNTER, 1) + 1);
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
                    setStoredStacks(stack, storedStacks); // Re-set without empty stacks
                    return true;
                }
            }
        } else {
            if (otherStack.isEmpty()) {
                return false;
            }

            // Check if the item being added is invalid
            if (isInvalidItem(otherStack)) {
                player.playSound(SoundEvents.ITEM_BUNDLE_INSERT_FAIL, 0.8F, 1.0F);
                return true;
            }

            // Add the stack to the first empty slot in the satchel
            for (int i = 0; i < MAX_STACKS; i++) {
                // Ensure the list is properly sized
                if (i >= storedStacks.size()) {
                    storedStacks.add(ItemStack.EMPTY); // Ensure there are enough slots in the list
                }

                if (storedStacks.get(i).isEmpty()) {
                    ItemStack splitStack = otherStack.split(otherStack.getCount());
                    storedStacks.set(i, splitStack);
                    player.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8F, 1.0F);
                    setStoredStacks(stack, storedStacks); // Re-set without empty stacks

                    // Clear the cursor stack after adding an item to the satchel
                    slot.setStack(ItemStack.EMPTY);
                    return true;
                } else if (i == 7) {
                    player.playSound(SoundEvents.ITEM_BUNDLE_INSERT_FAIL, 0.8F, 1.0F);
                    return true;
                }
            }
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
                    setStoredStacks(stack, storedStacks); // Re-set without empty stacks
                    return true;
                }
            }
        } else {
            if (cursorStackReference.get().isEmpty()) {
                return false;
            }

            // Check if the item being added is invalid
            if (isInvalidItem(otherStack)) {
                player.playSound(SoundEvents.ITEM_BUNDLE_INSERT_FAIL, 1.0F, 1.0F);
                return true;
            }

            // Add the stack to the first empty slot in the satchel
            for (int i = 0; i < MAX_STACKS; i++) {
                if (i >= storedStacks.size()) {
                    storedStacks.add(ItemStack.EMPTY);
                }

                if (storedStacks.get(i).isEmpty()) {
                    ItemStack splitStack = otherStack.split(otherStack.getCount());
                    storedStacks.set(i, splitStack);
                    player.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 1.0F, 1.0F);
                    setStoredStacks(stack, storedStacks); // Re-set without empty stacks

                    // Clear the cursor stack after adding an item to the satchel
                    cursorStackReference.set(ItemStack.EMPTY);
                    return true;
                } else if (i == 7) {
                    player.playSound(SoundEvents.ITEM_BUNDLE_INSERT_FAIL, 1.0F, 1.0F);
                    return true;
                }
            }
        }
        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    public static boolean hasSelectedStack(ItemStack stack) {
        return stack.getOrDefault(ModComponents.INTEGER_PROPERTY, -1) != -1;
    }

    public boolean isInvalidItem(ItemStack stack) {
        // Check if the item is a satchel, or shulker box
        Item item = stack.getItem();
        return item instanceof SatchelItem || item.getTranslationKey().contains("shulker_box");
    }

    public static List<ItemStack> getStoredStacks(ItemStack satchel) {
        List<ItemStack> storedStacks = satchel.get(AntiqueComponents.SATCHEL_STACK);
        return storedStacks != null ? storedStacks : new ArrayList<>();
    }

    public ItemStack getSelectedStack(ItemStack stack) {
        if (!Objects.requireNonNull(stack.get(AntiqueComponents.SATCHEL_STACK)).isEmpty()
                && index < Objects.requireNonNull(stack.get(AntiqueComponents.SATCHEL_STACK)).size()) {
            return Objects.requireNonNull(stack.get(AntiqueComponents.SATCHEL_STACK)).get(index);
        }
        return ItemStack.EMPTY;
    }

    public void setSlot(ItemStack satchel, ItemStack otherStack) {
        List<ItemStack> storedStacks = new ArrayList<>(getStoredStacks(satchel));
        if (!Objects.requireNonNull(satchel.get(AntiqueComponents.SATCHEL_STACK)).isEmpty()
                && index < Objects.requireNonNull(satchel.get(AntiqueComponents.SATCHEL_STACK)).size()) {
            storedStacks.set(index, otherStack);
        } else if (Objects.requireNonNull(satchel.get(AntiqueComponents.SATCHEL_STACK)).size() < 8 && index >= Objects.requireNonNull(satchel.get(AntiqueComponents.SATCHEL_STACK)).size()) {
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
        stack.set(ModComponents.INTEGER_PROPERTY, internalIndex);
    }

    public static int getInternalIndex(ItemStack stack) {
        return stack.getOrDefault(ModComponents.INTEGER_PROPERTY, -1);
    }

    public static void setStoredStacks(ItemStack satchel, List<ItemStack> stacks) {
        List<ItemStack> filteredStacks = new ArrayList<>();
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                filteredStacks.add(stack.copy());
            }
        }

        satchel.set(AntiqueComponents.SATCHEL_STACK, filteredStacks);
    }
}