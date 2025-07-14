package net.hollowed.antique.networking;

import net.hollowed.antique.Antiquities;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record DyePacketPayload(String color) implements CustomPayload {
    public static final Id<DyePacketPayload> ID = new Id<>(Identifier.of(Antiquities.MOD_ID, "dye_packet"));

    public static final PacketCodec<RegistryByteBuf, DyePacketPayload> CODEC = PacketCodec.of(DyePacketPayload::write, DyePacketPayload::new);

    public DyePacketPayload(RegistryByteBuf buf) {
        this(buf.readString());
    }

    public void write(RegistryByteBuf buf) {
        buf.writeString(color);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
