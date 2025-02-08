package net.hollowed.antique.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hollowed.antique.entities.custom.MyriadShovelEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

import java.util.List;

public class MyriadShovelSpawnPacketReceiver {
    public static void registerServerPacket() {
        ServerPlayNetworking.registerGlobalReceiver(MyriadShovelSpawnPacketPayload.ID, (payload, context) -> context.server().execute(() -> {
            PlayerEntity player = context.player();
            List<Entity> list = player.getWorld().getOtherEntities(null, new Box(player.getX() - 64, player.getY() - 64, player.getZ() - 64,
                    player.getX() + 64, player.getY() + 64, player.getZ() + 64));
            for (Entity entity : list) {
                if (entity instanceof MyriadShovelEntity paleWardenEntity && paleWardenEntity.age < 2) {
                    System.out.println("ran this code over here too");
                    paleWardenEntity.summonPart();
                    break;
                }
            }
        }));
    }
}
