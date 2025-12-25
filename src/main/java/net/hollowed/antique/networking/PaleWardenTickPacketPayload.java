package net.hollowed.antique.networking;

import net.hollowed.antique.Antiquities;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record PaleWardenTickPacketPayload(int entityId, ItemStack mainhand, ItemStack offhand) implements CustomPacketPayload {
    public static final Type<PaleWardenTickPacketPayload> ID = new Type<>(Identifier.fromNamespaceAndPath(Antiquities.MOD_ID, "pale_warden_tick_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PaleWardenTickPacketPayload> CODEC = StreamCodec.ofMember(PaleWardenTickPacketPayload::write, PaleWardenTickPacketPayload::new);

    public PaleWardenTickPacketPayload(RegistryFriendlyByteBuf buf) {
        this(buf.readInt(), decodeStack(buf), decodeStack(buf));
    }

    private static ItemStack decodeStack(RegistryFriendlyByteBuf buf) {
        return buf.readBoolean() ? ItemStack.STREAM_CODEC.decode(buf) : ItemStack.EMPTY;
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(entityId);
        boolean hasItem = !mainhand.isEmpty();
        buf.writeBoolean(hasItem);
        if (hasItem) {
            ItemStack.STREAM_CODEC.encode(buf, mainhand);
        }
        boolean hasItem1 = !offhand.isEmpty();
        buf.writeBoolean(hasItem1);
        if (hasItem1) {
            ItemStack.STREAM_CODEC.encode(buf, offhand);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
