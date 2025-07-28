package net.hollowed.antique.mixin.items;

import net.hollowed.antique.items.components.BagOfTricksTooltipComponent;
import net.hollowed.antique.items.components.SatchelTooltipComponent;
import net.hollowed.antique.items.tooltips.BagOfTricksTooltipData;
import net.hollowed.antique.items.tooltips.SatchelTooltipData;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.tooltip.TooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TooltipComponent.class)
public interface TooltipComponentMixin {

    @Inject(method = "of(Lnet/minecraft/item/tooltip/TooltipData;)Lnet/minecraft/client/gui/tooltip/TooltipComponent;", at = @At("HEAD"), cancellable = true)
    private static void of(TooltipData tooltipData, CallbackInfoReturnable<TooltipComponent> cir) {
        if (tooltipData instanceof SatchelTooltipData satchelTooltipData) {
            cir.setReturnValue(new SatchelTooltipComponent(satchelTooltipData.contents(), satchelTooltipData.stack()));
        }
        if (tooltipData instanceof BagOfTricksTooltipData bagOfTricksTooltipData) {
            cir.setReturnValue(new BagOfTricksTooltipComponent(bagOfTricksTooltipData.contents(), bagOfTricksTooltipData.stack()));
        }
    }
}
