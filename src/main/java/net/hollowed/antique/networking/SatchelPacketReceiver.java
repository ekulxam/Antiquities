package net.hollowed.antique.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hollowed.antique.index.AntiqueItems;
import net.hollowed.antique.items.MyriadToolBitItem;
import net.hollowed.antique.items.MyriadToolItem;
import net.hollowed.antique.items.SatchelItem;
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

            if (satchelInventory == null) return;
            if (satchelInventory.getItem() instanceof SatchelItem satchelItem) {
                PlayerInventory playerInventory = player.getInventory();

                int currentHotbarSlot = playerInventory.getSelectedSlot();

                ItemStack currentHotbarStack = playerInventory.getStack(currentHotbarSlot);
                ItemStack currentSatchelStack = satchelItem.getSelectedStack(satchelInventory);

                ItemStack myriadItem = ItemStack.EMPTY;

                if (!satchelItem.isInvalidItem(currentHotbarStack)) {
                    if (currentSatchelStack.isEmpty() && currentHotbarStack.isEmpty()) {
                        player.getEntityWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_BUNDLE_INSERT_FAIL, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    }

                    // Move the selected satchel stack to the hotbar
                    if (!currentSatchelStack.isEmpty()) {
                        if (currentHotbarStack.getItem() instanceof MyriadToolItem && !MyriadToolItem.isInvalidItem(currentSatchelStack) && !currentSatchelStack.isOf(AntiqueItems.MYRIAD_CLAW)) {
                            myriadItem = MyriadToolItem.getStoredStack(currentHotbarStack);
                            MyriadToolItem.setStoredStack(currentHotbarStack, currentSatchelStack);
                            player.getEntityWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_BUNDLE_REMOVE_ONE, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        } else {
                            playerInventory.setStack(currentHotbarSlot, currentSatchelStack);
                            player.getEntityWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_BUNDLE_REMOVE_ONE, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        }
                    } else if (currentHotbarStack.getItem() instanceof MyriadToolItem) {
                        myriadItem = MyriadToolItem.getStoredStack(currentHotbarStack);
                        if (myriadItem.isEmpty()) {
                            satchelItem.setSlot(satchelInventory, myriadItem);
                            if (currentSatchelStack.isEmpty()) {
                                playerInventory.removeStack(currentHotbarSlot);
                            } else {
                                MyriadToolItem.setStoredStack(currentHotbarStack, ItemStack.EMPTY);
                            }
                        } else {
                            MyriadToolItem.setStoredStack(currentHotbarStack, ItemStack.EMPTY);
                        }
                    } else {
                        playerInventory.removeStack(currentHotbarSlot);
                    }

                    // Update the satchel's slot with the hotbar item
                    if (!currentHotbarStack.isEmpty()) {
                        if ((currentHotbarStack.getItem() instanceof MyriadToolItem)) {
                            if (myriadItem == ItemStack.EMPTY) {
                                if (currentSatchelStack.getItem() instanceof MyriadToolBitItem && !currentSatchelStack.isOf(AntiqueItems.MYRIAD_CLAW)) {
                                    satchelItem.setSlot(satchelInventory, ItemStack.EMPTY);
                                } else {
                                    satchelItem.setSlot(satchelInventory, currentHotbarStack);
                                }
                            } else {
                                satchelItem.setSlot(satchelInventory, myriadItem);
                            }
                        } else {
                            satchelItem.setSlot(satchelInventory, currentHotbarStack);
                        }
                        player.getEntityWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_BUNDLE_INSERT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    } else  {
                        satchelItem.setSlot(satchelInventory, ItemStack.EMPTY);
                    }
                } else {
                    player.getEntityWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_BUNDLE_INSERT_FAIL, SoundCategory.PLAYERS, 1.0F, 1.0F);
                }
            }
        });
    }
}