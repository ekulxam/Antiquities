package net.hollowed.antique.mixin.items;

import net.hollowed.antique.enchantments.EnchantmentListener;
import net.hollowed.antique.items.SatchelItem;
import net.hollowed.antique.mixin.accessors.MinecartItemAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.TntBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.SplashPotionEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
        if (itemStack.getItem() instanceof SatchelItem) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "onClicked", at = @At("HEAD"), cancellable = true)
    private void preventSatchelsOnClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, CallbackInfoReturnable<Boolean> cir) {
        if (otherStack.getItem() instanceof SatchelItem) {
            cir.setReturnValue(false);
        }
    }


    @Shadow
    private static Optional<ItemStack> popFirstBundledStack(ItemStack stack, PlayerEntity player, BundleContentsComponent contents) {
        return Optional.empty();
    }

    @Unique
    public boolean placeBlockFromBundle(ItemUsageContext context, ItemStack stack) {
        BlockItem blockItem = (BlockItem) stack.getItem();
        ItemPlacementContext placementContext = new ItemPlacementContext(context);
        if (!placementContext.getWorld().isClient()) {
            ActionResult result = blockItem.place(placementContext);
            BlockPos pos = placementContext.getBlockPos();
            BlockState state = placementContext.getWorld().getBlockState(pos);
            BlockSoundGroup sound = state.getSoundGroup();

            if (result.isAccepted()) {
                placementContext.getWorld().playSound(null, pos, sound.getPlaceSound(), SoundCategory.BLOCKS,
                    (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
                Objects.requireNonNull(context.getPlayer()).swingHand(context.getHand(), true);
                return false;
            }
        }
        return true;
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (EnchantmentListener.hasEnchantment(user.getStackInHand(hand), "antique:jumbling")) {
            cir.setReturnValue(ActionResult.FAIL);
        }
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        Hand hand = context.getHand();
        PlayerEntity player = context.getPlayer();
        ItemStack stack = Objects.requireNonNull(player).getStackInHand(hand);

        if (EnchantmentListener.hasEnchantment(stack, "antique:jumbling")) {

            BundleContentsComponent bundleContentsComponent = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
            if (bundleContentsComponent != null) {
                List<ItemStack> blockItems = bundleContentsComponent.stream()
                        .filter(itemStack -> itemStack.getItem() instanceof BlockItem)
                        .toList();

                if (!blockItems.isEmpty()) {
                    if (!bundleContentsComponent.isEmpty()) {
                        BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(bundleContentsComponent);
                        boolean hasNonBlockItem = bundleContentsComponent.stream()
                                .anyMatch(itemStack -> !(itemStack.getItem() instanceof BlockItem));
                        if (hasNonBlockItem) {
                            return ActionResult.PASS;
                        }

                        int randomIndex = new Random().nextInt(blockItems.size());

                        builder.setSelectedStackIndex(randomIndex);
                        ItemStack selectedBlockStack = builder.removeSelected();
                        if (selectedBlockStack != null) {
                            boolean canPlace = placeBlockFromBundle(context, selectedBlockStack);
                            if (canPlace) {
                                return ActionResult.FAIL;
                            }
                            if (!player.isCreative()) {
                                selectedBlockStack.decrement(1);
                            }
                            builder.add(selectedBlockStack);
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
            BundleContentsComponent bundleContentsComponent = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
            assert bundleContentsComponent != null;

            BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(bundleContentsComponent);
            ItemStack itemStack = builder.removeSelected();

            if (itemStack != null) {
                float xpToGrant = calculateXpFromStackSize(itemStack.getCount(), itemStack) / 2;
                if (xpToGrant > 0) {
                    spawnXpOrb(player, (int) xpToGrant + 1);
                }
            }

            builder.removeSelected();
            stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
        }
    }

    @Inject(method = "onClicked", at = @At("RETURN"))
    public void onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, CallbackInfoReturnable<Boolean> cir) {
        if (EnchantmentListener.hasEnchantment(stack, "antique:curse_of_voiding")) {
            BundleContentsComponent bundleContentsComponent = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
            assert bundleContentsComponent != null;

            BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(bundleContentsComponent);
            ItemStack itemStack = builder.removeSelected();

            if (itemStack != null) {
                float xpToGrant = calculateXpFromStackSize(itemStack.getCount(), itemStack) / 2;
                if (xpToGrant > 0) {
                    spawnXpOrb(player, (int) xpToGrant + 1);
                }
            }

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
            case Rarity.EPIC -> xp *= 512;
            case Rarity.RARE -> xp *= 256;
            case Rarity.UNCOMMON -> xp *= 128;
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
            ExperienceOrbEntity xpOrb = new ExperienceOrbEntity(player.getWorld(), player.getX(), player.getY(), player.getZ(), xpAmount);
            player.getWorld().spawnEntity(xpOrb);
        }
    }

    @Inject(method = "popFirstBundledStack", at = @At("HEAD"), cancellable = true)
    private static void popFirstBundledStackInject(ItemStack stack, PlayerEntity player, BundleContentsComponent contents, CallbackInfoReturnable<Optional<ItemStack>> cir) {

        boolean hasProjectingEnchantment = EnchantmentListener.hasEnchantment(stack, "antique:projecting");

        BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(contents);
        ItemStack itemStack = builder.removeSelected();

        if (hasProjectingEnchantment && itemStack != null) {
            cir.setReturnValue(Optional.of(itemStack));

            boolean hasFlintAndSteel = player.getMainHandStack().getItem() instanceof FlintAndSteelItem ||
                    player.getOffHandStack().getItem() instanceof FlintAndSteelItem ||
                    player.getMainHandStack().getItem() instanceof FireChargeItem ||
                    player.getOffHandStack().getItem() instanceof FireChargeItem;

            boolean isMainhand = player.getMainHandStack().getItem() instanceof FlintAndSteelItem ||
                    player.getMainHandStack().getItem() instanceof FireChargeItem;

            if (itemStack.getItem() instanceof BlockItem && ((BlockItem) itemStack.getItem()).getBlock() instanceof TntBlock && player.getWorld() instanceof ServerWorld) {
                if (hasFlintAndSteel || player.isCreative()) {
                    itemStack.decrement(1);
                    builder.add(itemStack);
                    ItemStack item = isMainhand ? player.getMainHandStack() : player.getOffHandStack();

                    if (item.getItem() instanceof FlintAndSteelItem) {
                        item.damage(1, player);
                    } else {
                        item.decrement(1);
                    }

                    TntEntity tntEntity = new TntEntity(player.getWorld(), player.getX(), player.getY() + 0.75, player.getZ(), player);
                    tntEntity.setFuse(40);

                    Vec3d forward = player.getRotationVec(1.5F);
                    tntEntity.setVelocity(forward);

                    player.getWorld().spawnEntity(tntEntity);
                    playTntThrowSound(player.getWorld(), player);

                    player.incrementStat(Stats.USED.getOrCreateStat(itemStack.getItem()));
                    stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
                }
            } else if (itemStack.getItem() instanceof MinecartItem) {

                if (player.getWorld() instanceof ServerWorld) {

                    EntityType<? extends AbstractMinecartEntity> minecartType = ((MinecartItemAccessor) itemStack.getItem()).getType();

                    Vec3d forward = player.getRotationVector().normalize();
                    double offsetDistance = 1.5;
                    Vec3d offsetPos = player.getPos().add(forward.multiply(offsetDistance)).add(0, 1, 0);

                    if (player.getWorld().isSpaceEmpty(minecartType.create(player.getWorld(), SpawnReason.DISPENSER), new Box(offsetPos.x - 0.25, offsetPos.y, offsetPos.z - 0.25, offsetPos.x + 0.25, offsetPos.y + 0.5, offsetPos.z + 0.25))) {

                        AbstractMinecartEntity abstractMinecartEntity = AbstractMinecartEntity.create(
                                player.getWorld(),
                                offsetPos.x, offsetPos.y, offsetPos.z,
                                minecartType, SpawnReason.DISPENSER, itemStack, player
                        );

                        assert abstractMinecartEntity != null;
                        abstractMinecartEntity.setVelocity(forward.add(player.getVelocity()));
                        player.getWorld().spawnEntity(abstractMinecartEntity);
                        playPotionThrowSound(player.getWorld(), player);

                        itemStack.decrement(1);
                        builder.add(itemStack);
                    } else {
                        player.sendMessage(Text.of("Not enough space to place the minecart."), true);
                        stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
                    }
                }
                player.incrementStat(Stats.USED.getOrCreateStat(itemStack.getItem()));
                if (!player.isCreative()) {
                    stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
                }
            } else {
                stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
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

                if (itemStack.getItem() instanceof ThrowablePotionItem && hasProjectingEnchantment) {
                        handlePotionThrow(player, itemStack);
                } else if (hasProjectingEnchantment) {
                    ItemEntity entity = player.dropItem(itemStack, true);
                    if (entity != null) {
                        entity.setVelocity(entity.getVelocity().multiply(2));
                        entity.velocityModified = true;
                    }
                } else {
                    player.dropItem(itemStack, true);
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

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

    @Unique
    private static void playPotionThrowSound(World world, Entity entity) {
        world.playSound(null, entity.getBlockPos(), SoundEvents.ENTITY_WITCH_THROW, SoundCategory.PLAYERS, 0.8F, 1.0F);
        world.playSound(null, entity.getBlockPos(), SoundEvents.ITEM_BUNDLE_DROP_CONTENTS, SoundCategory.PLAYERS, 0.8F, 1.0F);
    }

    @Unique
    private static void playTntThrowSound(World world, Entity entity) {
        world.playSound(null, entity.getBlockPos(), SoundEvents.ENTITY_WITCH_THROW, SoundCategory.PLAYERS, 0.8F, 1.0F);
        world.playSound(null, entity.getBlockPos(), SoundEvents.ITEM_BUNDLE_DROP_CONTENTS, SoundCategory.PLAYERS, 0.8F, 1.0F);
        world.playSound(null, entity.getBlockPos(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.8F, 1.0F);
    }
}