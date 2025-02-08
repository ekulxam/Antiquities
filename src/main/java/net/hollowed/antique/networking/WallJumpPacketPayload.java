package net.hollowed.antique.networking;

import net.hollowed.antique.Antiquities;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record WallJumpPacketPayload(int entityId) implements CustomPayload {
    public static final Id<WallJumpPacketPayload> ID = new Id<>(Identifier.of(Antiquities.MOD_ID, "wall_jump_packet"));

    public static final PacketCodec<RegistryByteBuf, WallJumpPacketPayload> CODEC = PacketCodec.of(WallJumpPacketPayload::write, WallJumpPacketPayload::new);

    public WallJumpPacketPayload(RegistryByteBuf buf) {
        this(buf.readInt());
    }

    public void write(RegistryByteBuf buf) {
        buf.writeInt(entityId);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
