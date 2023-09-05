package dev.stormwatch.vegvisir.registry;

import dev.stormwatch.vegvisir.Vegvisir;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VegvisirPotions {

    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, Vegvisir.MOD_ID);

    public static final RegistryObject<Potion> WARMTH_POTION = POTIONS.register("warmth_potion",
            () -> new Potion(new MobEffectInstance(VegvisirEffects.WARMTH.get(), 6000, 0)));
    public static final RegistryObject<Potion> WARMTH_POTION_LONG = POTIONS.register("warmth_potion_long",
            () -> new Potion(new MobEffectInstance(VegvisirEffects.WARMTH.get(), 14400, 0)));
    public static final RegistryObject<Potion> WARMTH_POTION_STRONG = POTIONS.register("warmth_potion_strong",
            () -> new Potion(new MobEffectInstance(VegvisirEffects.WARMTH.get(), 6000, 1)));

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}
