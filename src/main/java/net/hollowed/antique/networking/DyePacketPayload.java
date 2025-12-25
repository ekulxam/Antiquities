package net.hollowed.antique.networking;

import net.hollowed.antique.Antiquities;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record DyePacketPayload(String color) implements CustomPacketPayload {
    public static final Type<DyePacketPayload> ID = new Type<>(Identifier.fromNamespaceAndPath(Antiquities.MOD_ID, "dye_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DyePacketPayload> CODEC = StreamCodec.ofMember(DyePacketPayload::write, DyePacketPayload::new);

    public DyePacketPayload(RegistryFriendlyByteBuf buf) {
        this(buf.readUtf());
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeUtf(color);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
