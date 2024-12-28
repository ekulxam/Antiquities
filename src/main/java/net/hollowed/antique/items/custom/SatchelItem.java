package net.hollowed.antique.items.custom;

import net.hollowed.antique.component.ModComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;

import java.util.List;

public class SatchelItem extends Item {
    private static final int MAX_STACKS = 8;
    private int index = 0; // Int from 0 to 7

    public SatchelItem(ArmorMaterial material, EquipmentType type, Item.Settings settings) {
        super(material.applySettings(settings, type));
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType == ClickType.RIGHT) {
            // Fetch stored stacks
            List<ItemStack> storedStacks = getStoredStacks(stack);

            if (otherStack.isEmpty()) {
                // Attempt to remove the first non-empty stack
                for (int i = 0; i < storedStacks.size(); i++) {
                    ItemStack storedStack = storedStacks.get(i);
                    if (!storedStack.isEmpty()) {
                        cursorStackReference.set(storedStack.copy());
                        storedStacks.set(i, ItemStack.EMPTY);
                        player.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 1.0F, 1.0F);
                        break;
                    }
                }
            } else {
                // Attempt to add `otherStack` to the first empty slot
                for (int i = 0; i < storedStacks.size(); i++) {
                    if (storedStacks.get(i).isEmpty()) {
                        storedStacks.set(i, otherStack.split(otherStack.getCount()));
                        player.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 1.0F, 1.0F);
                        cursorStackReference.set(ItemStack.EMPTY);
                        break;
                    }
                }
            }

            // Save updated stacks
            setStoredStacks(stack, storedStacks);
            return true;
        }

        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        List<ItemStack> storedStacks = getStoredStacks(stack);

        for (ItemStack storedStack : storedStacks) {
            if (!storedStack.isEmpty()) {
                tooltip.add(Text.literal(" - ").withColor(Formatting.GRAY.getColorValue()).append(Text.literal(storedStack.getCount() + "x ").withColor(storedStack.getRarity().getFormatting().getColorValue()))
                        .append(Text.translatable(storedStack.getItem().getTranslationKey()).withColor(storedStack.getRarity().getFormatting().getColorValue())));
            } else {
                tooltip.add(Text.literal(" - ").withColor(Formatting.GRAY.getColorValue()));
            }
        }
        super.appendTooltip(stack, context, tooltip, type);
    }

    public List<ItemStack> getStoredStacks(ItemStack stack) {
        // Retrieve the component, ensuring it’s initialized correctly
        return stack.get(ModComponents.SATCHEL_STACK);
    }

    public void setStoredStacks(ItemStack stack, List<ItemStack> storedStacks) {
        // Update the component with the modified stack list
        stack.set(ModComponents.SATCHEL_STACK, storedStacks);
    }


    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public ItemStack getSelectedStack(ItemStack satchel) {
        List<ItemStack> storedStacks = getStoredStacks(satchel); // Always returns a properly sized list
        return storedStacks.get(this.index);
    }

    public void setSlot(ItemStack satchel, ItemStack newStack) {
        List<ItemStack> storedStacks = getStoredStacks(satchel); // Always returns a properly sized list

        if (this.index >= 0 && this.index < MAX_STACKS) {
            storedStacks.set(this.index, newStack != null ? newStack : ItemStack.EMPTY);
        }

        setStoredStacks(satchel, storedStacks); // Reapply the fixed list back to the satchel
    }
}