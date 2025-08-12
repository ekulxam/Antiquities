package net.hollowed.antique.index;

import net.hollowed.antique.util.MyriadToolRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface AntiqueRecipeSerializer {
    RecipeSerializer<MyriadToolRecipe> MYRIAD_TOOL = register(
            "crafting_special_myriad_tool", new SpecialCraftingRecipe.SpecialRecipeSerializer<>(MyriadToolRecipe::new)
    );

    static void init() {}

    static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String id, S serializer) {
        return Registry.register(Registries.RECIPE_SERIALIZER, "antique:" + id, serializer);
    }
}
