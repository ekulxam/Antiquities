package net.hollowed.antique.util;

import net.minecraft.client.MinecraftClient;

public class FreezeFrameManager {
    private static int freezeTicks = 0;
    private static int ultrakill = 0;

    public static void triggerFreeze(int duration, boolean ultrakill) {
        FreezeFrameManager.freezeTicks = duration;
        if (ultrakill) {
            FreezeFrameManager.ultrakill = 2;
        }
    }

    public static boolean isFrozen() {
        return freezeTicks > 0;
    }

    public static boolean isUltrakill() {
        return ultrakill > 0;
    }

    public static void tick() {
        if (freezeTicks > 0) {
            freezeTicks--;
        }
        if (ultrakill > 0) {
            ultrakill--;
        }
    }

    public static void renderFreeze() {
        if (freezeTicks > 0) {
            // Pause the game's frame progression
            MinecraftClient client = MinecraftClient.getInstance();
            client.getFramebuffer().beginWrite(false);
        }
    }
}
