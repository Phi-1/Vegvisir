package dev.stormwatch.vegvisir;

import com.mojang.logging.LogUtils;
import dev.stormwatch.vegvisir.client.SpinningWheelStates;
import dev.stormwatch.vegvisir.datagen.VegvisirRecipeProvider;
import dev.stormwatch.vegvisir.effects.WetEffect;
import dev.stormwatch.vegvisir.environment.Nutrition;
import dev.stormwatch.vegvisir.events.*;
import dev.stormwatch.vegvisir.networking.VegvisirNetworking;
import dev.stormwatch.vegvisir.registry.*;
import dev.stormwatch.vegvisir.renderers.OrbRenderer;
import dev.stormwatch.vegvisir.renderers.SpinningWheelRenderer;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;
import top.theillusivec4.curios.api.SlotTypeMessage;

import java.util.EnumSet;

@Mod(Vegvisir.MOD_ID)
public class Vegvisir {
    // TODO: way to see nutrition and temperature ingame
    // TODO: clothing durability
    // TODO: warmth potion recipe

    public static final String MOD_ID = "vegvisir";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Vegvisir() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        GeckoLib.initialize();
        registerCuriosSlots();

        VegvisirItems.register(modEventBus);
        VegvisirEffects.register(modEventBus);
        VegvisirPotions.register(modEventBus);
        VegvisirBlocks.register(modEventBus);
        VegvisirBlockEntityTypes.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(WetEffect.class);
        MinecraftForge.EVENT_BUS.register(CapabilityEvents.class);
        MinecraftForge.EVENT_BUS.register(EnvironmentEvents.class);
        MinecraftForge.EVENT_BUS.register(NutritionEvents.class);
        MinecraftForge.EVENT_BUS.register(CampfireEvents.class);
        MinecraftForge.EVENT_BUS.register(MiscEvents.class);
    }

    private void gatherData(GatherDataEvent event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        event.getGenerator().addProvider(
                event.includeServer(),
                new VegvisirRecipeProvider(packOutput)
        );
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            VegvisirNetworking.register();
            registerPotionRecipes();
        });
    }

    private void addCreative(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(VegvisirItems.EYESCREAM);
            event.accept(VegvisirItems.DOUGH);
            event.accept(VegvisirItems.PUMKIN_PIE_BATTER);
            event.accept(VegvisirItems.UNCOOKED_PUMKIN_PIE);
        }
        if (event.getTab() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(VegvisirBlocks.SPINNING_WHEEL_BLOCK);
        }
        if (event.getTab() == CreativeModeTabs.INGREDIENTS) {
            event.accept(VegvisirItems.WOOL_YARN);
            event.accept(VegvisirItems.WOOL_PATCH);
            event.accept(VegvisirItems.FISH_OIL);
            event.accept(VegvisirItems.HEARTHESSENCE);
        }
        if (event.getTab() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(VegvisirItems.KNIT_CAP);
            event.accept(VegvisirItems.WOOL_SWEATER);
            event.accept(VegvisirItems.WOOL_SOCKS);
        }
    }

    private void registerPotionRecipes() {
        BrewingRecipeRegistry.addRecipe(new VegvisirPotionRecipe(VegvisirItems.HEARTHESSENCE.get(), Potions.AWKWARD, VegvisirPotions.WARMTH_POTION.get()));
        BrewingRecipeRegistry.addRecipe(new VegvisirPotionRecipe(Items.REDSTONE, VegvisirPotions.WARMTH_POTION.get(), VegvisirPotions.WARMTH_POTION_LONG.get()));
        BrewingRecipeRegistry.addRecipe(new VegvisirPotionRecipe(Items.GLOWSTONE_DUST, VegvisirPotions.WARMTH_POTION.get(), VegvisirPotions.WARMTH_POTION_STRONG.get()));
    }

    private void registerCuriosSlots() {
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE,
                () -> new SlotTypeMessage.Builder("beanie")
                        .icon(new ResourceLocation(Vegvisir.MOD_ID, "slot/beanie"))
                        .priority(10)
                        .size(1)
                        .build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE,
                () -> new SlotTypeMessage.Builder("sweater")
                        .icon(new ResourceLocation(Vegvisir.MOD_ID, "slot/sweater"))
                        .priority(10)
                        .size(1)
                        .build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE,
                () -> new SlotTypeMessage.Builder("socks")
                        .icon(new ResourceLocation(Vegvisir.MOD_ID, "slot/socks"))
                        .priority(10)
                        .size(1)
                        .build());
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientSetupEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }

        @SubscribeEvent
        public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(VegvisirBlockEntityTypes.SPINNING_WHEEL_BLOCK_ENTITY.get(), SpinningWheelRenderer::new);
            event.registerBlockEntityRenderer(VegvisirBlockEntityTypes.ORB_BLOCK_ENTITY.get(), OrbRenderer::new);
        }

    }

    @Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class ClientForgeEvents {

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

        @SubscribeEvent
        public static void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
            if (event.phase == TickEvent.Phase.START) return;
            if (SpinningWheelStates.wasUpdated()) {
                SpinningWheelStates.updateAllSpinningWheels(event.player.level);
            }
        }
    }
}
