package net.hollowed.antique.networking;

import net.hollowed.antique.Antiquities;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record PaleWardenTickPacketPayload(int entityId, ItemStack mainhand, ItemStack offhand) implements CustomPayload {
    public static final Id<PaleWardenTickPacketPayload> ID = new Id<>(Identifier.of(Antiquities.MOD_ID, "pale_warden_tick_packet"));

    public static final PacketCodec<RegistryByteBuf, PaleWardenTickPacketPayload> CODEC = PacketCodec.of(PaleWardenTickPacketPayload::write, PaleWardenTickPacketPayload::new);

    public PaleWardenTickPacketPayload(RegistryByteBuf buf) {
        this(buf.readInt(), decodeStack(buf), decodeStack(buf));
    }

    private static ItemStack decodeStack(RegistryByteBuf buf) {
        return buf.readBoolean() ? ItemStack.PACKET_CODEC.decode(buf) : ItemStack.EMPTY;
    }

    public void write(RegistryByteBuf buf) {
        buf.writeInt(entityId);
        boolean hasItem = !mainhand.isEmpty();
        buf.writeBoolean(hasItem);
        if (hasItem) {
            ItemStack.PACKET_CODEC.encode(buf, mainhand);
        }
        boolean hasItem1 = !offhand.isEmpty();
        buf.writeBoolean(hasItem1);
        if (hasItem1) {
            ItemStack.PACKET_CODEC.encode(buf, offhand);
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
