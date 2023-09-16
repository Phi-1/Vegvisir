package dev.stormwatch.vegvisir.datagen;

import dev.stormwatch.vegvisir.registry.VegvisirBlocks;
import dev.stormwatch.vegvisir.registry.VegvisirItems;
import dev.stormwatch.vegvisir.registry.VegvisirTags;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.FilledBucketTrigger;
import net.minecraft.advancements.critereon.FishingRodHookedTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.PlacedBlockTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

// Only extends RecipeProvider for access to TriggerInstances
public class VegvisirRecipes extends RecipeProvider {

    public static final ShapedRecipeBuilder RESONANT_CHRYSALIS = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, VegvisirItems.RESONANT_CHRYSALIS.get())
            .pattern("GEG")
            .pattern("ETE")
            .pattern("GEG")
            .define('G', Items.GOLD_INGOT)
            .define('E', Items.EMERALD)
            .define('T', Items.GHAST_TEAR)
            .unlockedBy("has_wool", inventoryTrigger(ItemPredicate.Builder.item().of(ItemTags.WOOL).build()));

    public static final ShapedRecipeBuilder SPINNING_WHEEL = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, VegvisirBlocks.SPINNING_WHEEL_BLOCK.get())
            .pattern(" S ")
            .pattern("SCS")
            .pattern("BBB")
            .define('S', Items.STICK)
            .define('C', VegvisirItems.RESONANT_CHRYSALIS.get())
            .define('B', ItemTags.WOODEN_SLABS)
            .unlockedBy("has_wool", inventoryTrigger(ItemPredicate.Builder.item().of(ItemTags.WOOL).build()));

    public static final ShapedRecipeBuilder WOOL_SWEATER = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, VegvisirItems.WOOL_SWEATER.get())
            .pattern("Y Y")
            .pattern("YYY")
            .pattern("YYY")
            .define('Y', VegvisirItems.WOOL_YARN.get())
            .unlockedBy("has_yarn", inventoryTrigger(ItemPredicate.Builder.item().of(VegvisirItems.WOOL_YARN.get()).build()));

    public static final ShapedRecipeBuilder WOOL_SOCKS = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, VegvisirItems.WOOL_SOCKS.get())
            .pattern("   ")
            .pattern("Y Y")
            .pattern("Y Y")
            .define('Y', VegvisirItems.WOOL_YARN.get())
            .unlockedBy("has_yarn", inventoryTrigger(ItemPredicate.Builder.item().of(VegvisirItems.WOOL_YARN.get()).build()));

    public static final ShapedRecipeBuilder KNIT_CAP = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, VegvisirItems.KNIT_CAP.get())
            .pattern("YYY")
            .pattern("Y Y")
            .pattern("   ")
            .define('Y', VegvisirItems.WOOL_YARN.get())
            .unlockedBy("has_yarn", inventoryTrigger(ItemPredicate.Builder.item().of(VegvisirItems.WOOL_YARN.get()).build()));

    public static final ShapelessRecipeBuilder FISH_OIL = ShapelessRecipeBuilder.shapeless(RecipeCategory.BREWING, VegvisirItems.FISH_OIL.get(), 1)
            .requires(VegvisirTags.Items.RAW_FISH)
            .requires(Items.GLASS_BOTTLE)
            .requires(Items.BOWL)
            .requires(Items.STICK)
            .unlockedBy("caught_fish", inventoryTrigger(ItemPredicate.Builder.item().of(VegvisirTags.Items.RAW_FISH).build()));

    public static final ShapelessRecipeBuilder HEARTHESSENCE = ShapelessRecipeBuilder.shapeless(RecipeCategory.BREWING, VegvisirItems.HEARTHESSENCE.get(), 2)
            .requires(VegvisirItems.FISH_OIL.get())
            .requires(Items.HONEY_BOTTLE)
            .requires(Items.COCOA_BEANS)
            .requires(Items.POPPY)
            .unlockedBy("has_fishoil", inventoryTrigger(ItemPredicate.Builder.item().of(VegvisirItems.FISH_OIL.get()).build()));

    public static final ShapelessRecipeBuilder DOUGH = ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, VegvisirItems.DOUGH.get(), 2)
            .requires(Items.WHEAT, 7)
            .requires(Items.WATER_BUCKET)
            .requires(Items.BOWL)
            .unlockedBy("filled_water_bucket", FilledBucketTrigger.TriggerInstance.filledBucket(ItemPredicate.Builder.item().of(Items.WATER_BUCKET).build()));

    public static final ShapelessRecipeBuilder PUMPKIN_PIE_BATTER = ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, VegvisirItems.PUMKIN_PIE_BATTER.get(), 2)
            .requires(Items.PUMPKIN, 3)
            .requires(Items.SUGAR, 3)
            .requires(Items.EGG, 2)
            .requires(Items.BOWL)
            .unlockedBy("obtained_pumpkin", inventoryTrigger(ItemPredicate.Builder.item().of(Items.PUMPKIN).build()));

    public static final ShapelessRecipeBuilder UNCOOKED_PUMPKIN_PIE = ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, VegvisirItems.UNCOOKED_PUMKIN_PIE.get(), 2)
            .requires(VegvisirItems.PUMKIN_PIE_BATTER.get())
            .requires(VegvisirItems.DOUGH.get())
            .unlockedBy("obtained_pumpkin_batter", inventoryTrigger(ItemPredicate.Builder.item().of(VegvisirItems.PUMKIN_PIE_BATTER.get()).build()));

    // TODO: figure out proper xp, 10 = 21 levels from a stack
    // TODO: cooking time
    // TODO: if vanilla override recipe is annoying in recipe book, see https://forums.minecraftforge.net/topic/89592-1161-overriding-vanilla-recipes/?do=findComment&comment=417682
    public static final SimpleCookingRecipeBuilder BREAD = SimpleCookingRecipeBuilder.smelting(Ingredient.of(VegvisirItems.DOUGH.get()), RecipeCategory.FOOD,
            Items.BREAD, 10, 400)
            .unlockedBy("has_wheat", inventoryTrigger(ItemPredicate.Builder.item().of(Items.WHEAT).build()));

    public static final SimpleCookingRecipeBuilder PUMPKIN_PIE = SimpleCookingRecipeBuilder.smelting(Ingredient.of(VegvisirItems.UNCOOKED_PUMKIN_PIE.get()), RecipeCategory.FOOD,
                    Items.PUMPKIN_PIE, 20, 300)
            .unlockedBy("has_uncooked_pumpkin_pie", inventoryTrigger(ItemPredicate.Builder.item().of(VegvisirItems.UNCOOKED_PUMKIN_PIE.get()).build()));





    // IGNORE
    private VegvisirRecipes(PackOutput pOutput) {
        super(pOutput);
    }
    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {}
}
