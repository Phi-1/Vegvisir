package dev.stormwatch.vegvisir.environment;

import dev.stormwatch.vegvisir.registry.VegvisirTags;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

public class Nutrition {

    public enum NutritionGroup {
        MEAT,
        FISH,
        FRUIT,
        VEGETABLE,
        STARCH
    }

    public static EnumSet<NutritionGroup> getNutrition(ItemStack itemStack) {
        EnumSet<NutritionGroup> groups = EnumSet.noneOf(NutritionGroup.class);
        if (itemStack.is(VegvisirTags.Items.MEAT))      groups.add(NutritionGroup.MEAT);
        if (itemStack.is(VegvisirTags.Items.FISH))      groups.add(NutritionGroup.FISH);
        if (itemStack.is(VegvisirTags.Items.VEGETABLE)) groups.add(NutritionGroup.VEGETABLE);
        if (itemStack.is(VegvisirTags.Items.FRUIT))     groups.add(NutritionGroup.FRUIT);
        if (itemStack.is(VegvisirTags.Items.STARCH))    groups.add(NutritionGroup.STARCH);
        return groups;
    }

    public static boolean hasNutrition(ItemStack itemStack) {
        if    (itemStack.is(VegvisirTags.Items.MEAT))      return true;
        if    (itemStack.is(VegvisirTags.Items.FISH))      return true;
        if    (itemStack.is(VegvisirTags.Items.VEGETABLE)) return true;
        if    (itemStack.is(VegvisirTags.Items.FRUIT))     return true;
        return itemStack.is(VegvisirTags.Items.STARCH);
    }

}
