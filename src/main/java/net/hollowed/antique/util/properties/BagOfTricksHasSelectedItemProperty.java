package net.hollowed.antique.util.properties;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.antique.items.BagOfTricksItem;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record BagOfTricksHasSelectedItemProperty() implements BooleanProperty {
    public static final MapCodec<BagOfTricksHasSelectedItemProperty> CODEC = MapCodec.unit(new BagOfTricksHasSelectedItemProperty());

    public boolean test(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
        return BagOfTricksItem.hasSelectedStack(stack);
    }

    public MapCodec<BagOfTricksHasSelectedItemProperty> getCodec() {
        return CODEC;
    }
}