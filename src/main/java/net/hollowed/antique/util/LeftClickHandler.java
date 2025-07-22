package net.hollowed.antique.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class LeftClickHandler {
    public static final MinecraftClient client = MinecraftClient.getInstance();

    public static void checkRightClickInAir() {
        if (client.world == null || client.player == null) {
            return;
        }

        if (client.options.attackKey.isPressed() && client.player.getMainHandStack() != ItemStack.EMPTY) {
            if (client.player.isUsingItem()) {
                client.player.stopUsingItem();
                client.player.getItemCooldownManager().set(client.player.getMainHandStack(), 5);
                client.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }
}
