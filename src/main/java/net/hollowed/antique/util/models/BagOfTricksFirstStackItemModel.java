package net.hollowed.antique.util.models;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.antique.index.AntiqueComponents;
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
public class BagOfTricksFirstStackItemModel implements ItemModel {
    static final ItemModel INSTANCE = new BagOfTricksFirstStackItemModel();

    public BagOfTricksFirstStackItemModel() {}

    public void update(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ItemDisplayContext displayContext, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed) {
        state.addModelKey(this);
        List<ItemStack> list = stack.getOrDefault(AntiqueComponents.SATCHEL_STACK, List.of(ItemStack.EMPTY));
        ItemStack itemStack = !list.isEmpty() ? list.getFirst() : ItemStack.EMPTY;
        if (!itemStack.isEmpty()) {
            resolver.update(state, itemStack, displayContext, world, user, seed);
        }
    }

    @Environment(EnvType.CLIENT)
    public record Unbaked() implements ItemModel.Unbaked {
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(new Unbaked());

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }

        public ItemModel bake(BakeContext context) {
            return BagOfTricksFirstStackItemModel.INSTANCE;
        }

        public void resolve(Resolver resolver) {
        }
    }
}