package net.hollowed.antique.items.custom.myriadStaff;

import net.hollowed.antique.Antiquities;
import net.hollowed.antique.component.ModComponents;
import net.hollowed.antique.enchantments.EnchantmentListener;
import net.hollowed.antique.util.EntityAnimeActivator;
import net.hollowed.antique.util.LeftClickHandler;
import net.hollowed.antique.util.TickDelayScheduler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

public class MyriadStaffItem extends Item {
    public MyriadStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        ItemStack storedStack = getStoredStack(stack);  // Create a mutable copy of the list
        if (clickType == ClickType.RIGHT) {
            if (otherStack.isEmpty()) {

                // Remove the internal selected stack :3
                if (!storedStack.isEmpty()) {
                    cursorStackReference.set(storedStack.copy());
                    storedStack = ItemStack.EMPTY;
                    player.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 1.0F, 1.0F);
                    setStoredStack(stack, storedStack); // Re-set without empty stacks
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

            ItemStack temp = getStoredStack(stack);
            storedStack = otherStack.split(otherStack.getCount());
            player.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 1.0F, 1.0F);
            setStoredStack(stack, storedStack); // Re-set without empty stacks

            cursorStackReference.set(temp);
            return true;
        }
        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
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
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);
        Object name = stack.getOrDefault(DataComponentTypes.CUSTOM_NAME, "None");
        if (name.equals(Text.literal("Perfected Staff")) || name.equals(Text.literal("Orb Staff")) || name.equals(Text.literal("Lapis Staff"))) {
            stack.set(net.hollowed.combatamenities.util.items.ModComponents.INTEGER_PROPERTY, 1);
        } else {
            stack.set(net.hollowed.combatamenities.util.items.ModComponents.INTEGER_PROPERTY, 0);
        }
    }

    public static void setStoredStack(ItemStack tool, ItemStack newStack) {
        tool.set(ModComponents.MYRIAD_STACK, newStack);
    }

    public static boolean isInvalidItem(ItemStack stack) {
        Item item = stack.getItem();
        return !(item instanceof BlockItem);
    }

    public static ItemStack getStoredStack(ItemStack tool) {
        return tool.get(ModComponents.MYRIAD_STACK);
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!user.getStackInHand(hand).getOrDefault(ModComponents.MYRIAD_STACK, ItemStack.EMPTY).equals(ItemStack.EMPTY)) {
            user.setCurrentHand(hand);
            return ActionResult.PASS;
        }
        return ActionResult.FAIL;
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (stack.getOrDefault(ModComponents.MYRIAD_STACK, ItemStack.EMPTY).isOf(Blocks.GOLD_BLOCK.asItem())) {
//            for (int i = 1; i < 101; i++) {
//                // world.breakBlock - particles, sound, lag
//                // world.setBlockState - no particles or sound, no lag
//            }

            int radius = 14;

            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        double distance = Math.sqrt(x * x + y * y + z * z);
                        if (distance <= radius) {
                            BlockPos pos = user.getBlockPos().add(x, y, z);
                            if (pos.getSquaredDistance(user.getX(), user.getY(), user.getZ()) < 4) {
                                world.breakBlock(pos, false);
                            } else {
                                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 0);
                            }
                        }
                    }
                }
            }
        }
        return super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    /*
        Now the actually fun stuff
     */

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        super.usageTick(world, user, stack, remainingUseTicks);
        LeftClickHandler.checkRightClickInAir();
    }

    @Override
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postHit(stack, target, attacker);
        if (stack.getOrDefault(ModComponents.MYRIAD_STACK, ItemStack.EMPTY).isOf(Blocks.GOLD_BLOCK.asItem())) {
            breakSphere(attacker.getWorld(), target.getBlockPos(), 2);
            target.addStatusEffect(new StatusEffectInstance(Antiquities.ANIME_EFFECT, 30, 0, true, true));
            TickDelayScheduler.schedule(1, () -> {
                target.setVelocity(attacker.getRotationVec(0).multiply(4, 2, 4));
                target.velocityModified = true;
            });
        }
    }

    @Unique
    private void breakSphere(World world, BlockPos center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);
                    if (distance <= radius) {
                        BlockPos pos = center.add(x, y, z);
                        if (!world.getBlockState(pos).isAir()) {
                            world.breakBlock(pos, false);
                        }
                    }
                }
            }
        }
    }
}
