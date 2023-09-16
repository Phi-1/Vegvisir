package dev.stormwatch.vegvisir.events;

import dev.stormwatch.vegvisir.capabilities.PlayerNutrition;
import dev.stormwatch.vegvisir.capabilities.PlayerNutritionProvider;
import dev.stormwatch.vegvisir.effects.FoodExtensionEffect;
import dev.stormwatch.vegvisir.environment.Nutrition;
import dev.stormwatch.vegvisir.registry.VegvisirEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItem;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.EnumSet;

public class NutritionEvents {

    @SubscribeEvent
    public static void tickPlayerNutrition(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.CLIENT) return;
        if (event.phase == TickEvent.Phase.START) return;

        Player player = event.player;
        PlayerNutrition playerNutrition = player.getCapability(PlayerNutritionProvider.PLAYER_NUTRITION).orElse(PlayerNutrition.EMPTY);
        if (playerNutrition == PlayerNutrition.EMPTY) return;
        for (Nutrition.NutritionGroup group : Nutrition.NutritionGroup.values()) {
            playerNutrition.decreasePlayerNutrition(group, Nutrition.NUTRITION_TICKRATE);
        }

        if (player.level.getGameTime() % 20 == 0) {
            double nutritionQuotient = playerNutrition.getTotalNutritionQuotient();
            Nutrition.Stats.applyNutritionStats(player, nutritionQuotient);
        }
    }

    @SubscribeEvent
    public static void onEatFoodEvent(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity().level.isClientSide()) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (!Nutrition.hasNutrition(event.getItem())) return;
        ItemStack stack = event.getItem();
        IForgeItem item = stack.getItem();
        FoodProperties foodProperties = item.getFoodProperties(stack, player);

        int foodLevel = foodProperties.getNutrition();
        int playerFoodLevel = player.getFoodData().getFoodLevel();
        int lastPlayerFoodLevel = player.getFoodData().getLastFoodLevel();
        // halve nutrition increase
        int usedFoodLevel = Math.min(20 - lastPlayerFoodLevel, foodLevel);
        player.getFoodData().setFoodLevel(playerFoodLevel - Math.max(usedFoodLevel - foodLevel / 2, 0));

        // set effect to re add saturation once it's depleted
        float saturation = (float) foodProperties.getNutrition() * foodProperties.getSaturationModifier() * 2.0F;
        applySaturationExtension(player, saturation, playerFoodLevel);

        PlayerNutrition playerNutrition = player.getCapability(PlayerNutritionProvider.PLAYER_NUTRITION).orElse(PlayerNutrition.EMPTY);
        if (playerNutrition == PlayerNutrition.EMPTY) return;
        increasePlayerNutrition(playerNutrition, foodLevel, Nutrition.getNutrition(stack));
    }

    private static void applySaturationExtension(Player player, float foodSaturation, int playerHungerLevel) {
        float saturationRefill = Math.min(foodSaturation, playerHungerLevel);
        player.addEffect(new MobEffectInstance(VegvisirEffects.FOOD_EXTENSION.get(), -1, FoodExtensionEffect.getAmplifierForSaturationLevel(saturationRefill), false, false, false));
    }

    private static void increasePlayerNutrition(PlayerNutrition playerNutrition, int foodLevel, EnumSet<Nutrition.NutritionGroup> groups) {
        for (Nutrition.NutritionGroup group : groups) {
            playerNutrition.increasePlayerNutrition(group, foodLevel * Nutrition.FOOD_NUTRITION_MODIFIER);
        }
    }

}
