package net.hollowed.antique.mixin;

import net.hollowed.antique.client.pedestal.PedestalTooltipRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class HUDOverylayMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void renderOverlays(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.world != null && client.player != null && client.player.isSneaking()) {
            int screenWidth = client.getWindow().getScaledWidth();
            int screenHeight = client.getWindow().getScaledHeight();
            PedestalTooltipRenderer.renderTooltip(context, screenWidth, screenHeight);
        }
    }
}
