package net.hollowed.antique.networking;

import net.hollowed.antique.Antiquities;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record MyriadShovelSpawnPacketPayload(int entityId) implements CustomPayload {
    public static final Id<MyriadShovelSpawnPacketPayload> ID = new Id<>(Identifier.of(Antiquities.MOD_ID, "myriad_shovel_spawn_packet"));

    public static final PacketCodec<RegistryByteBuf, MyriadShovelSpawnPacketPayload> CODEC = PacketCodec.of(MyriadShovelSpawnPacketPayload::write, MyriadShovelSpawnPacketPayload::new);

    public MyriadShovelSpawnPacketPayload(RegistryByteBuf buf) {
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
