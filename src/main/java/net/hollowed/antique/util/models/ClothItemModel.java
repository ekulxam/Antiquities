package net.hollowed.antique.util.models;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;

import java.util.*;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.antique.Antiquities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BakedQuadFactory;
import net.minecraft.client.render.model.BakedSimpleModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.ModelSettings;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class ClothItemModel implements ItemModel {
	private final List<BakedQuad> quads;
	private final Supplier<Vector3f[]> vector;
	private final ModelSettings settings;
	private final boolean animated;
	private static final ArrayList<String> models = new ArrayList<>();

	private record QuadKey(String variant) {}
	private final Map<QuadKey, BakedQuad[]> quadIndex;

	public ClothItemModel(List<BakedQuad> quads, ModelSettings settings) {
		this.quads = quads;
		this.settings = settings;
		this.vector = Suppliers.memoize(() -> bakeQuads(this.quads));
		this.quadIndex = buildQuadIndex(quads);
		boolean bl = false;

		for (BakedQuad bakedQuad : quads) {
			if (bakedQuad.sprite().isAnimated()) {
				bl = true;
				break;
			}
		}

		this.animated = bl;
	}

	private static Map<QuadKey, BakedQuad[]> buildQuadIndex(List<BakedQuad> quads) {
		Map<QuadKey, List<BakedQuad>> temp = new HashMap<>(64);

		for (BakedQuad quad : quads) {
			Identifier id = quad.sprite().getContents().getId();
			String path = id.getPath();

			String variant = extractVariantName(path);
			variant = variant.intern();

			QuadKey key = new QuadKey(variant);
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
		return name.isEmpty() ? "cloth" : name;
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
		@Nullable LivingEntity user,
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

		String modelVariant = "item.antique.cloth";
		Text text = stack.getOrDefault(DataComponentTypes.ITEM_NAME, Text.translatable("item.antique.cloth"));
		if (text.getContent() instanceof TranslatableTextContent translatable) {
			modelVariant = translatable.getKey();
		}
		modelVariant = modelVariant.substring(modelVariant.lastIndexOf(".") + 1);
		state.addModelKey(modelVariant);

		BakedQuad[] selected = quadIndex.get(new QuadKey(modelVariant));
		if (selected == null || selected.length == 0) selected = quadIndex.get(new QuadKey("cloth"));

		layerRenderState.setVertices(this.vector);
		layerRenderState.setRenderLayer(RenderLayers.getItemLayer(stack));
		this.settings.addSettings(layerRenderState, displayContext);
		if (selected != null && selected.length > 0) {
			Collections.addAll(layerRenderState.getQuads(), selected);
		}

		if (this.animated) {
			state.markAnimated();
		}
	}

	private static boolean shouldUseSpecialGlint(ItemStack stack) {
		return stack.isIn(ItemTags.COMPASSES) || stack.isOf(Items.CLOCK);
	}

	@Environment(EnvType.CLIENT)
	public record Unbaked() implements ItemModel.Unbaked {
		public static final MapCodec<Unbaked> CODEC = MapCodec.unit(new Unbaked());

		@Override
		public void resolve(ResolvableModel.Resolver resolver) {
			resolver.markDependency(Antiquities.id("item/cloth"));

			ResourceManager manager = MinecraftClient.getInstance().getResourceManager();
			manager.findResources("models/item", path -> path.getPath().endsWith(".json")).keySet().forEach(id -> {
				if (manager.getResource(id).isPresent() && id.getPath().contains("_cloth") && !id.getPath().contains("/cloth")) {
					String string = id.toString();
					string = string.substring(0, string.indexOf("."));
					string = string.substring(0, string.indexOf(":") + 1) + string.substring(string.indexOf("/") + 1);
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

			if (!models.contains("antique:item/cloth")) {
				models.add("antique:item/cloth");
			}

			BakedSimpleModel baseBaked = baker.getModel(Antiquities.id("item/cloth"));
			ModelTextures baseTex = baseBaked.getTextures();
			ModelSettings settings = ModelSettings.resolveSettings(baker, baseBaked, baseTex);

			ResourceManager manager = MinecraftClient.getInstance().getResourceManager();
			manager.findResources("models/item", path -> path.getPath().endsWith(".json")).keySet().forEach(id -> {
				if (manager.getResource(id).isPresent() && id.getPath().contains("_cloth") && !id.getPath().contains("/cloth")) {
					String string = id.toString();
					string = string.substring(0, string.indexOf("."));
					string = string.substring(0, string.indexOf(":") + 1) + string.substring(string.indexOf("/") + 1);
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

			return new ClothItemModel(variantQuads, settings);
		}

		@Override
		public MapCodec<Unbaked> getCodec() {
			return CODEC;
		}
	}
}
