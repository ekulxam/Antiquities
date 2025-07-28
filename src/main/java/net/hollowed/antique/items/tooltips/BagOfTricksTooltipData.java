package net.hollowed.antique.items.tooltips;

import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;

import java.util.List;

public record BagOfTricksTooltipData(List<ItemStack> contents, ItemStack stack) implements TooltipData {

    public List<ItemStack> contents() {
        return this.contents;
    }

    public ItemStack stack() {
        return this.stack;
    }
}