package net.hollowed.antique.util.resources;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.resources.Identifier;

public record ClothSkinData(
        List<ClothSubData> list
) {
    public static final Codec<ClothSkinData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ClothSubData.CODEC.listOf().fieldOf("skins").orElseGet(() -> List.of(new ClothSubData(Identifier.parse(""), "", 0, 0, 0, 0, false, false))).forGetter(ClothSkinData::list)
    ).apply(instance, ClothSkinData::new));

    public record ClothSubData(Identifier model, String hex, float length, float width, int bodyAmount, int light, boolean overlay, boolean dyeable) {
        public static final Codec<ClothSubData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("model").forGetter(ClothSubData::model),
                Codec.STRING.fieldOf("color").orElse("d13a68").forGetter(ClothSubData::hex),
                Codec.FLOAT.fieldOf("length").orElse(0.0F).forGetter(ClothSubData::length),
                Codec.FLOAT.fieldOf("width").orElse(0.0F).forGetter(ClothSubData::width),
                Codec.INT.fieldOf("bodies").orElse(0).forGetter(ClothSubData::bodyAmount),
                Codec.INT.fieldOf("light").orElse(0).forGetter(ClothSubData::light),
                Codec.BOOL.fieldOf("overlay").orElse(false).forGetter(ClothSubData::overlay),
                Codec.BOOL.fieldOf("dyeable").orElse(false).forGetter(ClothSubData::dyeable)
        ).apply(instance, ClothSubData::new));
    }
}
