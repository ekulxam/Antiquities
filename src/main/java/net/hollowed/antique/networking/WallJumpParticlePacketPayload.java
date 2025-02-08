package net.hollowed.antique.networking;

import net.hollowed.antique.Antiquities;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public record WallJumpParticlePacketPayload(float x, float y, float z, float particleX, float particleZ, Vec3d pushVector) implements CustomPayload {
    public static final Id<WallJumpParticlePacketPayload> ID = new Id<>(Identifier.of(Antiquities.MOD_ID, "wall_jump_particle_packet"));

    public static final PacketCodec<RegistryByteBuf, WallJumpParticlePacketPayload> CODEC = PacketCodec.of(WallJumpParticlePacketPayload::write, WallJumpParticlePacketPayload::new);

    public WallJumpParticlePacketPayload(RegistryByteBuf buf) {
        this(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readVec3d());
    }

    public void write(RegistryByteBuf buf) {
        buf.writeFloat(x);
        buf.writeFloat(y);
        buf.writeFloat(z);
        buf.writeFloat(particleX);
        buf.writeFloat(particleZ);
        buf.writeVec3d(pushVector);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
