package net.hollowed.antique.items;

import net.hollowed.antique.index.AntiqueSounds;
import net.hollowed.antique.index.AntiqueDataComponentTypes;
import net.hollowed.antique.index.AntiqueEffects;
import net.hollowed.antique.index.AntiqueEntities;
import net.hollowed.antique.entities.CakeEntity;
import net.hollowed.antique.util.LeftClickHandler;
import net.hollowed.antique.util.delay.TickDelayScheduler;
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
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.consume.UseAction;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

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
                    player.playSound(AntiqueSounds.STAFF_REMOVE, 0.5F, 1.0F);
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
            player.playSound(AntiqueSounds.STAFF_INSERT, 1.0F, 1.0F);
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
                    player.playSound(AntiqueSounds.STAFF_REMOVE, 0.5F, 1.0F);
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
                player.playSound(AntiqueSounds.STAFF_INSERT, 1.0F, 1.0F);
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
            stack.set(net.hollowed.combatamenities.util.items.CAComponents.INTEGER_PROPERTY, 1);
        } else {
            stack.set(net.hollowed.combatamenities.util.items.CAComponents.INTEGER_PROPERTY, 0);
        }
    }

    public static void setStoredStack(ItemStack tool, ItemStack newStack) {
        tool.set(AntiqueDataComponentTypes.MYRIAD_STACK, newStack);
    }

    public static boolean isInvalidItem(ItemStack stack) {
        Item item = stack.getItem();
        return !(item instanceof BlockItem);
    }

    public static ItemStack getStoredStack(ItemStack tool) {
        return tool.get(AntiqueDataComponentTypes.MYRIAD_STACK);
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!user.getStackInHand(hand).getOrDefault(AntiqueDataComponentTypes.MYRIAD_STACK, ItemStack.EMPTY).equals(ItemStack.EMPTY)) {
            user.setCurrentHand(hand);
            return ActionResult.PASS;
        }
        return ActionResult.FAIL;
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (stack.getOrDefault(AntiqueDataComponentTypes.MYRIAD_STACK, ItemStack.EMPTY).isOf(Blocks.GOLD_BLOCK.asItem())) {
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
        if (stack.getOrDefault(AntiqueDataComponentTypes.MYRIAD_STACK, ItemStack.EMPTY).isOf(Blocks.CAKE.asItem())) {
            if (!world.isClient()) {
                CakeEntity cake = new CakeEntity(AntiqueEntities.CAKE_ENTITY, world);
                cake.setPos(user.getX(), user.getY() + 2, user.getZ());
                cake.setVelocity(user.getRotationVector().multiply(0.75));
                cake.setAngles(-user.getHeadYaw(), -user.getPitch());
                world.spawnEntity(cake);

                world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_WIND_CHARGE_THROW, SoundCategory.NEUTRAL, 0.5F, 0.1F);
            }
        }
    }

    @Override
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postHit(stack, target, attacker);
        if (stack.getOrDefault(AntiqueDataComponentTypes.MYRIAD_STACK, ItemStack.EMPTY).isOf(Blocks.GOLD_BLOCK.asItem())) {
            target.addStatusEffect(new StatusEffectInstance(AntiqueEffects.ANIME_EFFECT, 60, 0, true, true));
            TickDelayScheduler.schedule(1, () -> {
                TickDelayScheduler.schedule(1, () -> breakSphere(attacker.getEntityWorld(), target.getBlockPos().up(), 2));
                target.setVelocity(attacker.getRotationVec(0).multiply(10, 5, 10));
                target.velocityModified = true;
            });
        }
    }

    @SuppressWarnings("all")
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

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getStack().getOrDefault(AntiqueDataComponentTypes.MYRIAD_STACK, ItemStack.EMPTY).isEmpty() && Objects.requireNonNull(context.getPlayer()).isSneaking()) {
            PlayerEntity user = context.getPlayer();
            context.getWorld().playSound(null, user.getX(), user.getY(), user.getZ(), AntiqueSounds.STAFF_INSERT, SoundCategory.NEUTRAL, 1.0F, 1.0F);

            BlockState block = context.getWorld().getBlockState(context.getBlockPos());
            ItemStack stack = block.getBlock().asItem().getDefaultStack();
            TickDelayScheduler.schedule(8, () -> {
                context.getStack().set(AntiqueDataComponentTypes.MYRIAD_STACK, stack);
                context.getWorld().breakBlock(context.getBlockPos(), false);
            });
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
