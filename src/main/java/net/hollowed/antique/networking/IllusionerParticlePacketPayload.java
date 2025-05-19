package net.hollowed.antique.networking;

import net.hollowed.antique.Antiquities;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record IllusionerParticlePacketPayload(double x, double y, double z) implements CustomPayload {
    public static final Id<IllusionerParticlePacketPayload> ID = new Id<>(Identifier.of(Antiquities.MOD_ID, "illusioner_particle_packet"));

    public static final PacketCodec<RegistryByteBuf, IllusionerParticlePacketPayload> CODEC = PacketCodec.of(IllusionerParticlePacketPayload::write, IllusionerParticlePacketPayload::new);

    public IllusionerParticlePacketPayload(RegistryByteBuf buf) {
        this(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public void write(RegistryByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
