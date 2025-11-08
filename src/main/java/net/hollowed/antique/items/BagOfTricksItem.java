package net.hollowed.antique.items;

import net.hollowed.antique.Antiquities;
import net.hollowed.antique.index.AntiqueDataComponentTypes;
import net.hollowed.antique.items.tooltips.BagOfTricksTooltipData;
import net.hollowed.combatamenities.util.items.CAComponents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BagOfTricksItem extends Item {
    public static final int MAX_STACKS = 8;
    public static int index = 0;
    public static List<ItemStack> lastContents = null;

    private static final int FULL_ITEM_BAR_COLOR = ColorHelper.fromFloats(1.0F, 1.0F, 0.33F, 0.33F);
    private static final int ITEM_BAR_COLOR = ColorHelper.fromFloats(1.0F, 0.44F, 0.53F, 1.0F);

    public BagOfTricksItem(Settings settings) {
        super(settings);
    }

    public boolean isItemBarVisible(ItemStack stack) {
        List<ItemStack> storedStacks = new ArrayList<>(getStoredStacks(stack));  // Create a mutable copy of the list
        return !storedStacks.isEmpty();
    }

    public int getItemBarStep(ItemStack stack) {
        List<ItemStack> storedStacks = new ArrayList<>(getStoredStacks(stack));  // Create a mutable copy of the list

        int maxStacks = 8;
        return Math.round((float) storedStacks.size() / maxStacks * 13);
    }

    public int getItemBarColor(ItemStack stack) {
        List<ItemStack> storedStacks = new ArrayList<>(getStoredStacks(stack));  // Create a mutable copy of the list
        return storedStacks.size() == 8 ? FULL_ITEM_BAR_COLOR : ITEM_BAR_COLOR;
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        TooltipDisplayComponent tooltipDisplayComponent = stack.getOrDefault(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplayComponent.DEFAULT);
        return !tooltipDisplayComponent.shouldDisplay(AntiqueDataComponentTypes.SATCHEL_STACK)
                ? Optional.empty()
                : Optional.ofNullable(stack.get(AntiqueDataComponentTypes.SATCHEL_STACK)).map(items -> new BagOfTricksTooltipData(items, stack));
    }

    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
        List<ItemStack> contents = entity.getStack().get(AntiqueDataComponentTypes.SATCHEL_STACK);
        if (contents != null) {
            entity.getStack().set(AntiqueDataComponentTypes.SATCHEL_STACK, List.of());
            ItemUsage.spawnItemContents(entity, contents);
        }
    }

    @Override
    public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        List<ItemStack> storedStacks = new ArrayList<>(getStoredStacks(stack));  // Create a mutable copy of the list
        ItemStack otherStack = slot.getStack();
        if (clickType == ClickType.RIGHT) {
            if (otherStack.isEmpty()) {

                // Remove the internal selected stack :3
                if (!storedStacks.isEmpty() && !storedStacks.getFirst().isEmpty()) {
                    slot.setStack(storedStacks.getFirst().copy());
                    storedStacks.set(0, ItemStack.EMPTY);
                    player.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8F, 1.0F);
                    setStoredStacks(stack, storedStacks);
                    return true;
                }
            }
        } else {
            if (otherStack.isEmpty()) {
                return false;
            }

            if (isInvalidItem(otherStack)) {
                player.playSound(SoundEvents.ITEM_BUNDLE_INSERT_FAIL, 0.8F, 1.0F);
                return true;
            }

            for (int i = 0; i < MAX_STACKS; i++) {
                if (i >= storedStacks.size()) {
                    storedStacks.add(ItemStack.EMPTY);
                }
            }

            int otherStackCount = otherStack.getCount();
            ItemStack remainder = addToStoredStacks(storedStacks, otherStack);
            if (remainder.getCount() == otherStackCount) {
                player.playSound(SoundEvents.ITEM_BUNDLE_INSERT_FAIL, 0.8F, 1.0F);
            } else {
                player.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8F, 1.0F);
            }
            setStoredStacks(stack, storedStacks);
            return true;
        }
        return super.onStackClicked(stack, slot, clickType, player);
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        List<ItemStack> storedStacks = new ArrayList<>(getStoredStacks(stack));  // Create a mutable copy of the list
        if (clickType == ClickType.RIGHT) {
            if (otherStack.isEmpty()) {

                int index = !hasSelectedStack(stack) ? 0 : getInternalIndex(stack);

                // Remove the internal selected stack :3
                if (!storedStacks.isEmpty() && !storedStacks.get(index).isEmpty()) {
                    cursorStackReference.set(storedStacks.get(index).copy());
                    storedStacks.set(index, ItemStack.EMPTY);
                    player.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 1.0F, 1.0F);
                    setStoredStacks(stack, storedStacks);
                    return true;
                }
            }
        } else {
            if (cursorStackReference.get().isEmpty()) {
                return false;
            }

            if (isInvalidItem(otherStack)) {
                player.playSound(SoundEvents.ITEM_BUNDLE_INSERT_FAIL, 1.0F, 1.0F);
                return true;
            }

            for (int i = 0; i < MAX_STACKS; i++) {
                if (i >= storedStacks.size()) {
                    storedStacks.add(ItemStack.EMPTY);
                }
            }

            int otherStackCount = otherStack.getCount();
            ItemStack remainder = addToStoredStacks(storedStacks, otherStack);
            if (remainder.getCount() == otherStackCount) {
                player.playSound(SoundEvents.ITEM_BUNDLE_INSERT_FAIL, 1.0F, 1.0F);
            } else {
                player.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 1.0F, 1.0F);
            }
            setStoredStacks(stack, storedStacks);
            return true;
        }
        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        List<ItemStack> stacks = stack.getOrDefault(AntiqueDataComponentTypes.SATCHEL_STACK, List.of());
        if (stacks.isEmpty() || stacks.getFirst().isEmpty()) return ActionResult.PASS;

        if (stacks.getFirst().getItem() instanceof ProjectileItem projectileItem) {
            ProjectileEntity entity = projectileItem.createEntity(world, user.getEyePos(), stacks.getFirst(), Direction.DOWN);
            entity.setOwner(user);
            entity.setPosition(user.getX(), user.getEyeY() - 0.10000000149011612, user.getZ());

            float f = -MathHelper.sin(user.getYaw() * 0.017453292F) * MathHelper.cos(user.getPitch() * 0.017453292F);
            float g = -MathHelper.sin(user.getPitch() * 0.017453292F);
            float h = MathHelper.cos(user.getYaw() * 0.017453292F) * MathHelper.cos(user.getPitch() * 0.017453292F);
            Vec3d throwDir = new Vec3d(f, g, h).normalize();
            Vec3d playerVel = user.getVelocity().normalize();
            double dot = throwDir.dotProduct(playerVel); // Value between -1 and 1
            float strength = 1.0F;
            if (dot > 0.1) {
                strength += (float) (dot * 2.0);
            }
            entity.setVelocity(f, g, h, strength, 1.0F);
            world.spawnEntity(entity);

            stacks.getFirst().decrementUnlessCreative(1, user);
            BagOfTricksItem.setStoredStacks(stack, stacks);
            return ActionResult.SUCCESS;
        }
        return super.use(world, user, hand);
    }

    public static boolean hasSelectedStack(ItemStack stack) {
        return stack.getOrDefault(CAComponents.INTEGER_PROPERTY, -1) != -1;
    }

    public boolean isInvalidItem(ItemStack stack) {
        return !stack.isIn(TagKey.of(RegistryKeys.ITEM, Antiquities.id("bag_projectiles")));
    }

    public static List<ItemStack> getStoredStacks(ItemStack satchel) {
        List<ItemStack> storedStacks = satchel.get(AntiqueDataComponentTypes.SATCHEL_STACK);
        return storedStacks != null ? storedStacks : new ArrayList<>();
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        BagOfTricksItem.index = index;
    }

    public static void setInternalIndex(ItemStack stack, int internalIndex) {
        stack.set(CAComponents.INTEGER_PROPERTY, internalIndex);
    }

    public static int getInternalIndex(ItemStack stack) {
        return stack.getOrDefault(CAComponents.INTEGER_PROPERTY, -1);
    }

    public static ItemStack addToStoredStacks(List<ItemStack> storedStacks, ItemStack stack) {

        for (int i = 0; i < storedStacks.size(); i++) {
            ItemStack storedStack = storedStacks.get(i);
            int stackRoom = storedStack.getMaxCount() - storedStack.getCount();
            if (stackRoom > 0 && !storedStack.isEmpty()) {
                if (ItemStack.areItemsAndComponentsEqual(storedStack, stack)) {
                    int stackAmount = Math.min(stack.getCount(), stackRoom);
                    storedStack.increment(stackAmount);
                    stack.decrement(stackAmount);
                    storedStacks.set(i, storedStack);

                    if (!stack.isEmpty()) {
                        if (storedStacks.size() < MAX_STACKS) {
                            storedStacks.add(stack.copy());
                            stack.setCount(0);
                            return ItemStack.EMPTY;
                        } else if (storedStacks.contains(ItemStack.EMPTY)) {
                            storedStacks.set(storedStacks.indexOf(ItemStack.EMPTY), stack.copy());
                            stack.setCount(0);
                            return ItemStack.EMPTY;
                        } else {
                            for (ItemStack checkStack : storedStacks) {
                                if (checkStack.getCount() < checkStack.getMaxCount() && ItemStack.areItemsAndComponentsEqual(checkStack, stack)) {
                                    return addToStoredStacks(storedStacks, stack);
                                }
                            }
                        }
                    }
                }
            } else if (storedStack.isEmpty()) {
                storedStacks.set(i, stack.copy());
                stack.setCount(0);
                return ItemStack.EMPTY;
            }
        }

        if (storedStacks.size() < MAX_STACKS && storedStacks.stream().noneMatch(ItemStack::isEmpty)) {
            storedStacks.add(stack.copy());
            stack.setCount(0);
            return ItemStack.EMPTY;
        }

        return stack;
    }

    public static void setStoredStacks(ItemStack satchel, List<ItemStack> stacks) {
        List<ItemStack> filteredStacks = new ArrayList<>();
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                filteredStacks.add(stack.copy());
            }
        }

        satchel.set(AntiqueDataComponentTypes.SATCHEL_STACK, filteredStacks);
    }
}