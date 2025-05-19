package net.hollowed.antique.client.hud;

import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.LayeredDrawerWrapper;
import net.hollowed.antique.util.FreezeFrameManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class DefaultOverlay implements HudLayerRegistrationCallback {

    @Override
    public void register(LayeredDrawerWrapper layeredDrawer) {
        if (FreezeFrameManager.getOverlay() != null) {
            FreezeFrameManager.getOverlay().register(layeredDrawer);
        }
    }
}
