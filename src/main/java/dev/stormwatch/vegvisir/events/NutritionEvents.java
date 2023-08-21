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
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.EnumSet;

public class NutritionEvents {

    // TODO: tick nutrition

    @SubscribeEvent
    public static void onPlayerDeathEvent(PlayerEvent.Clone event) {
        if (event.getEntity().level.isClientSide()) return;
        if (!event.isWasDeath()) return;
        Player newPlayer = event.getEntity();
        Player originalPlayer = event.getOriginal();

        PlayerNutrition playerNutrition = newPlayer.getCapability(PlayerNutritionProvider.PLAYER_NUTRITION).orElse(PlayerNutrition.EMPTY);
        PlayerNutrition oldPlayerNutrition = originalPlayer.getCapability(PlayerNutritionProvider.PLAYER_NUTRITION).orElse(PlayerNutrition.EMPTY);
        if (playerNutrition == PlayerNutrition.EMPTY || oldPlayerNutrition == PlayerNutrition.EMPTY) return;
        playerNutrition.copyFrom(oldPlayerNutrition);
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
        // TODO: check that this calculation is correct im half asleep
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
