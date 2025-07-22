package net.hollowed.antique.util.resources;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.util.List;

public record MyriadStaffTransformData(
        String item,
        Identifier model,
        List<Float> scale,
        List<Float> rotation,
        List<Float> translation
) {
    public static final Codec<MyriadStaffTransformData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("item").forGetter(MyriadStaffTransformData::item),
            Identifier.CODEC.fieldOf("model").orElseGet(() -> Identifier.of("default")).forGetter(MyriadStaffTransformData::model),
            Codec.FLOAT.listOf().fieldOf("scale").orElseGet(() -> List.of(1.0f, 1.0f, 1.0f)).forGetter(MyriadStaffTransformData::scale),
            Codec.FLOAT.listOf().fieldOf("rotation").orElseGet(() -> List.of(0.0f, 0.0f, 0.0f)).forGetter(MyriadStaffTransformData::rotation),
            Codec.FLOAT.listOf().fieldOf("translation").orElseGet(() -> List.of(0.0f, 0.0f, 0.0f)).forGetter(MyriadStaffTransformData::translation)
    ).apply(instance, MyriadStaffTransformData::new));
}
