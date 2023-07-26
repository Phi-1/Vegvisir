package dev.stormwatch.vegvisir.registry;

import dev.stormwatch.vegvisir.Vegvisir;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VegvisirItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Vegvisir.MOD_ID);

    // Armor
    // TODO: different color beanies?
    public static final RegistryObject<Item> KNIT_CAP = ITEMS.register("knit_cap",
            () -> new ArmorItem(VegvisirArmorMaterials.WOOL, ArmorItem.Type.HELMET, new Item.Properties().durability(40)));
    // TODO: different color sweaters with different patterns, fuck dyeable armor
    public static final RegistryObject<Item> WOOL_SWEATER = ITEMS.register("wool_sweater",
            () -> new ArmorItem(VegvisirArmorMaterials.WOOL, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(80)));

    // Food
    public static final RegistryObject<Item> EYESCREAM = ITEMS.register("eyescream",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().build())));

    // Materials
    public static final RegistryObject<Item> WOOL_PATCH = ITEMS.register("wool_patch",
            () -> new Item(new Item.Properties()));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
