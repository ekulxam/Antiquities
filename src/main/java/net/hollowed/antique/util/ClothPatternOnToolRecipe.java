package net.hollowed.antique.util;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.hollowed.antique.Antiquities;
import net.hollowed.antique.index.AntiqueDataComponentTypes;
import net.hollowed.antique.index.AntiqueItems;
import net.hollowed.antique.index.AntiqueRecipeSerializer;
import net.hollowed.antique.util.resources.ClothSkinData;
import net.hollowed.combatamenities.util.items.CAComponents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ClothPatternOnToolRecipe implements CraftingRecipe {
	final String group;
	final CraftingRecipeCategory category;
	final List<Ingredient> ingredients;
	@Nullable
	private IngredientPlacement ingredientPlacement;
	private static final Map<String, ClothSkinData.ClothSubData> transforms = new LinkedHashMap<>();

	public ClothPatternOnToolRecipe(String group, CraftingRecipeCategory category, List<Ingredient> ingredients) {
		this.group = group;
		this.category = category;
		this.ingredients = ingredients;
	}

	@Override
	public RecipeSerializer<ClothPatternOnToolRecipe> getSerializer() {
		return AntiqueRecipeSerializer.CLOTH_PATTERN;
	}

	@Override
	public String getGroup() {
		return this.group;
	}

	@Override
	public CraftingRecipeCategory getCategory() {
		return this.category;
	}

	@Override
	public IngredientPlacement getIngredientPlacement() {
		if (this.ingredientPlacement == null) {
			this.ingredientPlacement = IngredientPlacement.forShapeless(this.ingredients);
		}

		return this.ingredientPlacement;
	}

	@Override
	public DefaultedList<ItemStack> getRecipeRemainders(CraftingRecipeInput input) {
		return collectRecipeRemainders(input);
	}

	static DefaultedList<ItemStack> collectRecipeRemainders(CraftingRecipeInput input) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(input.size(), ItemStack.EMPTY);

		for (int i = 0; i < defaultedList.size(); i++) {
			ItemStack item = input.getStackInSlot(i);
			if (item.isOf(AntiqueItems.CLOTH_PATTERN)) {
				defaultedList.set(i, item.copy());
			}
		}

		return defaultedList;
	}

	public static ClothSkinData.ClothSubData getTransform(String id) {
		return transforms.getOrDefault(id, new ClothSkinData.ClothSubData(Antiquities.id("cloth"), "d13a68", 1.4F, 0.1F, 8, 0, true, true));
	}

	public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
		if (craftingRecipeInput.getStackCount() != this.ingredients.size()) {
			return false;
		} else {
			if (world instanceof ServerWorld serverWorld) {
				ResourceManager manager = serverWorld.getServer().getResourceManager();
				manager.findResources("cloth_skins", path -> path.getPath().endsWith(".json")).keySet().forEach(id -> {
					if (manager.getResource(id).isPresent()) {
						try (InputStream stream = manager.getResource(id).get().getInputStream()) {
							JsonObject json = JsonHelper.deserialize(new InputStreamReader(stream, StandardCharsets.UTF_8));
							DataResult<ClothSkinData> result = ClothSkinData.CODEC.parse(JsonOps.INSTANCE, json);

							result.resultOrPartial(Antiquities.LOGGER::error).ifPresent(data -> {
								for (ClothSkinData.ClothSubData entry : data.list()) {
									transforms.putIfAbsent(entry.model().getPath(), entry);
								}
							});
						} catch (Exception e) {
							Antiquities.LOGGER.error("Failed to load transform for {}: {}", id, e.getMessage());
						}
					}
				});

				for (ItemStack stack : craftingRecipeInput.getStacks()) {
					if (stack.isOf(AntiqueItems.MYRIAD_TOOL) && !getTransform(stack.getOrDefault(AntiqueDataComponentTypes.CLOTH_TYPE, "cloth")).overlay()) {
						return false;
					}
				}
			}
			return craftingRecipeInput.size() == 1 && this.ingredients.size() == 1
					? this.ingredients.getFirst().test(craftingRecipeInput.getStackInSlot(0))
					: craftingRecipeInput.getRecipeMatcher().isCraftable(this, null);
		}
	}

	public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
		ItemStack myriadTool = null;
		ItemStack clothPattern = null;

		for (ItemStack stack : craftingRecipeInput.getStacks()) {
			if (stack.isOf(AntiqueItems.MYRIAD_TOOL)) {
				myriadTool = stack;
			} else if (stack.isOf(AntiqueItems.CLOTH_PATTERN)) {
				clothPattern = stack;
			}
		}

		if (myriadTool != null && clothPattern != null) {
			ItemStack result = myriadTool.copy();
			String pattern = "item.antique.cloth_pattern";
			Text text = clothPattern.getOrDefault(DataComponentTypes.ITEM_NAME, Text.translatable("item.antique.cloth_pattern"));
			if (text.getContent() instanceof TranslatableTextContent translatable) {
				pattern = translatable.getKey();
			}
			pattern = pattern.substring(pattern.indexOf(".") + 1);
			pattern = pattern.replace(".", ":");
			pattern = pattern.substring(0, pattern.indexOf("_"));

			result.set(AntiqueDataComponentTypes.SECONDARY_DYED_COLOR, clothPattern.getOrDefault(DataComponentTypes.DYED_COLOR, new DyedColorComponent(0xFFFFFF)));
			result.set(AntiqueDataComponentTypes.CLOTH_PATTERN, pattern);
			result.set(CAComponents.BOOLEAN_PROPERTY, clothPattern.getOrDefault(CAComponents.BOOLEAN_PROPERTY, false));

			return result;
		}
		return ItemStack.EMPTY;
	}

	public static class Serializer implements RecipeSerializer<ClothPatternOnToolRecipe> {
		private static final MapCodec<ClothPatternOnToolRecipe> CODEC = RecordCodecBuilder.mapCodec(
				instance -> instance.group(
								Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
								CraftingRecipeCategory.CODEC.fieldOf("category").orElse(CraftingRecipeCategory.MISC).forGetter(recipe -> recipe.category),
								Ingredient.CODEC.listOf(1, 9).fieldOf("ingredients").forGetter(recipe -> recipe.ingredients)
						)
						.apply(instance, ClothPatternOnToolRecipe::new)
		);
		public static final PacketCodec<RegistryByteBuf, ClothPatternOnToolRecipe> PACKET_CODEC = PacketCodec.tuple(
				PacketCodecs.STRING,
				recipe -> recipe.group,
				CraftingRecipeCategory.PACKET_CODEC,
				recipe -> recipe.category,
				Ingredient.PACKET_CODEC.collect(PacketCodecs.toList()),
				recipe -> recipe.ingredients,
				ClothPatternOnToolRecipe::new
		);

		@Override
		public MapCodec<ClothPatternOnToolRecipe> codec() {
			return CODEC;
		}

		@Override
		public PacketCodec<RegistryByteBuf, ClothPatternOnToolRecipe> packetCodec() {
			return PACKET_CODEC;
		}
	}
}