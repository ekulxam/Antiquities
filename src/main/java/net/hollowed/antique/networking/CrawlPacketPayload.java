package net.hollowed.antique.networking;

import net.hollowed.antique.Antiquities;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record CrawlPacketPayload(boolean crawling) implements CustomPacketPayload {
    public static final Type<CrawlPacketPayload> ID = new Type<>(Identifier.fromNamespaceAndPath(Antiquities.MOD_ID, "crawl_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CrawlPacketPayload> CODEC = StreamCodec.ofMember(CrawlPacketPayload::write, CrawlPacketPayload::new);

    public CrawlPacketPayload(RegistryFriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(crawling);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
