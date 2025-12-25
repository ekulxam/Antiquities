package net.hollowed.antique.items;

import net.hollowed.combatamenities.util.items.CAComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ClothPatternItem extends Item {
    public ClothPatternItem(Properties settings) {
        super(settings);
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction clickType, Player player) {
        ItemStack otherStack = slot.getItem();
        if (clickType == ClickAction.PRIMARY) {
            if (otherStack.is(Items.INK_SAC) || otherStack.is(Items.GLOW_INK_SAC)) {
                addInk(player, stack, otherStack);
                return true;
            }
        }
        return super.overrideStackedOnOther(stack, slot, clickType, player);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack otherStack, Slot slot, ClickAction clickType, Player player, SlotAccess cursorStackReference) {
        if (clickType == ClickAction.PRIMARY) {
            if (otherStack.is(Items.INK_SAC) || otherStack.is(Items.GLOW_INK_SAC)) {
                addInk(player, stack, otherStack);
                return true;
            }
        }
        return super.overrideOtherStackedOnMe(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    private void addInk(Player player, ItemStack patternStack, ItemStack inkStack) {
        patternStack.set(CAComponents.BOOLEAN_PROPERTY, inkStack.is(Items.GLOW_INK_SAC));
        player.playSound(inkStack.is(Items.GLOW_INK_SAC) ? SoundEvents.GLOW_INK_SAC_USE : SoundEvents.INK_SAC_USE, 1.0F, 1.0F);
        inkStack.consume(1, player);
    }
}
