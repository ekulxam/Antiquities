package net.hollowed.antique.mixin.items;

import net.hollowed.antique.index.AntiqueKeyBindings;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class PreventItemActionsMixin {

    @Inject(method = {"use", "useOn", "interactLivingEntity"}, at = @At("HEAD"), cancellable = true)
    public void preventInteract(CallbackInfoReturnable<InteractionResult> cir) {
        if (AntiqueKeyBindings.showSatchel.isDown()) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}
