package dev.stormwatch.vegvisir.registry;

import dev.stormwatch.vegvisir.Vegvisir;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VegvisirItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Vegvisir.MOD_ID);

    public static final RegistryObject<Item> EYESCREAM = ITEMS.register("eyescream",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().build())));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
