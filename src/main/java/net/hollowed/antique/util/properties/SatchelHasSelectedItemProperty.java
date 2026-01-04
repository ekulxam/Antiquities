package net.hollowed.antique.util.properties;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.antique.items.SatchelItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record SatchelHasSelectedItemProperty() implements ConditionalItemModelProperty {
    public static final MapCodec<SatchelHasSelectedItemProperty> CODEC = MapCodec.unit(new SatchelHasSelectedItemProperty());

    public boolean get(@NotNull ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed, @NotNull ItemDisplayContext displayContext) {
        return SatchelItem.hasSelectedStack(stack);
    }

    public @NotNull MapCodec<SatchelHasSelectedItemProperty> type() {
        return CODEC;
    }
}