package net.hollowed.antique.mixin;

import net.hollowed.antique.util.FreezeFrameManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class FreezeFrameMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        if (FreezeFrameManager.isFrozen() && !FreezeFrameManager.isUltrakill()) {
            FreezeFrameManager.renderFreeze();
            ci.cancel(); // Prevent further rendering updates
        }
    }
}
