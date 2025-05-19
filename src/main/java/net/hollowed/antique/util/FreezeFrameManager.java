package net.hollowed.antique.util;

import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.hollowed.antique.client.hud.ParryScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class FreezeFrameManager {
    private static int freezeTicks = 0;
    private static int overlayTime = 0;

    // Freeze Frame Overlay
    private static HudLayerRegistrationCallback overlay;

    public static void triggerFreeze(int duration) {
        FreezeFrameManager.freezeTicks = duration;
    }

    public static void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (freezeTicks > 0) {
            freezeTicks--;
            client.setScreen(new ParryScreen(Text.literal("")));
        } else if (freezeTicks == 0) {
            client.setScreen(null);
            freezeTicks = -1;
        }
        if (overlayTime > 0) {
            overlayTime--;
        }
    }

    public static HudLayerRegistrationCallback getOverlay() {
        return overlay;
    }
}
