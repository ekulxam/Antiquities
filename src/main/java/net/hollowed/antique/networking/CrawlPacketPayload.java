package net.hollowed.antique.networking;

import net.hollowed.antique.Antiquities;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record CrawlPacketPayload(boolean crawling) implements CustomPayload {
    public static final Id<CrawlPacketPayload> ID = new Id<>(Identifier.of(Antiquities.MOD_ID, "crawl_packet"));

    public static final PacketCodec<RegistryByteBuf, CrawlPacketPayload> CODEC = PacketCodec.of(CrawlPacketPayload::write, CrawlPacketPayload::new);

    public CrawlPacketPayload(RegistryByteBuf buf) {
        this(buf.readBoolean());
    }

    public void write(RegistryByteBuf buf) {
        buf.writeBoolean(crawling);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
