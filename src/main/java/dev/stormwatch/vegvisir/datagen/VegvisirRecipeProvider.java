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
        VegvisirRecipes.PUMPKIN_PIE_BATTER.save(writer);
        VegvisirRecipes.UNCOOKED_PUMPKIN_PIE.save(writer);
        VegvisirRecipes.BREAD.save(writer);
        VegvisirRecipes.PUMPKIN_PIE.save(writer);
        VegvisirRecipes.FISH_OIL.save(writer);
        VegvisirRecipes.RESONANT_CHRYSALIS.save(writer);
        VegvisirRecipes.SPINNING_WHEEL.save(writer);
        VegvisirRecipes.WOOL_SOCKS.save(writer);
        VegvisirRecipes.WOOL_SWEATER.save(writer);
        VegvisirRecipes.KNIT_CAP.save(writer);
        VegvisirRecipes.HEARTHESSENCE.save(writer);
    }
}
