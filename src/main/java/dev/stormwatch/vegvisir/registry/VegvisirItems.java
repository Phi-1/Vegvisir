package dev.stormwatch.vegvisir.registry;

import dev.stormwatch.vegvisir.Vegvisir;
import dev.stormwatch.vegvisir.items.FishOilItem;
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
//    public static final RegistryObject<Item> KNIT_CAP = ITEMS.register("knit_cap",
//            () -> new ArmorItem(VegvisirArmorMaterials.WOOL, ArmorItem.Type.HELMET, new Item.Properties().durability(40)));
//    public static final RegistryObject<Item> WOOL_SWEATER = ITEMS.register("wool_sweater",
//            () -> new ArmorItem(VegvisirArmorMaterials.WOOL, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(80)));

    // Clothing
    public static final RegistryObject<Item> KNIT_CAP = ITEMS.register("knit_cap",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> WOOL_SWEATER = ITEMS.register("wool_sweater",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> WOOL_SOCKS = ITEMS.register("wool_socks",
            () -> new Item(new Item.Properties()));

    // Food
    public static final RegistryObject<Item> DOUGH = ITEMS.register("dough",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PUMKIN_PIE_BATTER = ITEMS.register("pumpkin_pie_batter",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> UNCOOKED_PUMKIN_PIE = ITEMS.register("uncooked_pumpkin_pie",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EYESCREAM = ITEMS.register("eyescream",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().build())));

    // Materials
    public static final RegistryObject<Item> WOOL_PATCH = ITEMS.register("wool_patch",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> WOOL_YARN = ITEMS.register("wool_yarn",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> FISH_OIL = ITEMS.register("fish_oil",
            FishOilItem::new);
    public static final RegistryObject<Item> RESONANT_CHRYSALIS = ITEMS.register("resonant_chrysalis",
            () -> new Item(new Item.Properties()));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
