package dev.stormwatch.vegvisir;

import dev.stormwatch.vegvisir.environment.Nutrition;
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
        if (!(event.getEntity() instanceof Player)) return;
        if (!Nutrition.hasNutrition(event.getItem())) return;
        Player player = (Player) event.getEntity();
        ItemStack stack = event.getItem();
        IForgeItem item = stack.getItem();
        FoodProperties foodProperties = item.getFoodProperties(stack, player);
        // TODO: reduce hunger saturation
        // TODO: add food group values
    }

}
