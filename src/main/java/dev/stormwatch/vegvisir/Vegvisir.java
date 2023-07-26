package dev.stormwatch.vegvisir;

import com.mojang.logging.LogUtils;
import dev.stormwatch.vegvisir.environment.Nutrition;
import dev.stormwatch.vegvisir.registry.VegvisirEffects;
import dev.stormwatch.vegvisir.registry.VegvisirItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.EnumSet;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Vegvisir.MOD_ID)
public class Vegvisir {
    public static final String MOD_ID = "vegvisir";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Vegvisir() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        VegvisirItems.register(modEventBus);
        VegvisirEffects.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(CapabilityEvents.class);
        MinecraftForge.EVENT_BUS.register(EnvironmentEvents.class);
        MinecraftForge.EVENT_BUS.register(NutritionEvents.class);
        MinecraftForge.EVENT_BUS.register(CampfireEvents.class);

        // TODO: tooltip event for food groups
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private void addCreative(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(VegvisirItems.EYESCREAM);
        }
        if (event.getTab() == CreativeModeTabs.COMBAT) {
            event.accept(VegvisirItems.WOOL_SWEATER);
            event.accept(VegvisirItems.KNIT_CAP);
        }
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void addNutritionTooltips(ItemTooltipEvent event) {
            EnumSet<Nutrition.NutritionGroup> nutritionGroups = Nutrition.getNutrition(event.getItemStack());
            if (nutritionGroups.isEmpty()) return;
            if (nutritionGroups.contains(Nutrition.NutritionGroup.MEAT)) {
                event.getToolTip().add(Component.translatable("vegvisir.tooltip.nutrition.meat"));
            }
            if (nutritionGroups.contains(Nutrition.NutritionGroup.FISH)) {
                event.getToolTip().add(Component.translatable("vegvisir.tooltip.nutrition.fish"));
            }
            if (nutritionGroups.contains(Nutrition.NutritionGroup.VEGETABLE)) {
                event.getToolTip().add(Component.translatable("vegvisir.tooltip.nutrition.vegetable"));
            }
            if (nutritionGroups.contains(Nutrition.NutritionGroup.FRUIT)) {
                event.getToolTip().add(Component.translatable("vegvisir.tooltip.nutrition.fruit"));
            }
            if (nutritionGroups.contains(Nutrition.NutritionGroup.STARCH)) {
                event.getToolTip().add(Component.translatable("vegvisir.tooltip.nutrition.starch"));
            }
        }
    }
}
