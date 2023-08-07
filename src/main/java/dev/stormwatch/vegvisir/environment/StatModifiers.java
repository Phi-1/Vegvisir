package dev.stormwatch.vegvisir.environment;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class StatModifiers {
    private static class AttributeIdentifier {
        public String name;
        public UUID uuid;

        public AttributeIdentifier(String name, UUID uuid) {
            this.name = name;
            this.uuid = uuid;
        }
    }

    public static void setAttribute(Player player, Attribute attribute, UUID modifierUUID, String modifierName, double value, AttributeModifier.Operation operation) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return;
        if (instance.getModifier(modifierUUID) != null) instance.removeModifier(modifierUUID);
        instance.addTransientModifier(new AttributeModifier(modifierUUID, modifierName, value, operation));
    }

}
