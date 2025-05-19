package net.hollowed.antique.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hollowed.antique.entities.ai.IllusionerCloneParticles;
import net.hollowed.antique.util.Crawl;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.Objects;

public class IllusionerParticlePacketReceiver {
    public static void registerServerPacket() {
        ClientPlayNetworking.registerGlobalReceiver(IllusionerParticlePacketPayload.ID, (payload, context) -> context.client().execute(() -> {
            IllusionerCloneParticles.particles(context.player().getWorld(), payload.x(), payload.y(), payload.z());
        }));
    }
}
