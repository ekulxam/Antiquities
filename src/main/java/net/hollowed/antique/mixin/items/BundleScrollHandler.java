package net.hollowed.antique.mixin.items;

import net.minecraft.client.gui.tooltip.BundleTooltipSubmenuHandler;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Debug(export = true)
@Mixin(BundleTooltipSubmenuHandler.class)
public class BundleScrollHandler {

    @ModifyVariable(method = "onScroll", at = @At(value = "STORE", ordinal = 0), ordinal = 2)
    private int modifyScrollAmount(int original) {
        return original * -1;
    }
}