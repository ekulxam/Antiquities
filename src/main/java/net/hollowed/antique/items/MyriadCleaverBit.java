package net.hollowed.antique.items;

import net.hollowed.antique.Antiquities;
import net.hollowed.antique.index.AntiqueItems;
import net.hollowed.combatamenities.util.items.ModComponents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public class MyriadCleaverBit extends MyriadToolBitItem{

    public MyriadCleaverBit(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction toolGetUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public boolean toolOnStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        return true;
    }

    @Override
    public ActionResult toolUse(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return ActionResult.CONSUME;
    }

    @Override
    public void setToolAttributes(ItemStack tool) {
        tool.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder()
                .add(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, 6, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
                .add(EntityAttributes.ATTACK_SPEED, new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, -2.2, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
                .add(EntityAttributes.ENTITY_INTERACTION_RANGE, new EntityAttributeModifier(Identifier.ofVanilla("base_attack_range"), 1, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
                .build());
        tool.set(DataComponentTypes.TOOL, new ToolComponent(
                List.of(
                        ToolComponent.Rule.ofNeverDropping(AntiqueItems.registryEntryLookup.getOrThrow(BlockTags.INCORRECT_FOR_IRON_TOOL)),
                        ToolComponent.Rule.ofAlwaysDropping(AntiqueItems.registryEntryLookup.getOrThrow(BlockTags.SHOVEL_MINEABLE), 20)
                ),
                1.0F,
                1,
                true
        ));
        tool.set(DataComponentTypes.ITEM_MODEL, Antiquities.id("myriad_cleaver"));
        tool.set(ModComponents.INTEGER_PROPERTY, 1);
    }
}
