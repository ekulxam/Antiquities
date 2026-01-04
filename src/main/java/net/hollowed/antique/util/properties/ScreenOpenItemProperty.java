package net.hollowed.antique.util.properties;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record ScreenOpenItemProperty() implements ConditionalItemModelProperty {
    public static final MapCodec<ScreenOpenItemProperty> CODEC = MapCodec.unit(new ScreenOpenItemProperty());

    public boolean get(@NotNull ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed, @NotNull ItemDisplayContext displayContext) {
        return entity instanceof AbstractClientPlayer player && player.containerMenu != player.inventoryMenu;
    }

    public @NotNull MapCodec<ScreenOpenItemProperty> type() {
        return CODEC;
    }
}