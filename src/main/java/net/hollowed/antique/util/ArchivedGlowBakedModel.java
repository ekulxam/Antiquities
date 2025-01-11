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

public class ArchivedGlowBakedModel implements FabricBakedModel, BakedModel {

	private final BakedModel baseModel;

	public ArchivedGlowBakedModel(BakedModel model) {
		this.baseModel = model;
	}

	@Override
	public boolean isVanillaAdapter() {
		return this.baseModel.isVanillaAdapter();
	}

	@Override
	public void emitBlockQuads(QuadEmitter emitter, BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, Predicate<@Nullable Direction> cullTest) {
		System.out.println("Emissive model: emitting block quads");
		emitter.pushTransform(quad -> {
			quad.lightmap(0xf000f0, 0xf000f0, 0xf000f0, 0xf000f0); // Set light levels to maximum
			return true;
		});
		this.baseModel.emitBlockQuads(emitter, blockView, state, pos, randomSupplier, cullTest);
		emitter.popTransform();
	}

	@Override
	public void emitItemQuads(QuadEmitter emitter, Supplier<Random> randomSupplier) {
		System.out.println("Emissive model: emitting item quads");
		emitter.pushTransform(quad -> {
			quad.lightmap(0xf000f0, 0xf000f0, 0xf000f0, 0xf000f0); // Set light levels to maximum
			return true;
		});
		this.baseModel.emitItemQuads(emitter, randomSupplier);
		emitter.popTransform();
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
		return this.baseModel.getQuads(state, face, random);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return this.baseModel.useAmbientOcclusion();
	}

	@Override
	public boolean hasDepth() {
		return this.baseModel.hasDepth();
	}

	@Override
	public boolean isSideLit() {
		return this.baseModel.isSideLit();
	}

	@Override
	public Sprite getParticleSprite() {
		return this.baseModel.getParticleSprite();
	}

	@Override
	public ModelTransformation getTransformation() {
		return this.baseModel.getTransformation();
	}
}