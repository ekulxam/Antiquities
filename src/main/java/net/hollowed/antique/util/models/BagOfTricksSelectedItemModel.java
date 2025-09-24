package net.hollowed.antique.util.models;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.antique.index.AntiqueDataComponentTypes;
import net.hollowed.antique.items.BagOfTricksItem;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Environment(EnvType.CLIENT)
public class BagOfTricksSelectedItemModel implements ItemModel {
    static final ItemModel INSTANCE = new BagOfTricksSelectedItemModel();

    public BagOfTricksSelectedItemModel() {}

    public void update(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ItemDisplayContext displayContext, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed) {
        state.addModelKey(this);
        List<ItemStack> list = stack.getOrDefault(AntiqueDataComponentTypes.SATCHEL_STACK, List.of(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY));
        if (BagOfTricksItem.getInternalIndex(stack) >= 0 && !list.isEmpty() && BagOfTricksItem.getInternalIndex(stack) < list.size()) {
            ItemStack itemStack = list.get(BagOfTricksItem.getInternalIndex(stack));
            if (!itemStack.isEmpty()) {
                resolver.update(state, itemStack, displayContext, world, user, seed);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public record Unbaked() implements ItemModel.Unbaked {
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(new Unbaked());

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }

        public ItemModel bake(BakeContext context) {
            return BagOfTricksSelectedItemModel.INSTANCE;
        }

        public void resolve(Resolver resolver) {
        }
    }
}