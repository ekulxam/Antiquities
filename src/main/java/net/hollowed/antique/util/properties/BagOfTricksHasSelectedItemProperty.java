package net.hollowed.antique.util.properties;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.antique.items.BagOfTricksItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record BagOfTricksHasSelectedItemProperty() implements ConditionalItemModelProperty {
    public static final MapCodec<BagOfTricksHasSelectedItemProperty> CODEC = MapCodec.unit(new BagOfTricksHasSelectedItemProperty());

    public boolean get(ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
        return BagOfTricksItem.hasSelectedStack(stack);
    }

    public MapCodec<BagOfTricksHasSelectedItemProperty> type() {
        return CODEC;
    }
}