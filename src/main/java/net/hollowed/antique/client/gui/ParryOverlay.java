package net.hollowed.antique.client.gui;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.util.FreezeFrameManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

public class ParryOverlay implements HudRenderCallback {

    private static final Identifier PARRY_OVERLAY = Antiquities.id("textures/gui/parry.png");

    @Override
    public void onHudRender(DrawContext context, RenderTickCounter counter) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client == null || client.player == null) {
            return; // Ensure the client and player are valid
        }

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        // Draw the texture stretched over the entire screen
        if (FreezeFrameManager.isUltrakill()) {
            context.drawTexture(RenderLayer::getGuiTextured, PARRY_OVERLAY, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);
        }
    }
}
