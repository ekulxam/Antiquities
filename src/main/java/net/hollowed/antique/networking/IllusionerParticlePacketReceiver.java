package net.hollowed.antique.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.hollowed.antique.entities.ai.IllusionerCloneParticles;

public class IllusionerParticlePacketReceiver {
    public static void registerServerPacket() {
        ClientPlayNetworking.registerGlobalReceiver(IllusionerParticlePacketPayload.ID, (payload, context) -> context.client().execute(() -> IllusionerCloneParticles.particles(context.player().getWorld(), payload.x(), payload.y(), payload.z())));
    }
}
