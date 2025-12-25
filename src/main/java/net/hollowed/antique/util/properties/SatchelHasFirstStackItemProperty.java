package net.hollowed.antique.util.properties;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.antique.index.AntiqueDataComponentTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Environment(EnvType.CLIENT)
public record SatchelHasFirstStackItemProperty() implements ConditionalItemModelProperty {
    public static final MapCodec<SatchelHasFirstStackItemProperty> CODEC = MapCodec.unit(new SatchelHasFirstStackItemProperty());

    public boolean get(ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
        List<ItemStack> list = stack.getOrDefault(AntiqueDataComponentTypes.SATCHEL_STACK, List.of(ItemStack.EMPTY));
        ItemStack componentStack = !list.isEmpty() ? list.getFirst() : ItemStack.EMPTY;
        return !componentStack.isEmpty();
    }

    public MapCodec<SatchelHasFirstStackItemProperty> type() {
        return CODEC;
    }
}