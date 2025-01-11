package net.hollowed.antique.util;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class DoubleGlowBakedModel implements FabricBakedModel, BakedModel, ItemModel {
    private final BakedModel baseModel;           // Non-emissive model
    private final BakedModel emissiveOverlay;   // Emissive overlay model

    public DoubleGlowBakedModel(BakedModel baseModel, BakedModel emissiveOverlay) {
        this.baseModel = baseModel;
        this.emissiveOverlay = emissiveOverlay;
    }

    @Override
    public void emitBlockQuads(QuadEmitter emitter, BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, Predicate<@Nullable Direction> cullTest) {
        // Render the base (non-emissive) model
        baseModel.emitBlockQuads(emitter, blockView, state, pos, randomSupplier, cullTest);

        // Render the emissive overlay model with glow effect
        emitter.pushTransform(quad -> {
            quad.lightmap(0xf000f0, 0xf000f0, 0xf000f0, 0xf000f0); // Maximum brightness
            return true;
        });
        emissiveOverlay.emitBlockQuads(emitter, blockView, state, pos, randomSupplier, cullTest);
        emitter.popTransform();
    }

    @Override
    public void emitItemQuads(QuadEmitter emitter, Supplier<Random> randomSupplier) {
        // Render the base (non-emissive) model
        baseModel.emitItemQuads(emitter, randomSupplier);

        // Render the emissive overlay model with glow effect
        emitter.pushTransform(quad -> {
            quad.lightmap(0xf000f0, 0xf000f0, 0xf000f0, 0xf000f0); // Maximum brightness
            return true;
        });
        emissiveOverlay.emitItemQuads(emitter, randomSupplier);
        emitter.popTransform();
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        return List.of();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean hasDepth() {
        return false;
    }

    @Override
    public boolean isSideLit() {
        return false;
    }

    @Override
    public Sprite getParticleSprite() {
        return null;
    }

    @Override
    public ModelTransformation getTransformation() {
        return null;
    }

    @Override
    public void update(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ModelTransformationMode transformationMode, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed) {

    }
}