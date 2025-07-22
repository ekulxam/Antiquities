package net.hollowed.antique.items;

import net.hollowed.antique.Antiquities;
import net.hollowed.antique.index.AntiqueComponents;
import net.hollowed.antique.index.AntiqueItems;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.item.consume.UseAction;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class MyriadToolItem extends Item {

    public MyriadToolItem(Settings settings) {
        super(settings);
    }

    /*
        Stack setting functions
     */

    public static AttributeModifiersComponent createAttributeModifiers(double damage, double attackSpeed, double reach) {
        return AttributeModifiersComponent.builder()
                .add(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, damage - 1, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
                .add(EntityAttributes.ATTACK_SPEED, new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, -4 + attackSpeed, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
                .add(EntityAttributes.ENTITY_INTERACTION_RANGE, new EntityAttributeModifier(Identifier.ofVanilla("base_attack_range"), reach, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
                .build();
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        ItemStack storedStack = getStoredStack(stack);
        ItemStack otherStack = slot.getStack();
        if (clickType == ClickType.RIGHT) {
            if (otherStack.isEmpty()) {

                // Remove the internal selected stack :3
                if (!storedStack.isEmpty()) {
                    slot.setStack(storedStack.copy());
                    storedStack = ItemStack.EMPTY;
                    player.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8F, 1.0F);
                    setStoredStack(stack, storedStack);
                    return true;
                }
            }
        } else {
            if (otherStack.isEmpty()) {
                return false;
            }

            // Check if the item being added is invalid
            if (isInvalidItem(otherStack)) {
                return false;
            }

            if (storedStack.isEmpty()) {
                storedStack = otherStack.split(otherStack.getCount());
                player.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8F, 1.0F);
                setStoredStack(stack, storedStack); // Re-set without empty stacks

                // Clear the cursor stack after adding an item to the tool
                slot.setStack(ItemStack.EMPTY);
                return true;
            }
        }
        return super.onStackClicked(stack, slot, clickType, player);
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        ItemStack storedStack = getStoredStack(stack);
        if (clickType == ClickType.RIGHT) {
            if (otherStack.isEmpty()) {

                // :3
                if (!storedStack.isEmpty()) {
                    cursorStackReference.set(storedStack.copy());
                    storedStack = ItemStack.EMPTY;
                    player.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 1.0F, 1.0F);
                    setStoredStack(stack, storedStack);
                    return true;
                }
            }
        } else {
            if (cursorStackReference.get().isEmpty()) {
                return false;
            }

            if (isInvalidItem(otherStack)) {
                return false;
            }

            if (otherStack.getItem() instanceof MyriadClawBit) {
                player.getInventory().removeStack(slot.getIndex());
                player.getInventory().setStack(slot.getIndex(), stack.copyComponentsToNewStack(AntiqueItems.MYRIAD_STAFF, 1));
            }

            ItemStack temp = getStoredStack(stack);
            storedStack = otherStack.split(otherStack.getCount());
            player.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 1.0F, 1.0F);
            setStoredStack(stack, storedStack);
            cursorStackReference.set(temp);
            return true;
        }
        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    public static boolean isInvalidItem(ItemStack stack) {
        Item item = stack.getItem();
        return !(item instanceof MyriadToolBitItem);
    }

    public static ItemStack getStoredStack(ItemStack tool) {
        return tool.get(AntiqueComponents.MYRIAD_STACK);
    }

    public static void setStoredStack(ItemStack tool, ItemStack newStack) {
        if (newStack.getItem() instanceof MyriadToolBitItem item) {
            item.setToolAttributes(tool);
        } else {
            tool.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder()
                    .add(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, 2.0, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
                    .add(EntityAttributes.ATTACK_SPEED, new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, -2.2, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
                    .add(EntityAttributes.ENTITY_INTERACTION_RANGE, new EntityAttributeModifier(Identifier.ofVanilla("base_attack_range"), 0.25, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
                    .build());
            tool.remove(DataComponentTypes.TOOL);
            tool.set(DataComponentTypes.ITEM_MODEL, Antiquities.id("myriad_tool"));
            tool.remove(net.hollowed.combatamenities.util.items.ModComponents.INTEGER_PROPERTY);
        }
        tool.set(AntiqueComponents.MYRIAD_STACK, newStack);
    }

    /*
        Tool functionality
     */

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getStack().getOrDefault(AntiqueComponents.MYRIAD_STACK, ItemStack.EMPTY).getItem() instanceof MyriadToolBitItem item) {
            return item.toolUseOnBlock(context);
        }
        return ActionResult.PASS;
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (stack.getOrDefault(AntiqueComponents.MYRIAD_STACK, ItemStack.EMPTY).getItem() instanceof MyriadToolBitItem item) {
            return item.toolOnStoppedUsing(stack, world, user, remainingUseTicks);
        }
        return super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        if (stack.getOrDefault(AntiqueComponents.MYRIAD_STACK, ItemStack.EMPTY).getItem() instanceof MyriadToolBitItem item) {
            return item.toolGetUseAction(stack);
        }
        return super.getUseAction(stack);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (user.getStackInHand(hand).getOrDefault(AntiqueComponents.MYRIAD_STACK, ItemStack.EMPTY).getItem() instanceof MyriadToolBitItem item) {
            return item.toolUse(world, user, hand);
        }
        return ActionResult.PASS;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }
}