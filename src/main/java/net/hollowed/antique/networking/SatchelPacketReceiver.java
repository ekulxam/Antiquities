package net.hollowed.antique.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hollowed.antique.component.ModComponents;
import net.hollowed.antique.component.SatchelInventoryComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class SatchelPacketReceiver {

    public static void registerServerPacket() {

        ServerPlayNetworking.registerGlobalReceiver(SatchelPacketPayload.ID, (payload, context) -> {

            PlayerEntity player = context.player();

            SatchelInventoryComponent satchelInventory = player.getEquippedStack(EquipmentSlot.LEGS).get(ModComponents.SATCHEL_INVENTORY);

            assert satchelInventory != null;
            PlayerInventory playerInventory = player.getInventory();

            int currentHotbarSlot = playerInventory.selectedSlot;
            int currentSatchelSlot = satchelInventory.getSelectedStack();

            ItemStack currentHotbarStack = playerInventory.getStack(currentHotbarSlot);
            ItemStack currentSatchelStack = satchelInventory.getStack(currentSatchelSlot);

            if (currentSatchelStack != null) {
                playerInventory.setStack(currentHotbarSlot, currentSatchelStack);
            } else {
                playerInventory.removeStack(currentHotbarSlot);
            }
            if (currentHotbarStack != null && currentHotbarStack.getItem() != Items.AIR) {
                satchelInventory.setStack(currentSatchelSlot, currentHotbarStack);
            } else {
                satchelInventory.removeStack(currentSatchelSlot);
            }
        });

    }

}
