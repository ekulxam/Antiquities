package net.hollowed.antique.util;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ArchivedTripleGlowBakedModel implements FabricBakedModel, BakedModel {
    private final BakedModel baseModel;           // Non-emissive model
    private final BakedModel emissiveOverlay;   // Emissive overlay model
    private final BakedModel brighterOverlay;

    public ArchivedTripleGlowBakedModel(BakedModel baseModel, BakedModel emissiveOverlay, BakedModel brighterOverlay) {
        this.baseModel = baseModel;
        this.emissiveOverlay = emissiveOverlay;
        this.brighterOverlay = brighterOverlay;
    }

    @Override
    public void emitBlockQuads(QuadEmitter emitter, BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, Predicate<@Nullable Direction> cullTest) {

        baseModel.emitBlockQuads(emitter, blockView, state, pos, randomSupplier, cullTest);

        // Render the emissive overlay model with noticeable glow
        emitter.pushTransform(quad -> {
            quad.lightmap(0x5F0000, 0x5F0000, 0x5F0000, 0x5F0000); // Brighter value for moderate glow
            return true;
        });
        emissiveOverlay.emitBlockQuads(emitter, blockView, state, pos, randomSupplier, cullTest);
        emitter.popTransform(); // Make sure to pop the transform after the overlay

        // Render the brighter overlay model with maximum glow
        emitter.pushTransform(quad -> {
            quad.lightmap(0xFF00F0, 0xFF00F0, 0xFF00F0, 0xFF00F0); // Maximum brightness for brighter overlay
            return true;
        });
        brighterOverlay.emitBlockQuads(emitter, blockView, state, pos, randomSupplier, cullTest);
        emitter.popTransform(); // Pop transform after the brighter overlay
    }

    @Override
    public void emitItemQuads(QuadEmitter emitter, Supplier<Random> randomSupplier) {

        baseModel.emitItemQuads(emitter, randomSupplier);

        // Render the emissive overlay model with noticeable glow
        emitter.pushTransform(quad -> {
            quad.lightmap(0x5F0000, 0x5F0000, 0x5F0000, 0x5F0000); // Brighter value for moderate glow
            return true;
        });
        emissiveOverlay.emitItemQuads(emitter, randomSupplier);
        emitter.popTransform(); // Make sure to pop the transform after the overlay

        // Render the brighter overlay model with maximum glow
        emitter.pushTransform(quad -> {
            quad.lightmap(0xFF00F0, 0xFF00F0, 0xFF00F0, 0xFF00F0); // Maximum brightness for brighter overlay
            return true;
        });
        brighterOverlay.emitItemQuads(emitter, randomSupplier);
        emitter.popTransform(); // Pop transform after the brighter overlay
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
}