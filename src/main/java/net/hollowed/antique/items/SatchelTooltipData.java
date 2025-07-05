package net.hollowed.antique.items;

import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;

import java.util.List;

public record SatchelTooltipData(List<ItemStack> contents) implements TooltipData {
    public SatchelTooltipData(List<ItemStack> contents) {
        this.contents = contents;
    }

    public List<ItemStack> contents() {
        return this.contents;
    }
}