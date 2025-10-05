package net.hollowed.antique.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hollowed.antique.entities.PaleWardenEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public class PaleWardenTickPacketReceiver {
    public static void registerServerPacket() {
        ServerPlayNetworking.registerGlobalReceiver(PaleWardenTickPacketPayload.ID, (payload, context) -> context.server().execute(() -> {
            Entity entity = context.player().getEntityWorld().getEntityById(payload.entityId());
            if (entity instanceof PaleWardenEntity paleWardenEntity) {
                ItemStack mainhand = payload.mainhand() == null || payload.mainhand().isEmpty() ? ItemStack.EMPTY : payload.mainhand().copy();
                ItemStack offhand = payload.offhand() == null || payload.offhand().isEmpty() ? ItemStack.EMPTY : payload.offhand().copy();
                paleWardenEntity.swapStacks(mainhand, offhand);
            }
        }));

    }
}
