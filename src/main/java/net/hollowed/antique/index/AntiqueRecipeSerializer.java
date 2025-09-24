package net.hollowed.antique.index;

import net.hollowed.antique.util.ClothPatternGlowRecipe;
import net.hollowed.antique.util.ClothPatternOnToolRecipe;
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
    RecipeSerializer<ClothPatternOnToolRecipe> CLOTH_PATTERN = register(
            "crafting_special_cloth_pattern", new ClothPatternOnToolRecipe.Serializer()
    );
    RecipeSerializer<ClothPatternGlowRecipe> PATTERN_GLOW = register(
            "crafting_special_pattern_glow", new ClothPatternGlowRecipe.Serializer()
    );

    static void init() {}

    static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String id, S serializer) {
        return Registry.register(Registries.RECIPE_SERIALIZER, "antique:" + id, serializer);
    }
}
