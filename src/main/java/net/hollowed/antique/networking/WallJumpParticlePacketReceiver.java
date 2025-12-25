package net.hollowed.antique.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class WallJumpParticlePacketReceiver {
    public static void registerClientPacket() {
        ClientPlayNetworking.registerGlobalReceiver(WallJumpParticlePacketPayload.ID, (payload, context) -> context.client().execute(() -> WallJumpPacketReceiver.particles(context.player().level(), payload.x(), payload.y(), payload.z(), payload.particleX(), payload.particleZ(), payload.pushVector())));
    }
}
