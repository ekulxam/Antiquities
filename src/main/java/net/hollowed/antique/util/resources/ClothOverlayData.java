package net.hollowed.antique.util.resources;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.resources.Identifier;

public record ClothOverlayData(
        List<Identifier> list
) {
    public static final Codec<ClothOverlayData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.listOf().fieldOf("overlays").orElseGet(() -> List.of(Identifier.parse(""))).forGetter(ClothOverlayData::list)
    ).apply(instance, ClothOverlayData::new));
}
