package dev.stormwatch.vegvisir;

import dev.stormwatch.vegvisir.effects.FoodExtensionEffect;
import dev.stormwatch.vegvisir.environment.Nutrition;
import dev.stormwatch.vegvisir.registry.VegvisirEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItem;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class NutritionEvents {

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
        int lastFoodLevel = player.getFoodData().getLastFoodLevel();
        float saturation = (float)foodProperties.getNutrition() * foodProperties.getSaturationModifier() * 2.0F;
        float saturationRefill = Math.min(saturation, playerFoodLevel);
        // halve nutrition increase
        // FIXME: if food doesn't use its full value (capped by max) this still detracts half, use getLastFoodLevel?
        player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel() - foodLevel / 2);
        // set effect to re add saturation once it's depleted
        player.addEffect(new MobEffectInstance(VegvisirEffects.FOOD_EXTENSION.get(), -1, FoodExtensionEffect.getAmplifierForSaturationLevel(saturationRefill), false, false, false));
        // TODO: add food group values
    }

}
