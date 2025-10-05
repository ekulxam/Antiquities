package net.hollowed.antique.items;

import net.hollowed.combatamenities.util.items.ModComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ClickType;

public class ClothPatternItem extends Item {
    public ClothPatternItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        ItemStack otherStack = slot.getStack();
        if (clickType == ClickType.LEFT) {
            if (otherStack.isOf(Items.INK_SAC) || otherStack.isOf(Items.GLOW_INK_SAC)) {
                addInk(player, stack, otherStack);
                return true;
            }
        }
        return super.onStackClicked(stack, slot, clickType, player);
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType == ClickType.LEFT) {
            if (otherStack.isOf(Items.INK_SAC) || otherStack.isOf(Items.GLOW_INK_SAC)) {
                addInk(player, stack, otherStack);
                return true;
            }
        }
        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    private void addInk(PlayerEntity player, ItemStack patternStack, ItemStack inkStack) {
        patternStack.set(ModComponents.BOOLEAN_PROPERTY, inkStack.isOf(Items.GLOW_INK_SAC));
        player.playSound(inkStack.isOf(Items.GLOW_INK_SAC) ? SoundEvents.ITEM_GLOW_INK_SAC_USE : SoundEvents.ITEM_INK_SAC_USE, 1.0F, 1.0F);
        inkStack.decrementUnlessCreative(1, player);
    }
}
