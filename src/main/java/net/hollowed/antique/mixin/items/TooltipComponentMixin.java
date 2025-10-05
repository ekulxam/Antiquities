package net.hollowed.antique.mixin.items;

import net.hollowed.antique.items.components.BagOfTricksTooltipComponent;
import net.hollowed.antique.items.components.SatchelTooltipComponent;
import net.hollowed.antique.items.tooltips.BagOfTricksTooltipData;
import net.hollowed.antique.items.tooltips.SatchelTooltipData;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(TooltipComponent.class)
public interface TooltipComponentMixin {

    @Inject(method = "of(Lnet/minecraft/item/tooltip/TooltipData;)Lnet/minecraft/client/gui/tooltip/TooltipComponent;", at = @At("HEAD"), cancellable = true)
    private static void of(TooltipData tooltipData, CallbackInfoReturnable<TooltipComponent> cir) {
        if (tooltipData instanceof SatchelTooltipData(List<ItemStack> contents, ItemStack stack)) {
            cir.setReturnValue(new SatchelTooltipComponent(contents, stack));
        }
        if (tooltipData instanceof BagOfTricksTooltipData(List<ItemStack> contents, ItemStack stack)) {
            cir.setReturnValue(new BagOfTricksTooltipComponent(contents, stack));
        }
    }
}
