package dev.stormwatch.vegvisir.registry;

import dev.stormwatch.vegvisir.Vegvisir;
import dev.stormwatch.vegvisir.effects.FoodExtensionEffect;
import dev.stormwatch.vegvisir.effects.WarmthEffect;
import dev.stormwatch.vegvisir.effects.WetEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VegvisirEffects {

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Vegvisir.MOD_ID);

    public static final RegistryObject<MobEffect> WARMTH = EFFECTS.register("effect_warmth", WarmthEffect::new);
    public static final RegistryObject<MobEffect> WET = EFFECTS.register("effect_wet", WetEffect::new);
    public static final RegistryObject<MobEffect> FOOD_EXTENSION = EFFECTS.register("effect_food_extension", FoodExtensionEffect::new);

    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
    }

}
