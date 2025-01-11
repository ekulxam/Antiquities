package net.hollowed.antique.items.custom;

import net.hollowed.antique.component.ModComponents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ColorHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SatchelItem extends Item {
    private static final int MAX_STACKS = 8;
    private int index = 0;
    private int internalIndex = 0;

    private static final int FULL_ITEM_BAR_COLOR = ColorHelper.fromFloats(1.0F, 1.0F, 0.33F, 0.33F);
    private static final int ITEM_BAR_COLOR = ColorHelper.fromFloats(1.0F, 0.44F, 0.53F, 1.0F);

    public SatchelItem(ArmorMaterial material, EquipmentType type, Item.Settings settings) {
        super(material.applySettings(settings, type));
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
                    this.setInternalIndex(0);
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
                this.setInternalIndex(0);
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
                    this.setInternalIndex(0);
                    return true;
                } else if (i == 7) {
                    player.playSound(SoundEvents.ITEM_BUNDLE_INSERT_FAIL, 0.8F, 1.0F);
                    this.setInternalIndex(0);
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

                // Remove the internal selected stack :3
                if (!storedStacks.isEmpty() && !storedStacks.get(this.internalIndex).isEmpty()) {
                    cursorStackReference.set(storedStacks.get(this.internalIndex).copy());
                    storedStacks.set(this.internalIndex, ItemStack.EMPTY);
                    player.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 1.0F, 1.0F);
                    setStoredStacks(stack, storedStacks); // Re-set without empty stacks
                    this.setInternalIndex(0);
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
                this.setInternalIndex(0);
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
                    player.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 1.0F, 1.0F);
                    setStoredStacks(stack, storedStacks); // Re-set without empty stacks

                    // Clear the cursor stack after adding an item to the satchel
                    cursorStackReference.set(ItemStack.EMPTY);
                    this.setInternalIndex(0);
                    return true;
                } else if (i == 7) {
                    player.playSound(SoundEvents.ITEM_BUNDLE_INSERT_FAIL, 1.0F, 1.0F);
                    this.setInternalIndex(0);
                    return true;
                }
            }
        }
        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        List<ItemStack> storedStacks = getStoredStacks(stack);
        MinecraftClient client = MinecraftClient.getInstance();
        int maxWidth = 0;

        // Add item information to the tooltip
        for (int i = 0; i < storedStacks.size(); i++) {
            ItemStack storedStack = storedStacks.get(i);
            if (!storedStack.isEmpty()) {
                assert Formatting.GRAY.getColorValue() != null;
                assert storedStack.getRarity().getFormatting().getColorValue() != null;

                // Determine the prefix based on the index
                String prefix = (i == this.getInternalIndex()) ? "[-] " : " -  ";

                assert Formatting.WHITE.getColorValue() != null;
                int color = Formatting.WHITE.getColorValue(); // Default color
                Text customName = storedStack.get(DataComponentTypes.ITEM_NAME);
                if (customName != null && customName.getStyle() != null && customName.getStyle().getColor() != null) {
                    color = customName.getStyle().getColor().getRgb();
                } else if (storedStack.getRarity().getFormatting().getColorValue() != null) {
                    color = storedStack.getRarity().getFormatting().getColorValue();
                }


                // Build the line
                Text line = Text.literal(prefix).withColor(Formatting.GRAY.getColorValue())
                        .append(Text.literal(storedStack.getCount() + "x ").withColor(color))
                        .append(Text.translatable(storedStack.getItem().getTranslationKey()).withColor(color));

                // Add to tooltip
                tooltip.add(line);

                // Calculate max width
                int lineWidth = client.textRenderer.getWidth(line);
                maxWidth = Math.max(maxWidth, lineWidth);
            }
        }

        // Check if satchel is full and center the full message
        if (getStoredStacks(stack).size() == MAX_STACKS) {
            assert Formatting.RED.getColorValue() != null;

            // Add empty line for spacing
            tooltip.add(Text.literal(""));

            // Calculate padding for centering
            Text fullMessage = Text.translatable("item.satchel.satchel_full").withColor(Formatting.RED.getColorValue());
            int fullMessageWidth = client.textRenderer.getWidth(fullMessage);
            int padding = (maxWidth - fullMessageWidth) / 2 + 1;

            // Add the centered thinga-ma-bobber
            tooltip.add(Text.literal(" ".repeat(Math.max(0, padding / client.textRenderer.getWidth(" ")))).append(fullMessage));
        }

        super.appendTooltip(stack, context, tooltip, type);
    }

    public boolean isInvalidItem(ItemStack stack) {
        // Check if the item is a bundle, satchel, or shulker box
        Item item = stack.getItem();
        return item instanceof SatchelItem || item.getTranslationKey().contains("shulker_box");
    }

    public static List<ItemStack> getStoredStacks(ItemStack satchel) {
        List<ItemStack> storedStacks = satchel.get(ModComponents.SATCHEL_STACK);
        return storedStacks != null ? storedStacks : new ArrayList<>();
    }

    public ItemStack getSelectedStack(ItemStack stack) {
        if (!Objects.requireNonNull(stack.get(ModComponents.SATCHEL_STACK)).isEmpty()
                && this.index < Objects.requireNonNull(stack.get(ModComponents.SATCHEL_STACK)).size()) {
            return Objects.requireNonNull(stack.get(ModComponents.SATCHEL_STACK)).get(this.index);
        }
        return ItemStack.EMPTY;
    }

    public void setSlot(ItemStack satchel, ItemStack otherStack) {
        List<ItemStack> storedStacks = new ArrayList<>(getStoredStacks(satchel));
        if (!Objects.requireNonNull(satchel.get(ModComponents.SATCHEL_STACK)).isEmpty()
                && this.index < Objects.requireNonNull(satchel.get(ModComponents.SATCHEL_STACK)).size()) {
            storedStacks.set(this.index, otherStack);
        } else if (Objects.requireNonNull(satchel.get(ModComponents.SATCHEL_STACK)).size() < 8 && this.index >= Objects.requireNonNull(satchel.get(ModComponents.SATCHEL_STACK)).size()) {
            storedStacks.add(otherStack);
        }
        setStoredStacks(satchel, storedStacks);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setInternalIndex(int internalIndex) {
        this.internalIndex = internalIndex;
    }

    public int getInternalIndex() {
        return internalIndex;
    }

    public static void setStoredStacks(ItemStack satchel, List<ItemStack> stacks) {
        // Remove empty stacks before setting the component
        List<ItemStack> filteredStacks = new ArrayList<>();
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                filteredStacks.add(stack.copy());
            }
        }

        // Reset the component with the filtered stacks
        satchel.set(ModComponents.SATCHEL_STACK, filteredStacks);
    }
}