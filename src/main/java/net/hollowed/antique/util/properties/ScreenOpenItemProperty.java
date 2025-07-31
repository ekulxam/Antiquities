package net.hollowed.antique.util.properties;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record ScreenOpenItemProperty() implements BooleanProperty {
    public static final MapCodec<ScreenOpenItemProperty> CODEC = MapCodec.unit(new ScreenOpenItemProperty());

    public boolean test(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
        return entity instanceof AbstractClientPlayerEntity player && player.currentScreenHandler != player.playerScreenHandler;
    }

    public MapCodec<ScreenOpenItemProperty> getCodec() {
        return CODEC;
    }
}