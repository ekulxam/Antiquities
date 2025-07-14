package net.hollowed.antique.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hollowed.antique.blocks.custom.screen.DyeingScreenHandler;

public class DyePacketReceiver {
    public static void registerServerPacket() {
        ServerPlayNetworking.registerGlobalReceiver(DyePacketPayload.ID, (payload, context) -> context.server().execute(() -> {
            if (context.player().currentScreenHandler instanceof DyeingScreenHandler screen) {
                screen.setHexCode(payload.color());
            }
        }));
    }
}
