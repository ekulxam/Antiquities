package net.hollowed.antique.mixin.items;

import net.hollowed.antique.index.AntiqueKeyBindings;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class PreventItemActionsMixin {

    @Inject(method = {"use", "useOnBlock", "useOnEntity"}, at = @At("HEAD"), cancellable = true)
    public void preventInteract(CallbackInfoReturnable<ActionResult> cir) {
        if (AntiqueKeyBindings.showSatchel.isPressed()) {
            cir.setReturnValue(ActionResult.FAIL);
        }
    }
}
