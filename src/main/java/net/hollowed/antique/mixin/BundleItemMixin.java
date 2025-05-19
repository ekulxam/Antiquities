package net.hollowed.antique.mixin;

import net.hollowed.antique.enchantments.EnchantmentListener;
import net.hollowed.antique.items.custom.SatchelItem;
import net.minecraft.block.TntBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.projectile.thrown.SplashPotionEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(BundleItem.class)
public abstract class BundleItemMixin extends Item {

    public BundleItemMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "onStackClicked", at = @At("HEAD"), cancellable = true)
    private void preventSatchelsOnStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack itemStack = slot.getStack();
        if (itemStack.getItem() instanceof SatchelItem) { // Replace 'SatchelItem' with your satchel item class
            cir.setReturnValue(false); // Cancel the action
        }
    }

    @Inject(method = "onClicked", at = @At("HEAD"), cancellable = true)
    private void preventSatchelsOnClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, CallbackInfoReturnable<Boolean> cir) {
        if (otherStack.getItem() instanceof SatchelItem) { // Replace 'SatchelItem' with your satchel item class
            cir.setReturnValue(false); // Cancel the action
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void applyEnchantableComponent(Settings settings, CallbackInfo ci) {
        // Modify the enchantability of the item during initialization
        settings.enchantable(5);
    }

    // Shadow the method that retrieves the first item from the bundle.
    @Shadow
    private static Optional<ItemStack> popFirstBundledStack(ItemStack stack, PlayerEntity player, BundleContentsComponent contents) {
        return Optional.empty();
    }

    @Unique
    public void placeBlockFromBundle(ItemUsageContext context, ItemStack stack) {
        ((BlockItem) stack.getItem()).place(new ItemPlacementContext(context));
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        Hand hand = context.getHand();
        PlayerEntity player = context.getPlayer();
        ItemStack stack = Objects.requireNonNull(player).getStackInHand(hand);

        if (EnchantmentListener.hasEnchantment(stack, "antique:jumbling")) {

            // Filter for BlockItems directly and create a list of matching items
            BundleContentsComponent bundleContentsComponent = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
            if (bundleContentsComponent != null) {
                List<ItemStack> blockItems = bundleContentsComponent.stream()
                        .filter(itemStack -> itemStack.getItem() instanceof BlockItem)
                        .toList();

                // Additional logic for Jumbling enchantment
                if (!blockItems.isEmpty()) {
                    // Get the current contents of the bundle
                    if (!bundleContentsComponent.isEmpty()) {
                        BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(bundleContentsComponent);

                        // Check if any item in the bundle is not a BlockItem
                        boolean hasNonBlockItem = bundleContentsComponent.stream()
                                .anyMatch(itemStack -> !(itemStack.getItem() instanceof BlockItem));

                        // If the bundle contains any non-block item, cancel the functionality
                        if (hasNonBlockItem) {
                            return ActionResult.PASS; // This effectively cancels the enchantment functionality
                        }


                        // Randomly select an index from the filtered block items
                        int randomIndex = new Random().nextInt(blockItems.size());

                        builder.setSelectedStackIndex(randomIndex);
                        ItemStack selectedBlockStack = builder.removeSelected();

                        // Place the randomly selected block
                        if (selectedBlockStack != null) {
                            placeBlockFromBundle(context, selectedBlockStack);
                            if (!player.isCreative()) {
                                selectedBlockStack.decrement(1);  // Decrease the item stack
                                stack.increment(1);
                            }
                            builder.add(selectedBlockStack);

                            // Rebuild and set the modified bundle contents
                            stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
                        }
                    }
                }
            }
            return ActionResult.SUCCESS;
        } else {
            return ActionResult.PASS;
        }
    }

    @Inject(method = "onStackClicked", at = @At("RETURN"))
    public void onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (EnchantmentListener.hasEnchantment(stack, "antique:curse_of_voiding")) {
            // Get the current contents of the bundle
            BundleContentsComponent bundleContentsComponent = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
            assert bundleContentsComponent != null;

            // Create a builder to modify the contents
            BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(bundleContentsComponent);

            ItemStack itemStack = builder.removeSelected();

            // Calculate the XP based on the stack size
            if (itemStack != null) {
                float xpToGrant = calculateXpFromStackSize(itemStack.getCount(), itemStack) / 2;
                if (xpToGrant > 0) {
                    spawnXpOrb(player, (int) xpToGrant + 1);
                }
            }

            // Remove the selected item from the bundle
            builder.removeSelected();
            stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
        }
    }

    @Inject(method = "onClicked", at = @At("RETURN"))
    public void onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, CallbackInfoReturnable<Boolean> cir) {
        if (EnchantmentListener.hasEnchantment(stack, "antique:curse_of_voiding")) {
            // Get the current contents of the bundle
            BundleContentsComponent bundleContentsComponent = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
            assert bundleContentsComponent != null;

            // Create a builder to modify the contents
            BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(bundleContentsComponent);

            ItemStack itemStack = builder.removeSelected();

            // Calculate the XP based on the stack size
            if (itemStack != null) {
                float xpToGrant = calculateXpFromStackSize(itemStack.getCount(), itemStack) / 2;
                if (xpToGrant > 0) {
                    spawnXpOrb(player, (int) xpToGrant + 1);
                }
            }

            // Remove the selected item from the bundle
            builder.removeSelected();
            stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
        }
    }

    /**
     * Helper method to calculate the XP amount based on the stack size.
     * Adjust the multiplier as needed to control how much XP is granted.
     */
    @Unique
    private float calculateXpFromStackSize(int stackSize, ItemStack stack) {
        float xp = Math.max(1f, stackSize);

        switch (stack.getRarity()) {
            case Rarity.EPIC -> xp *= 128;
            case Rarity.RARE -> xp *= 64;
            case Rarity.UNCOMMON -> xp *= 32;
            default -> {}
        }

        return xp;
    }

    /**
     * Helper method to spawn an XP orb at the player's position.
     */
    @Unique
    private void spawnXpOrb(PlayerEntity player, int xpAmount) {
        if (xpAmount > 0 && player.getWorld() instanceof ServerWorld) {
            // Spawn the XP orb at the player's position with the given amount of XP
            ExperienceOrbEntity xpOrb = new ExperienceOrbEntity(player.getWorld(), player.getX(), player.getY(), player.getZ(), xpAmount);
            player.getWorld().spawnEntity(xpOrb);
        }
    }

    @Inject(method = "popFirstBundledStack", at = @At("HEAD"), cancellable = true)
    private static void popFirstBundledStackInject(ItemStack stack, PlayerEntity player, BundleContentsComponent contents, CallbackInfoReturnable<Optional<ItemStack>> cir) {

        boolean hasProjectingEnchantment = EnchantmentListener.hasEnchantment(stack, "antique:projecting");

        BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(contents);
        ItemStack itemStack = builder.removeSelected();
        if (hasProjectingEnchantment) {
            if (itemStack != null && itemStack.getItem() instanceof BlockItem && ((BlockItem) itemStack.getItem()).getBlock() instanceof TntBlock) {
                // Cancel the pop and handle TNT launching
                boolean hasFlintAndSteel = player.getMainHandStack().getItem() instanceof FlintAndSteelItem ||
                        player.getOffHandStack().getItem() instanceof FlintAndSteelItem ||
                        player.getMainHandStack().getItem() instanceof FireChargeItem ||
                        player.getOffHandStack().getItem() instanceof FireChargeItem;

                boolean isMainhand = player.getMainHandStack().getItem() instanceof FlintAndSteelItem ||
                        player.getMainHandStack().getItem() instanceof FireChargeItem;

                if (hasFlintAndSteel && player.getWorld() instanceof ServerWorld) {
                    ItemStack item = isMainhand ? player.getMainHandStack() : player.getOffHandStack();

                    // Damage flint and steel or consume fire charge
                    if (item.getItem() instanceof FlintAndSteelItem) {
                        item.damage(1, player);
                    } else {
                        item.decrement(1);
                    }

                    // Spawn a TNT entity and set its fuse
                    TntEntity tntEntity = new TntEntity(player.getWorld(), player.getX(), player.getY() + 0.75, player.getZ(), player);
                    tntEntity.setFuse(40);

                    // Launch the TNT entity in the direction the player is facing
                    Vec3d forward = player.getRotationVec(1.5F);
                    tntEntity.setVelocity(forward);

                    player.getWorld().spawnEntity(tntEntity);
                    playTntThrowSound(player.getWorld(), player);

                    cir.setReturnValue(Optional.empty());
                    cir.cancel();

                    // Update the builder by removing one TNT item directly
                    builder.removeSelected();
                    itemStack.decrement(1);
                    builder.add(itemStack);
                    BundleContentsComponent updatedContents = builder.build();

                    // Reassign the modified contents back to the stack's data
                    stack.set(DataComponentTypes.BUNDLE_CONTENTS, updatedContents);
                }

                player.incrementStat(Stats.USED.getOrCreateStat(itemStack.getItem()));
            } else if (itemStack != null && itemStack.getItem() instanceof MinecartItem) {

                if (player.getWorld() instanceof ServerWorld) {

                    // Use the accessor to get the `type` field of MinecartItem
                    EntityType<? extends AbstractMinecartEntity> minecartType = ((MinecartItemAccessor) itemStack.getItem()).getType();

                    // Calculate an offset position slightly in front of the player
                    Vec3d forward = player.getRotationVec(1.5F).normalize(); // Direction player is facing
                    double offsetDistance = 1.0; // Adjust this distance to move the minecart further forward if needed
                    Vec3d offsetPos = player.getPos().add(forward.multiply(offsetDistance)).add(0, 0.75, 0); // Raise Y slightly to match previous spawn height

                    // Check if there is enough space at the offset position
                    if (player.getWorld().isSpaceEmpty(minecartType.create(player.getWorld(), SpawnReason.DISPENSER), new Box(offsetPos.x - 0.25, offsetPos.y, offsetPos.z - 0.25, offsetPos.x + 0.25, offsetPos.y + 0.5, offsetPos.z + 0.25))) {

                        // Spawn the minecart entity at the new offset position
                        AbstractMinecartEntity abstractMinecartEntity = AbstractMinecartEntity.create(
                                player.getWorld(),
                                offsetPos.x, offsetPos.y, offsetPos.z,
                                minecartType, SpawnReason.DISPENSER, itemStack, player
                        );

                        // Set the initial velocity in the player's facing direction
                        assert abstractMinecartEntity != null;
                        abstractMinecartEntity.setVelocity(forward);
                        player.getWorld().spawnEntity(abstractMinecartEntity);
                        playPotionThrowSound(player.getWorld(), player);
                        cir.setReturnValue(Optional.empty());
                        cir.cancel();

                        itemStack.decrement(1);
                        builder.add(itemStack);
                        BundleContentsComponent updatedContents = builder.build();
                        // Reassign the modified contents back to the stack's data
                        stack.set(DataComponentTypes.BUNDLE_CONTENTS, updatedContents);
                    } else {
                        // Notify the player if there's no space
                        player.sendMessage(Text.of("Not enough space to place the minecart."), true);

                        cir.setReturnValue(Optional.empty());
                        cir.cancel();
                    }
                }
                player.incrementStat(Stats.USED.getOrCreateStat(itemStack.getItem()));
            }
        }
    }

    /**
     * @author Hollowed
     * @reason funny
     */
    @Overwrite
    private boolean dropFirstBundledStack(ItemStack stack, PlayerEntity player) {
        boolean hasProjectingEnchantment = EnchantmentListener.hasEnchantment(stack, "antique:projecting");

        BundleContentsComponent bundleContentsComponent = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
        assert bundleContentsComponent != null;

        if (!bundleContentsComponent.isEmpty()) {
            Optional<ItemStack> optional = popFirstBundledStack(stack, player, bundleContentsComponent);
            if (optional.isPresent()) {
                ItemStack itemStack = optional.get();

                // Check if the item is a ThrowablePotionItem and if the bundle has the projecting enchantment
                if (itemStack.getItem() instanceof ThrowablePotionItem && hasProjectingEnchantment) {
                        handlePotionThrow(player, itemStack);
                } else {
                    player.dropItem(itemStack, true);
                }
                return true;
            } else {
                return false; // No item to process
            }
        } else {
            return false; // Bundle is empty
        }
    }

    // Custom method for handling throwable potions
    @Unique
    private void handlePotionThrow(PlayerEntity player, ItemStack itemStack) {
        if (player.getWorld() instanceof ServerWorld) {
            SplashPotionEntity potionEntity = new SplashPotionEntity(player.getWorld(), player, itemStack);
            potionEntity.setPosition(player.getPos().add(0, 1.5, 0));
            potionEntity.setVelocity(player.getRotationVector().add(0, 0.25, 0));
            player.getWorld().spawnEntity(potionEntity);
            playPotionThrowSound(player.getWorld(), player);
        }
        player.incrementStat(Stats.USED.getOrCreateStat(itemStack.getItem()));
    }

    // Sound method for dropping contents
    @Unique
    private static void playPotionThrowSound(World world, Entity entity) {
        world.playSound(null, entity.getBlockPos(), SoundEvents.ENTITY_WITCH_THROW, SoundCategory.PLAYERS, 0.8F, 1.0F);
    }

    @Unique
    private static void playTntThrowSound(World world, Entity entity) {
        world.playSound(null, entity.getBlockPos(), SoundEvents.ENTITY_WITCH_THROW, SoundCategory.PLAYERS, 0.8F, 1.0F);
        world.playSound(null, entity.getBlockPos(), SoundEvents.ITEM_BUNDLE_DROP_CONTENTS, SoundCategory.PLAYERS, 0.8F, 1.0F);
        world.playSound(null, entity.getBlockPos(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.8F, 1.0F);
    }

    @Inject(method = "usageTick", at = @At("HEAD"), cancellable = true)
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks, CallbackInfo ci) {
        if (stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof TntBlock) {
            ci.cancel();
        }
    }
}