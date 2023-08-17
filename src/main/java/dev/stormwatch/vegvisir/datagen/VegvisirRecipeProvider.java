package dev.stormwatch.vegvisir.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.function.Consumer;

public class VegvisirRecipeProvider extends RecipeProvider {

    public VegvisirRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> writer) {
        VegvisirRecipes.DOUGH.save(writer);
        VegvisirRecipes.BREAD.save(writer);
    }
}
