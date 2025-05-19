package net.hollowed.antique.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class ItemHoldingUtil {

    public static boolean isHoldingItem(LivingEntity entity, Item item) {
        return entity.getStackInHand(Hand.MAIN_HAND).isOf(item) || entity.getStackInHand(Hand.OFF_HAND).isOf(item);
    }

    public static boolean isHoldingItem(LivingEntity entity, Identifier tagKey) {
        return entity.getStackInHand(Hand.MAIN_HAND).streamTags().toList().contains(TagKey.of(RegistryKeys.ITEM, tagKey)) || entity.getStackInHand(Hand.OFF_HAND).streamTags().toList().contains(TagKey.of(RegistryKeys.ITEM, tagKey));
    }
}
