package net.hollowed.antique.mixin.accessors;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.TexturedModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TexturedModelData.class)
public interface TexturedModelDataDataAccessor {
    @Accessor("data")
    ModelData getData();
}
