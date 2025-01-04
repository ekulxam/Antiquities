package net.hollowed.antique.mixin;

import net.hollowed.antique.ModKeyBindings;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class PreventItemActionsMixin {

    @Inject(method = {"use", "useOnBlock", "useOnEntity"}, at = @At("HEAD"), cancellable = true)
    public void preventInteract(CallbackInfoReturnable<ActionResult> cir) {
        if (ModKeyBindings.showSatchel.isPressed()) {
            cir.setReturnValue(ActionResult.FAIL);
        }
    }
}
