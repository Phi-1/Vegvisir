package dev.stormwatch.vegvisir.effects;

import dev.stormwatch.vegvisir.registry.VegvisirEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class FoodExtensionEffect extends MobEffect {

    public static final float[] SATURATION_PER_AMPLIFIER = { 0.2f, 0.4f, 0.6f, 0.8f, 1.2f, 1.8f, 2.4f, 2.8f, 3.2f, 3.6f, 4.8f, 6f, 7.2f, 9.6f, 12f, 12.8f, 14.4f, 21.2f };

    public static int getAmplifierForSaturationLevel(float saturation) {
        float smallestDifference = 1000;
        int bestIndex = 0;
        for (int i = 0; i < SATURATION_PER_AMPLIFIER.length; i++) {
            float s = SATURATION_PER_AMPLIFIER[i];
            if (saturation == s) return i;
            float difference = Math.abs(s - saturation);
            if (difference < smallestDifference) {
                smallestDifference = difference;
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    public FoodExtensionEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x000000);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity living, int amplifier) {
        if (living.level.isClientSide()) return;
        if (amplifier > SATURATION_PER_AMPLIFIER.length) return;
        if (!(living instanceof Player player)) return;
        int maxSaturation = player.getFoodData().getFoodLevel();
        float saturation = player.getFoodData().getSaturationLevel();
        if (maxSaturation - saturation >= SATURATION_PER_AMPLIFIER[amplifier]) {
            player.getFoodData().setSaturation(saturation + SATURATION_PER_AMPLIFIER[amplifier]);
            player.removeEffect(VegvisirEffects.FOOD_EXTENSION.get());
        }
    }
}
