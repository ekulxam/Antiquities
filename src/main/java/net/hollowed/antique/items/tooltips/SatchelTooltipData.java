package net.hollowed.antique.items.tooltips;

import java.util.List;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public record SatchelTooltipData(List<ItemStack> contents, ItemStack stack) implements TooltipComponent {
    public ItemStack stack() {
        return this.stack;
    }
}