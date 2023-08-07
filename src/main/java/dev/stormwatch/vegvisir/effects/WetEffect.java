package dev.stormwatch.vegvisir.effects;

import dev.stormwatch.vegvisir.Vegvisir;
import dev.stormwatch.vegvisir.registry.VegvisirEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

public class WetEffect extends MobEffect {

    private static final UUID MOVEMENT_SPEED_ATTRIBUTE_UUID = UUID.fromString("333aaaf2-0d44-452e-9841-6a831cd9c412");
    private static final UUID ATTACK_SPEED_ATTRIBUTE_UUID = UUID.fromString("5874f472-fcfc-46db-b816-41334a3b1890");
    // Attributes detract fraction from base
    private static final double MOVEMENT_SPEED_MODIFIER = -0.2;
    private static final double ATTACK_SPEED_MODIFIER = -0.3;
    // Mine speed just multiplies
    private static final double MINE_SPEED_MODIFIER = 0.6;

    public WetEffect() {
        super(MobEffectCategory.NEUTRAL, 0x5f92e3);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_ATTRIBUTE_UUID.toString(), MOVEMENT_SPEED_MODIFIER, AttributeModifier.Operation.MULTIPLY_BASE);
        this.addAttributeModifier(Attributes.ATTACK_SPEED, ATTACK_SPEED_ATTRIBUTE_UUID.toString(), ATTACK_SPEED_MODIFIER, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    @SubscribeEvent
    public static void onPlayerBreakSpeedEvent(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (player.hasEffect(VegvisirEffects.WET.get())) {
            event.setNewSpeed((float) (event.getOriginalSpeed() * MINE_SPEED_MODIFIER));
        }
    }
}
