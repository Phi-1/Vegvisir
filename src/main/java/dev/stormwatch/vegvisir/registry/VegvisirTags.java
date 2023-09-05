package dev.stormwatch.vegvisir.registry;


import dev.stormwatch.vegvisir.Vegvisir;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class VegvisirTags {

    public static class Items {
        public static final TagKey<Item> MEAT = ItemTags.create(new ResourceLocation(Vegvisir.MOD_ID, "nutrition/meat"));
        public static final TagKey<Item> FISH = ItemTags.create(new ResourceLocation(Vegvisir.MOD_ID, "nutrition/fish"));
        public static final TagKey<Item> VEGETABLE = ItemTags.create(new ResourceLocation(Vegvisir.MOD_ID, "nutrition/vegetable"));
        public static final TagKey<Item> FRUIT = ItemTags.create(new ResourceLocation(Vegvisir.MOD_ID, "nutrition/fruit"));
        public static final TagKey<Item> STARCH = ItemTags.create(new ResourceLocation(Vegvisir.MOD_ID, "nutrition/starch"));

        public static final TagKey<Item> RAW_FISH = ItemTags.create(new ResourceLocation(Vegvisir.MOD_ID, "nutrition/raw_fish"));
    }

}
