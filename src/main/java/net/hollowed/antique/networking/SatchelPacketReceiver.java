package net.hollowed.antique.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hollowed.antique.items.custom.SatchelItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class SatchelPacketReceiver {

    public static void registerServerPacket() {

        ServerPlayNetworking.registerGlobalReceiver(SatchelPacketPayload.ID, (payload, context) -> {

            PlayerEntity player = context.player();

            ItemStack satchelInventory = player.getEquippedStack(EquipmentSlot.LEGS);

            assert satchelInventory != null;
            if (satchelInventory.getItem() instanceof SatchelItem satchelItem) {
                PlayerInventory playerInventory = player.getInventory();

                int currentHotbarSlot = playerInventory.selectedSlot;

                ItemStack currentHotbarStack = playerInventory.getStack(currentHotbarSlot);
                ItemStack currentSatchelStack = satchelItem.getSelectedStack(satchelInventory);

                if (currentSatchelStack.isEmpty() && currentHotbarStack.isEmpty()) {
                    player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_BUNDLE_INSERT_FAIL, SoundCategory.PLAYERS, 1.0F, 1.0F);
                }

                // Move the selected satchel stack to the hotbar
                if (!currentSatchelStack.isEmpty()) {
                    playerInventory.setStack(currentHotbarSlot, currentSatchelStack);
                    player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_BUNDLE_REMOVE_ONE, SoundCategory.PLAYERS, 1.0F, 1.0F);
                } else {
                    playerInventory.removeStack(currentHotbarSlot);
                }

                // Update the satchel's slot with the hotbar item
                if (!currentHotbarStack.isEmpty()) {
                    satchelItem.setSlot(satchelInventory, currentHotbarStack);
                    player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_BUNDLE_INSERT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                } else {
                    satchelItem.setSlot(satchelInventory, ItemStack.EMPTY);
                }

                // Ensure the satchel data is saved
                // If you're using a custom data component or a system for persistence, make sure to trigger a sync
            }
        });
    }
}