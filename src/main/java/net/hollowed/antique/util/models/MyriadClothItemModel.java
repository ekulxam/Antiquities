package net.hollowed.antique.util.models;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.*;
import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.antique.index.AntiqueDataComponentTypes;
import net.hollowed.antique.index.AntiqueItemTags;
import net.hollowed.antique.util.resources.ClothSkinListener;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.RenderLayer;
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
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class MyriadClothItemModel implements ItemModel {

	private enum DisplayBucket { GUI_FIXED_GROUND, HAND }
	private record QuadKey(Identifier variant, boolean large, DisplayBucket bucket) {}

	private static final ArrayList<String> models = new ArrayList<>();
	private final List<TintSource> tints;
	private final List<BakedQuad> baseQuads;
	private final Map<QuadKey, BakedQuad[]> quadIndex;
	private final Supplier<Vector3f[]> vertices;
	private final ModelSettings settings;
	private final boolean animated;

	public MyriadClothItemModel(
			List<TintSource> tints,
			List<BakedQuad> baseQuads,
			List<BakedQuad> variantQuads,
			ModelSettings settings
	) {
		this.tints = tints;
		this.baseQuads = baseQuads;
		this.settings = settings;
		this.quadIndex = buildQuadIndex(variantQuads);
		List<BakedQuad> all = new ArrayList<>(baseQuads.size() + variantQuads.size());
		all.addAll(baseQuads);
		all.addAll(variantQuads);
		this.vertices = Suppliers.memoize(() -> bakeQuads(all));
		boolean anyAnimated = false;
		for (BakedQuad q : all) {
			if (q.sprite().isAnimated()) {
				anyAnimated = true;
				break;
			}
		}
		this.animated = anyAnimated;
	}

	private static Map<QuadKey, BakedQuad[]> buildQuadIndex(List<BakedQuad> quads) {
		Map<QuadKey, List<BakedQuad>> temp = new HashMap<>(64);

		for (BakedQuad quad : quads) {
			Identifier id = quad.sprite().getContents().getId();
			String path = id.getPath();

			boolean large = path.contains("large");
			DisplayBucket bucket = path.contains("_hand") ? DisplayBucket.HAND : DisplayBucket.GUI_FIXED_GROUND;

			String variantPath = extractVariantName(path);
			Identifier variantId = Identifier.of(id.getNamespace(), variantPath);

			QuadKey key = new QuadKey(variantId, large, bucket);
			temp.computeIfAbsent(key, k -> new ArrayList<>(8)).add(quad);
		}

		Map<QuadKey, BakedQuad[]> out = new HashMap<>(temp.size());
		for (Map.Entry<QuadKey, List<BakedQuad>> e : temp.entrySet()) {
			out.put(e.getKey(), e.getValue().toArray(BakedQuad[]::new));
		}
		return out;
	}

	private static String extractVariantName(String path) {
		int lastSlash = path.lastIndexOf('/');
		String name = lastSlash >= 0 ? path.substring(lastSlash + 1) : path;

		name = name.replace("_hand", "")
				.replace("_item", "")
				.replace("_large", "");
		return name.isEmpty() ? "cloth" : name;
	}

	public static Vector3f[] bakeQuads(List<BakedQuad> quads) {
		Set<Vector3f> set = new LinkedHashSet<>(Math.max(16, quads.size() * 4));
		for (BakedQuad quad : quads) {
			BakedQuadFactory.calculatePosition(quad.vertexData(), set::add);
		}
		return set.toArray(Vector3f[]::new);
	}

	private static DisplayBucket bucketFrom(ItemDisplayContext ctx) {
		return (ctx == ItemDisplayContext.GROUND || ctx == ItemDisplayContext.FIXED || ctx == ItemDisplayContext.GUI)
				? DisplayBucket.GUI_FIXED_GROUND
				: DisplayBucket.HAND;
	}

	@Override
	public void update(
			ItemRenderState state,
			ItemStack stack,
			ItemModelManager resolver,
			ItemDisplayContext displayContext,
			@Nullable ClientWorld world,
			@Nullable LivingEntity user,
			int seed
	) {
		state.addModelKey(this);
		state.addModelKey(displayContext);


		Identifier modelVariantId = Identifier.tryParse(
				stack.getOrDefault(AntiqueDataComponentTypes.CLOTH_TYPE, "antique:cloth")
		);
		if (modelVariantId == null) return;
		state.addModelKey(modelVariantId);

		boolean large = stack.getOrDefault(AntiqueDataComponentTypes.MYRIAD_STACK, ItemStack.EMPTY)
				.isIn(AntiqueItemTags.LARGE_CLOTH);
		state.addModelKey(large);

		ItemRenderState.Glint glint = null;
		if (stack.hasGlint()) {
			glint = shouldUseSpecialGlint(stack) ? ItemRenderState.Glint.SPECIAL : ItemRenderState.Glint.STANDARD;
			state.markAnimated();
			state.addModelKey(glint);
		}

		DisplayBucket bucket = bucketFrom(displayContext);
		BakedQuad[] selected = quadIndex.get(new QuadKey(modelVariantId, large, bucket));

		boolean isFallback = false;
		if (selected == null || selected.length == 0) {
			selected = quadIndex.get(new QuadKey(Identifier.of("antique:cloth"), large, bucket));
			isFallback = true;
		}

		RenderLayer layerType = RenderLayers.getItemLayer(stack);

		ItemRenderState.LayerRenderState baseLayer = state.newLayer();
		if (glint != null) baseLayer.setGlint(glint);
		baseLayer.setVertices(this.vertices);
		baseLayer.setRenderLayer(layerType);
		this.settings.addSettings(baseLayer, displayContext);
		baseLayer.getQuads().addAll(this.baseQuads);

		ItemRenderState.LayerRenderState tintLayer = state.newLayer();
		if (glint != null) tintLayer.setGlint(glint);
		tintLayer.setVertices(this.vertices);
		tintLayer.setRenderLayer(layerType);
		this.settings.addSettings(tintLayer, displayContext);

		if (selected != null && selected.length > 0) {
			Collections.addAll(tintLayer.getQuads(), selected);
		}

		if (ClothSkinListener.getTransform(modelVariantId.toString()).dyeable()
				|| tintLayer.getQuads().isEmpty()
				|| isFallback) {
			int n = this.tints.size();
			int[] t = tintLayer.initTints(n);
			for (int i = 0; i < n; i++) {
				int c = this.tints.get(i).getTint(stack, world, user);
				t[i] = c;
				state.addModelKey(c);
			}
		}

		if (this.animated) {
			state.markAnimated();
		}
	}

	private static boolean shouldUseSpecialGlint(ItemStack stack) {
		return stack.isIn(ItemTags.COMPASSES) || stack.isOf(Items.CLOCK);
	}

	@Environment(EnvType.CLIENT)
	public record Unbaked(Identifier base, List<TintSource> tints) implements ItemModel.Unbaked {
		public static final MapCodec<MyriadClothItemModel.Unbaked> CODEC = RecordCodecBuilder.mapCodec(
				instance -> instance.group(
						Identifier.CODEC.fieldOf("base").forGetter(MyriadClothItemModel.Unbaked::base),
						TintSourceTypes.CODEC.listOf().optionalFieldOf("tints", List.of()).forGetter(MyriadClothItemModel.Unbaked::tints)
				).apply(instance, MyriadClothItemModel.Unbaked::new)
		);

		@Override
		public void resolve(ResolvableModel.Resolver resolver) {
			resolver.markDependency(this.base);

			ResourceManager manager = MinecraftClient.getInstance().getResourceManager();
			manager.findResources("models/item/cloth", path -> path.getPath().endsWith(".json")).keySet().forEach(id -> {
				if (manager.getResource(id).isPresent()) {
					String string = id.toString();
					string = string.substring(0, string.indexOf("."));
					string = string.substring(0, 8) + string.substring(15);
					if (!models.contains(string)) {
						models.add(string);
					}
				}
			});

			for (String model : models) {
				resolver.markDependency(Identifier.of(model));
			}
		}

		@Override
		public ItemModel bake(ItemModel.BakeContext context) {

			Baker baker = context.blockModelBaker();
			List<BakedQuad> variantQuads = new ArrayList<>(64);

			BakedSimpleModel baseBaked = baker.getModel(this.base);
			ModelTextures baseTex = baseBaked.getTextures();
			ModelSettings settings = ModelSettings.resolveSettings(baker, baseBaked, baseTex);

			ResourceManager manager = MinecraftClient.getInstance().getResourceManager();
			manager.findResources("models/item/cloth", path -> path.getPath().endsWith(".json")).keySet().forEach(id -> {
				if (manager.getResource(id).isPresent()) {
					String string = id.toString();
					string = string.substring(0, string.indexOf("."));
					string = string.substring(0, 8) + string.substring(15);
					if (!models.contains(string)) {
						models.add(string);
					}
				}
			});

			for (String model : models) {
				BakedSimpleModel m = baker.getModel(Identifier.of(model));
				ModelTextures tex = m.getTextures();
				variantQuads.addAll(m.bakeGeometry(tex, baker, ModelRotation.X0_Y0).getAllQuads());
			}

			List<BakedQuad> baseQuads = baseBaked.bakeGeometry(baseTex, baker, ModelRotation.X0_Y0).getAllQuads();
			return new MyriadClothItemModel(this.tints, baseQuads, variantQuads, settings);
		}

		@Override
		public MapCodec<MyriadClothItemModel.Unbaked> getCodec() {
			return CODEC;
		}
	}
}