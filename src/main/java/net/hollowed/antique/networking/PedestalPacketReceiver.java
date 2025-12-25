package net.hollowed.antique.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.hollowed.antique.blocks.entities.PedestalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PedestalPacketReceiver {
    public static void registerClientPacket() {
        ClientPlayNetworking.registerGlobalReceiver(PedestalPacketPayload.ID, (payload, context) -> context.client().execute(() -> {
                Level world = context.player().level();
                BlockPos pos = payload.blockPos();
                ItemStack stack = payload.stack();

                BlockEntity entity = world.getBlockEntity(pos);
                if (entity instanceof PedestalBlockEntity) {
                    ((PedestalBlockEntity) entity).setItem(0, stack);
                    world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), Block.UPDATE_ALL);
                }
        }));
    }
}
