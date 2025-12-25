package net.hollowed.antique.networking;

import net.hollowed.antique.Antiquities;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record WallJumpPacketPayload(int entityId) implements CustomPacketPayload {
    public static final Type<WallJumpPacketPayload> ID = new Type<>(Identifier.fromNamespaceAndPath(Antiquities.MOD_ID, "wall_jump_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, WallJumpPacketPayload> CODEC = StreamCodec.ofMember(WallJumpPacketPayload::write, WallJumpPacketPayload::new);

    public WallJumpPacketPayload(RegistryFriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(entityId);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
