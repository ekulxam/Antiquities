package net.hollowed.antique.util.resources;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record ClothSkinData(
        List<ClothSubData> list
) {
    public static final Codec<ClothSkinData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ClothSubData.CODEC.listOf().fieldOf("skins").orElseGet(() -> List.of(new ClothSubData("", "", 0, 0, 0, 0))).forGetter(ClothSkinData::list)
    ).apply(instance, ClothSkinData::new));

    public record ClothSubData(String texture, String hex, float length, float width, int bodyAmount, int light) {
        public static final Codec<ClothSubData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("texture").forGetter(ClothSubData::texture),
                Codec.STRING.fieldOf("color").forGetter(ClothSubData::hex),
                Codec.FLOAT.fieldOf("length").orElse(0.0F).forGetter(ClothSubData::length),
                Codec.FLOAT.fieldOf("width").orElse(0.0F).forGetter(ClothSubData::width),
                Codec.INT.fieldOf("bodies").orElse(0).forGetter(ClothSubData::bodyAmount),
                Codec.INT.fieldOf("light").orElse(0).forGetter(ClothSubData::light)
        ).apply(instance, ClothSubData::new));
    }
}
