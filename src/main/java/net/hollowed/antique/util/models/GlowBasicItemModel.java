package net.hollowed.antique.util.models;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.client.render.item.tint.TintSourceTypes;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BakedQuadFactory;
import net.minecraft.client.render.model.BakedSimpleModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.ModelSettings;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.HeldItemContext;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class GlowBasicItemModel implements ItemModel {
	private final List<TintSource> tints;
	private final List<Integer> emissions;
	private final List<BakedQuad> quads;
	private final Supplier<Vector3f[]> vector;
	private final ModelSettings settings;
	private final boolean animated;

	public GlowBasicItemModel(List<TintSource> tints, List<Integer> emissions, List<BakedQuad> quads, ModelSettings settings) {
		this.tints = tints;
		this.emissions = emissions;
		this.quads = quads;
		this.settings = settings;
		this.vector = Suppliers.memoize(() -> bakeQuads(this.quads));
		boolean bl = false;

		for (BakedQuad bakedQuad : quads) {
			if (bakedQuad.sprite().getContents().isAnimated()) {
				bl = true;
				break;
			}
		}

		this.animated = bl;
	}

	public static Vector3f[] bakeQuads(List<BakedQuad> quads) {
		Set<Vector3f> set = new HashSet<>();

		for (BakedQuad bakedQuad : quads) {
			BakedQuadFactory.calculatePosition(bakedQuad.vertexData(), set::add);
		}

		return set.toArray(Vector3f[]::new);
	}

	@Override
	public void update(
		ItemRenderState state,
		ItemStack stack,
		ItemModelManager resolver,
		ItemDisplayContext displayContext,
		@Nullable ClientWorld world,
		@Nullable HeldItemContext heldItemContext,
		int seed
	) {
		state.addModelKey(this);
		ItemRenderState.LayerRenderState layerRenderState = state.newLayer();
		if (stack.hasGlint()) {
			ItemRenderState.Glint glint = shouldUseSpecialGlint(stack) ? ItemRenderState.Glint.SPECIAL : ItemRenderState.Glint.STANDARD;
			layerRenderState.setGlint(glint);
			state.markAnimated();
			state.addModelKey(glint);
		}

		int i = this.tints.size();
		int[] is = layerRenderState.initTints(i);

		for (int j = 0; j < i; j++) {
			int k = this.tints.get(j).getTint(stack, world, heldItemContext == null ? null : heldItemContext.getEntity());
			is[j] = k;
			state.addModelKey(k);
		}

		List<BakedQuad> newQuads = getNewQuads();

		layerRenderState.setVertices(this.vector);
		layerRenderState.setRenderLayer(RenderLayers.getItemLayer(stack));
		this.settings.addSettings(layerRenderState, displayContext);
		layerRenderState.getQuads().addAll(newQuads);
		if (this.animated) {
			state.markAnimated();
		}
	}

	private @NotNull List<BakedQuad> getNewQuads() {
		List<BakedQuad> newQuads = new java.util.ArrayList<>(List.of());
		String spriteId = this.quads.getFirst().sprite().getContents().getId().getPath();
		int glowIndex = 0;
		for (BakedQuad quad : this.quads) {
			if (!(quad.sprite().getContents().getId().getPath().equals(spriteId))) {
				glowIndex++;
				spriteId = quad.sprite().getContents().getId().getPath();
			}
			newQuads.add(new BakedQuad(quad.vertexData(), quad.tintIndex(), quad.face(), quad.sprite(), quad.shade(), glowIndex >= this.emissions.size() ? quad.lightEmission() : this.emissions.get(glowIndex)));
		}
		return newQuads;
	}

	private static boolean shouldUseSpecialGlint(ItemStack stack) {
		return stack.isIn(ItemTags.COMPASSES) || stack.isOf(Items.CLOCK);
	}

	@Environment(EnvType.CLIENT)
	public record Unbaked(Identifier model, List<Integer> emissions, List<TintSource> tints) implements ItemModel.Unbaked {
		public static final MapCodec<GlowBasicItemModel.Unbaked> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					Identifier.CODEC.fieldOf("model").forGetter(GlowBasicItemModel.Unbaked::model),
					Codec.INT.listOf().optionalFieldOf("emissions", List.of()).forGetter(GlowBasicItemModel.Unbaked::emissions),
					TintSourceTypes.CODEC.listOf().optionalFieldOf("tints", List.of()).forGetter(GlowBasicItemModel.Unbaked::tints)
				)
				.apply(instance, GlowBasicItemModel.Unbaked::new)
		);

		@Override
		public void resolve(ResolvableModel.Resolver resolver) {
			resolver.markDependency(this.model);
		}

		@Override
		public ItemModel bake(ItemModel.BakeContext context) {
			Baker baker = context.blockModelBaker();
			BakedSimpleModel bakedSimpleModel = baker.getModel(this.model);
			ModelTextures modelTextures = bakedSimpleModel.getTextures();
			List<BakedQuad> list = bakedSimpleModel.bakeGeometry(modelTextures, baker, ModelRotation.X0_Y0).getAllQuads();
			ModelSettings modelSettings = ModelSettings.resolveSettings(baker, bakedSimpleModel, modelTextures);
			return new GlowBasicItemModel(this.tints, this.emissions, list, modelSettings);
		}

		@Override
		public MapCodec<GlowBasicItemModel.Unbaked> getCodec() {
			return CODEC;
		}
	}
}
