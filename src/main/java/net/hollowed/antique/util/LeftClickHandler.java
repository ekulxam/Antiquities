package net.hollowed.antique.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class LeftClickHandler {
    public static final Minecraft client = Minecraft.getInstance();

    public static void checkRightClickInAir() {
        if (client.level == null || client.player == null) {
            return;
        }

        if (client.options.keyAttack.isDown() && client.player.getMainHandItem() != ItemStack.EMPTY) {
            if (client.player.isUsingItem()) {
                client.player.releaseUsingItem();
                client.player.getCooldowns().addCooldown(client.player.getMainHandItem(), 5);
                client.player.swing(InteractionHand.MAIN_HAND);
            }
        }
    }
}
