package dev.stormwatch.vegvisir.datagen;

import dev.stormwatch.vegvisir.registry.VegvisirItems;
import net.minecraft.advancements.critereon.FilledBucketTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.PlacedBlockTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

// Only extends RecipeProvider for access to TriggerInstances
public class VegvisirRecipes extends RecipeProvider {

    public static final ShapedRecipeBuilder DOUGH = ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, VegvisirItems.DOUGH.get(), 2)
            .pattern("WWW")
            .pattern("WBW")
            .pattern("WWW")
            .define('W', Items.WHEAT)
            .define('B', Items.WATER_BUCKET)
            .unlockedBy("picked_up_wheat", FilledBucketTrigger.TriggerInstance.filledBucket(ItemPredicate.Builder.item().of(Items.WATER_BUCKET).build()));

    // TODO: figure out proper xp, 10 = 21 levels from a stack
    // TODO: cooking time
    // TODO: if vanilla override recipe is annoying in recipe book, see https://forums.minecraftforge.net/topic/89592-1161-overriding-vanilla-recipes/?do=findComment&comment=417682
    public static final SimpleCookingRecipeBuilder BREAD = SimpleCookingRecipeBuilder.smelting(Ingredient.of(VegvisirItems.DOUGH.get()), RecipeCategory.FOOD,
            Items.BREAD, 10, 400)
            .unlockedBy("has_wheat", inventoryTrigger(ItemPredicate.Builder.item().of(Items.WHEAT).build()));


    // TODO: pumpkin pie baking recipe





    private VegvisirRecipes(PackOutput pOutput) {
        super(pOutput);
    }
    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {}
}
