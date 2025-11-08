package net.hollowed.antique.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hollowed.antique.index.AntiqueItems;
import net.hollowed.antique.index.AntiqueRecipeSerializer;
import net.hollowed.combatamenities.util.items.CAComponents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.IngredientPlacement;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ClothPatternGlowRecipe implements CraftingRecipe {
	final String group;
	final CraftingRecipeCategory category;
	final List<Ingredient> ingredients;
	@Nullable
	private IngredientPlacement ingredientPlacement;

	public ClothPatternGlowRecipe(String group, CraftingRecipeCategory category, List<Ingredient> ingredients) {
		this.group = group;
		this.category = category;
		this.ingredients = ingredients;
	}

	@Override
	public RecipeSerializer<ClothPatternGlowRecipe> getSerializer() {
		return AntiqueRecipeSerializer.PATTERN_GLOW;
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

	public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
		if (craftingRecipeInput.getStackCount() != this.ingredients.size()) {
			return false;
		} else {
			return craftingRecipeInput.size() == 1 && this.ingredients.size() == 1
					? this.ingredients.getFirst().test(craftingRecipeInput.getStackInSlot(0))
					: craftingRecipeInput.getRecipeMatcher().isCraftable(this, null);
		}
	}

	public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
		ItemStack clothPattern = null;
		boolean glow = false;

		for (ItemStack stack : craftingRecipeInput.getStacks()) {
			if (stack.isOf(AntiqueItems.CLOTH_PATTERN)) {
				clothPattern = stack;
			} else if (stack.isOf(Items.GLOW_INK_SAC)) {
				glow = true;
			}
		}

		if (clothPattern != null) {
			ItemStack result = clothPattern.copy();
			result.set(CAComponents.BOOLEAN_PROPERTY, glow);
			return result;
		}
		return ItemStack.EMPTY;
	}

	public static class Serializer implements RecipeSerializer<ClothPatternGlowRecipe> {
		private static final MapCodec<ClothPatternGlowRecipe> CODEC = RecordCodecBuilder.mapCodec(
				instance -> instance.group(
								Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
								CraftingRecipeCategory.CODEC.fieldOf("category").orElse(CraftingRecipeCategory.MISC).forGetter(recipe -> recipe.category),
								Ingredient.CODEC.listOf(1, 9).fieldOf("ingredients").forGetter(recipe -> recipe.ingredients)
						)
						.apply(instance, ClothPatternGlowRecipe::new)
		);
		public static final PacketCodec<RegistryByteBuf, ClothPatternGlowRecipe> PACKET_CODEC = PacketCodec.tuple(
				PacketCodecs.STRING,
				recipe -> recipe.group,
				CraftingRecipeCategory.PACKET_CODEC,
				recipe -> recipe.category,
				Ingredient.PACKET_CODEC.collect(PacketCodecs.toList()),
				recipe -> recipe.ingredients,
				ClothPatternGlowRecipe::new
		);

		@Override
		public MapCodec<ClothPatternGlowRecipe> codec() {
			return CODEC;
		}

		@Override
		public PacketCodec<RegistryByteBuf, ClothPatternGlowRecipe> packetCodec() {
			return PACKET_CODEC;
		}
	}
}