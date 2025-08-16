package net.hollowed.antique.util;

import net.hollowed.antique.index.AntiqueComponents;
import net.hollowed.antique.index.AntiqueItems;
import net.hollowed.antique.index.AntiqueRecipeSerializer;
import net.hollowed.antique.util.resources.ClothSkinData;
import net.hollowed.antique.util.resources.ClothSkinListener;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.world.World;

public class MyriadToolRecipe extends SpecialCraftingRecipe {
	private static final Ingredient CLOTH = Ingredient.ofItem(AntiqueItems.CLOTH);

	public MyriadToolRecipe(CraftingRecipeCategory craftingRecipeCategory) {
		super(craftingRecipeCategory);
	}

	public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
        if (craftingRecipeInput.getStackCount() >= 5) {
            if (craftingRecipeInput.getStackInSlot(1).isOf(AntiqueItems.RAW_MYRIAD)) {
                if (craftingRecipeInput.size() >= 6 && craftingRecipeInput.getStackInSlot(2).isOf(AntiqueItems.RAW_MYRIAD) && craftingRecipeInput.getStackInSlot(5).isOf(AntiqueItems.RAW_MYRIAD) && craftingRecipeInput.getStackInSlot(6).isOf(AntiqueItems.RAW_MYRIAD)
                        || craftingRecipeInput.size() >= 8 && craftingRecipeInput.getStackInSlot(0).isOf(AntiqueItems.RAW_MYRIAD) && craftingRecipeInput.getStackInSlot(3).isOf(AntiqueItems.RAW_MYRIAD) && craftingRecipeInput.getStackInSlot(8).isOf(AntiqueItems.RAW_MYRIAD)) {
                    return CLOTH.test(craftingRecipeInput.getStackInSlot(4));
                }
            }
        }
        return false;
    }

	public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
		ItemStack itemStack = new ItemStack(AntiqueItems.MYRIAD_TOOL);

		ItemStack itemStack2 = craftingRecipeInput.getStackInSlot(4);
		if (!itemStack2.isEmpty() && CLOTH.test(itemStack2)) {
			String model = "item.antique.cloth";
			Text text = itemStack2.getOrDefault(DataComponentTypes.ITEM_NAME, Text.translatable("item.antique.cloth"));
			if (text.getContent() instanceof TranslatableTextContent translatable) {
				model = translatable.getKey();
			}
			model = model.substring(model.lastIndexOf(".") + 1);
			itemStack.set(AntiqueComponents.CLOTH_TYPE, model);

			ClothSkinData.ClothSubData data = ClothSkinListener.getTransform(model);

			int intValue = 0;
			try {
				if (!data.hex().isBlank()) {
					intValue = Integer.parseInt(data.hex(), 16);
				}
			} catch (NumberFormatException e) {
				System.err.println("Invalid hexadecimal string format: " + e.getMessage());
			}

			itemStack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(intValue));
		}

		return itemStack;
	}

	@Override
	public RecipeSerializer<MyriadToolRecipe> getSerializer() {
		return AntiqueRecipeSerializer.MYRIAD_TOOL;
	}
}
