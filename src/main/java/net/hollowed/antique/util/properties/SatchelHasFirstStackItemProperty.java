package net.hollowed.antique.util.properties;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.antique.index.AntiqueDataComponentTypes;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Environment(EnvType.CLIENT)
public record SatchelHasFirstStackItemProperty() implements BooleanProperty {
    public static final MapCodec<SatchelHasFirstStackItemProperty> CODEC = MapCodec.unit(new SatchelHasFirstStackItemProperty());

    public boolean test(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
        List<ItemStack> list = stack.getOrDefault(AntiqueDataComponentTypes.SATCHEL_STACK, List.of(ItemStack.EMPTY));
        ItemStack componentStack = !list.isEmpty() ? list.getFirst() : ItemStack.EMPTY;
        return !componentStack.isEmpty();
    }

    public MapCodec<SatchelHasFirstStackItemProperty> getCodec() {
        return CODEC;
    }
}