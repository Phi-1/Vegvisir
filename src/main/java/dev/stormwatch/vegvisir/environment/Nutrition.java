package dev.stormwatch.vegvisir.environment;

import dev.stormwatch.vegvisir.registry.VegvisirTags;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.UUID;

public class Nutrition {

    public static final int FOOD_NUTRITION_MODIFIER = 5;
    public static final double NUTRITION_TICKRATE = (double) 100 / 72000;

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

    public static class Stats {
        // buffs/debuffs attack damage, attack speed, max health, movespeed
        private static final double MINIMAL_NUTRITION_QUOTIENT = 0;
        private static final double NEUTRAL_NUTRITION_QUOTIENT = 0.6;
        private static final double OPTIMAL_NUTRITION_QUOTIENT = 0.8;

        private static final double LOWER_DX = NEUTRAL_NUTRITION_QUOTIENT - MINIMAL_NUTRITION_QUOTIENT;
        private static final double UPPER_DX = OPTIMAL_NUTRITION_QUOTIENT - NEUTRAL_NUTRITION_QUOTIENT;

        private static final double NEUTRAL_STAT = 0;

        private static final String HEALTH_NAME = "vegvisir_nutrition_health";
        private static final UUID HEALTH_UUID = UUID.fromString("bf52ceab-d5cb-4ad9-80e9-21343ea7bea5");
        private static final double MIN_HEALTH = -0.4;
        private static final double MAX_HEALTH = 0.3;
        private static final double LOWER_HEALTH_B = NEUTRAL_STAT - NEUTRAL_NUTRITION_QUOTIENT * ((NEUTRAL_STAT - MIN_HEALTH) / LOWER_DX);
        private static final double UPPER_HEALTH_B = NEUTRAL_STAT - NEUTRAL_NUTRITION_QUOTIENT * ((MAX_HEALTH - NEUTRAL_STAT) / UPPER_DX);

        private static final String SPEED_NAME = "vegvisir_nutrition_movespeed";
        private static final UUID   SPEED_UUID = UUID.fromString("7bf45bdb-9f18-4503-aac4-036692dd99d4");
        private static final double MIN_SPEED = -0.1;
        private static final double MAX_SPEED = 0.1;
        private static final double LOWER_SPEED_B = NEUTRAL_STAT - NEUTRAL_NUTRITION_QUOTIENT * ((NEUTRAL_STAT - MIN_SPEED) / LOWER_DX);
        private static final double UPPER_SPEED_B = NEUTRAL_STAT - NEUTRAL_NUTRITION_QUOTIENT * ((MAX_SPEED - NEUTRAL_STAT) / UPPER_DX);

        private static final String DAMAGE_NAME = "vegvisir_nutrition_damage";
        private static final UUID   DAMAGE_UUID = UUID.fromString("8119d972-f820-433a-b65c-2124b5f7233f");
        private static final double MIN_DAMAGE = -0.3;
        private static final double MAX_DAMAGE = 0.3;
        private static final double LOWER_DAMAGE_B = NEUTRAL_STAT - NEUTRAL_NUTRITION_QUOTIENT * ((NEUTRAL_STAT - MIN_DAMAGE) / LOWER_DX);
        private static final double UPPER_DAMAGE_B = NEUTRAL_STAT - NEUTRAL_NUTRITION_QUOTIENT * ((MAX_DAMAGE - NEUTRAL_STAT) / UPPER_DX);

        private static final String ATTACKSPEED_NAME = "vegvisir_nutrition_attackspeed";
        private static final UUID   ATTACKSPEED_UUID = UUID.fromString("981160e9-5392-4182-b4f5-ae5883e8492a");
        private static final double MIN_ATTACKSPEED = -0.2;
        private static final double MAX_ATTACKSPEED = 0.2;
        private static final double LOWER_ATTACKSPEED_B = NEUTRAL_STAT - NEUTRAL_NUTRITION_QUOTIENT * ((NEUTRAL_STAT - MIN_ATTACKSPEED) / LOWER_DX);
        private static final double UPPER_ATTACKSPEED_B = NEUTRAL_STAT - NEUTRAL_NUTRITION_QUOTIENT * ((MAX_ATTACKSPEED - NEUTRAL_STAT) / UPPER_DX);

        public static void applyNutritionStats(Player player, double nutritionQuotient) {
            double health = calcAttributeValue(nutritionQuotient, MIN_HEALTH, MAX_HEALTH, LOWER_HEALTH_B, UPPER_HEALTH_B);
            setAttribute(player, Attributes.MAX_HEALTH, HEALTH_NAME, HEALTH_UUID, health);
            double speed = calcAttributeValue(nutritionQuotient, MIN_SPEED, MAX_SPEED, LOWER_SPEED_B, UPPER_SPEED_B);
            setAttribute(player, Attributes.MOVEMENT_SPEED, SPEED_NAME, SPEED_UUID, speed);
            double damage = calcAttributeValue(nutritionQuotient, MIN_DAMAGE, MAX_DAMAGE, LOWER_DAMAGE_B, UPPER_DAMAGE_B);
            setAttribute(player, Attributes.ATTACK_DAMAGE, DAMAGE_NAME, DAMAGE_UUID, damage);
            double attackspeed = calcAttributeValue(nutritionQuotient, MIN_ATTACKSPEED, MAX_ATTACKSPEED, LOWER_ATTACKSPEED_B, UPPER_ATTACKSPEED_B);
            setAttribute(player, Attributes.ATTACK_SPEED, ATTACKSPEED_NAME, ATTACKSPEED_UUID, attackspeed);
        }

        private static double calcAttributeValue(double nutritionQuotient, double minValue, double maxValue, double lowerIntercept, double upperIntercept) {
            // TODO: optimize to not use if statements
            if (nutritionQuotient <= MINIMAL_NUTRITION_QUOTIENT) return minValue;
            else if (nutritionQuotient >= OPTIMAL_NUTRITION_QUOTIENT) return maxValue;
            else return nutritionQuotient < NEUTRAL_NUTRITION_QUOTIENT ?
                        nutritionQuotient * ((NEUTRAL_STAT - minValue) / LOWER_DX) + lowerIntercept
                        : nutritionQuotient * ((maxValue - NEUTRAL_STAT) / UPPER_DX) + upperIntercept;
        }

        private static void setAttribute(Player player, Attribute attribute, String name, UUID uuid, double amount) {
            AttributeInstance instance = player.getAttribute(attribute);
            if (instance != null) {
                AttributeModifier modifier = instance.getModifier(uuid);
                if (modifier != null) {
                    instance.removeModifier(uuid);
                }
                instance.addPermanentModifier(new AttributeModifier(uuid, name, amount, AttributeModifier.Operation.MULTIPLY_BASE));
                // TODO: set health to max health if max decreased, player.setHealth does hurt animation so needs other method
                // i tried livinghurtevent clientside, but it doesn't fire for this
            }
        }
    }

}
