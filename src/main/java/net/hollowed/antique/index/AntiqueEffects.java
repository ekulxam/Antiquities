package net.hollowed.antique.index;

import net.hollowed.antique.Antiquities;
import net.hollowed.antique.effect.AnimeEffect;
import net.hollowed.antique.effect.BounceEffect;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public interface AntiqueEffects {

    RegistryEntry<StatusEffect> VOLATILE_BOUNCE_EFFECT = registerEffect("volatile_bouncy", new BounceEffect().addAttributeModifier(EntityAttributes.STEP_HEIGHT, Identifier.ofVanilla("effect.step_height"), 1, EntityAttributeModifier.Operation.ADD_VALUE));
    RegistryEntry<StatusEffect> BOUNCE_EFFECT = registerEffect("bouncy", new BounceEffect().addAttributeModifier(EntityAttributes.STEP_HEIGHT, Identifier.ofVanilla("effect.step_height"), 1, EntityAttributeModifier.Operation.ADD_VALUE));
    RegistryEntry<StatusEffect> ANIME_EFFECT = registerEffect("anime_effect", new AnimeEffect());

    static RegistryEntry<StatusEffect> registerEffect(String id, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(Antiquities.MOD_ID, id), statusEffect);
    }

    static void initialize() {}
}
