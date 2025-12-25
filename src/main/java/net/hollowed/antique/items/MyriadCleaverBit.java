package net.hollowed.antique.items;

import net.hollowed.combatamenities.util.items.CAComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;

public class MyriadCleaverBit extends MyriadToolBitItem{

    public MyriadCleaverBit(Properties settings) {
        super(settings);
    }

    @Override
    public ItemUseAnimation toolGetUseAction(ItemStack stack) {
        return ItemUseAnimation.TRIDENT;
    }

    @Override
    public boolean toolOnStoppedUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        if (user.onGround()) {
            for (Entity entity : world.getEntities(user, user.getBoundingBox().inflate(3, 0.5, 3))) {
                entity.push(entity.position().subtract(user.position()).normalize().multiply(1.25, 0.5, 1.25).add(0, 0.75, 0));
                entity.hurtMarked = true;
            }

            return true;
        }

        return false;
    }

    @Override
    public InteractionResult toolUse(Level world, Player user, InteractionHand hand) {
        user.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public void setToolAttributes(ItemStack tool) {
        tool.set(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 6, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.2, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(Identifier.withDefaultNamespace("base_attack_range"), 0.5, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build());
        tool.remove(DataComponents.TOOL);
        tool.set(CAComponents.INTEGER_PROPERTY, 1);
    }
}
