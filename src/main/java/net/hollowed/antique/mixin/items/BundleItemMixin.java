package net.hollowed.antique.mixin.items;

import net.hollowed.antique.enchantments.EnchantmentListener;
import net.hollowed.antique.index.AntiqueEnchantments;
import net.hollowed.antique.items.SatchelItem;
import net.hollowed.antique.mixin.accessors.MinecartItemAccessor;
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
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
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

    @Inject(method = "onStackClicked", at = @At("RETURN"))
    public void onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (EnchantmentListener.hasEnchantment(stack, AntiqueEnchantments.CURSE_OF_VOIDING.getValue().toString())) {
            BundleContentsComponent bundleContentsComponent = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
            if (bundleContentsComponent == null) return;

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
        if (EnchantmentListener.hasEnchantment(stack, AntiqueEnchantments.CURSE_OF_VOIDING.getValue().toString())) {
            BundleContentsComponent bundleContentsComponent = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
            if (bundleContentsComponent == null) return;

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

    @Unique
    private void spawnXpOrb(PlayerEntity player, int xpAmount) {
        if (xpAmount > 0 && player.getEntityWorld() instanceof ServerWorld) {
            ExperienceOrbEntity xpOrb = new ExperienceOrbEntity(player.getEntityWorld(), player.getX(), player.getY(), player.getZ(), xpAmount);
            player.getEntityWorld().spawnEntity(xpOrb);
        }
    }

    @Inject(method = "popFirstBundledStack", at = @At("HEAD"), cancellable = true)
    private static void popFirstBundledStackInject(ItemStack stack, PlayerEntity player, BundleContentsComponent contents, CallbackInfoReturnable<Optional<ItemStack>> cir) {

        boolean hasProjectingEnchantment = EnchantmentListener.hasEnchantment(stack, AntiqueEnchantments.PROJECTING.getValue().toString());

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

            switch (itemStack.getItem()) {
                case BlockItem blockItem when blockItem.getBlock() instanceof TntBlock -> {
                    if (hasFlintAndSteel || player.isCreative()) {
                        itemStack.decrement(1);
                        builder.add(itemStack);
                        ItemStack item = isMainhand ? player.getMainHandStack() : player.getOffHandStack();

                        if (item.getItem() instanceof FlintAndSteelItem) {
                            item.damage(1, player);
                        } else {
                            item.decrement(1);
                        }

                        TntEntity tntEntity = new TntEntity(player.getEntityWorld(), player.getX(), player.getY() + 0.75, player.getZ(), player);
                        tntEntity.setFuse(40);

                        Vec3d forward = player.getRotationVec(1.5F);
                        tntEntity.setVelocity(forward);

                        player.getEntityWorld().spawnEntity(tntEntity);
                        player.getEntityWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_WITCH_THROW, SoundCategory.PLAYERS, 0.8F, 1.0F);
                        player.getEntityWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_BUNDLE_DROP_CONTENTS, SoundCategory.PLAYERS, 0.8F, 1.0F);
                        player.getEntityWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.8F, 1.0F);

                        player.incrementStat(Stats.USED.getOrCreateStat(itemStack.getItem()));
                        if (!player.isCreative()) {
                            stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
                        }
                    } else {
                        stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
                    }
                }
                case MinecartItem ignored -> {

                    boolean takeItem = true;

                    if (player.getEntityWorld() instanceof ServerWorld) {

                        EntityType<? extends AbstractMinecartEntity> minecartType = ((MinecartItemAccessor) itemStack.getItem()).getType();

                        Vec3d forward = player.getRotationVector().normalize();
                        double offsetDistance = 1.5;
                        Vec3d offsetPos = player.getEntityPos().add(forward.multiply(offsetDistance)).add(0, 1, 0);

                        if (player.getEntityWorld().isSpaceEmpty(minecartType.create(player.getEntityWorld(), SpawnReason.DISPENSER), new Box(offsetPos.x - 0.25, offsetPos.y, offsetPos.z - 0.25, offsetPos.x + 0.25, offsetPos.y + 0.5, offsetPos.z + 0.25))) {

                            AbstractMinecartEntity abstractMinecartEntity = AbstractMinecartEntity.create(
                                    player.getEntityWorld(),
                                    offsetPos.x, offsetPos.y, offsetPos.z,
                                    minecartType, SpawnReason.DISPENSER, itemStack, player
                            );

                            if (abstractMinecartEntity == null) return;
                            abstractMinecartEntity.setVelocity(forward.add(player.getVelocity()));
                            player.getEntityWorld().spawnEntity(abstractMinecartEntity);
                            player.getEntityWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_WITCH_THROW, SoundCategory.PLAYERS, 0.8F, 1.0F);
                            player.getEntityWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_BUNDLE_DROP_CONTENTS, SoundCategory.PLAYERS, 0.8F, 1.0F);

                            itemStack.decrement(1);
                            builder.add(itemStack);
                        } else {
                            takeItem = false;
                            player.sendMessage(Text.translatable("item.antique.bundle_minecart"), true);
                            cir.setReturnValue(Optional.empty());
                        }
                        player.incrementStat(Stats.USED.getOrCreateStat(itemStack.getItem()));
                        if (!player.isCreative() && takeItem) {
                            stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
                        }
                    }
                }
                case ThrowablePotionItem ignored -> {
                    if (!player.isCreative()) {
                        stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
                    }
                }
                case null, default -> stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
            }
        }
    }

    @Inject(method = "dropFirstBundledStack", at = @At("HEAD"), cancellable = true)
    private void projectingDropStack(ItemStack stack, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (EnchantmentListener.hasEnchantment(stack, AntiqueEnchantments.PROJECTING.getValue().toString())) {
            BundleContentsComponent bundleContentsComponent = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
            if (bundleContentsComponent != null && !bundleContentsComponent.isEmpty()) {
                Optional<ItemStack> optional = popFirstBundledStack(stack, player, bundleContentsComponent);
                if (optional.isPresent()) {
                    cir.setReturnValue(true);
                    if (optional.get().getItem() instanceof ThrowablePotionItem) {
                        handlePotionThrow(player, optional.get());
                    } else {
                        player.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8F, 0.8F + player.getEntityWorld().getRandom().nextFloat() * 0.4F);
                        ItemEntity entity = player.dropItem(optional.get(), true);
                        if (entity != null) {
                            entity.setVelocity(entity.getVelocity().multiply(2));
                            entity.velocityModified = true;
                        }
                    }
                } else {
                    cir.setReturnValue(false);
                }
            }
            cir.setReturnValue(false);
        }
    }

    @Unique
    private void handlePotionThrow(PlayerEntity player, ItemStack itemStack) {
        if (player.getEntityWorld() instanceof ServerWorld) {
            SplashPotionEntity potionEntity = new SplashPotionEntity(player.getEntityWorld(), player, itemStack);
            potionEntity.setPosition(player.getEntityPos().add(0, 1.5, 0));
            potionEntity.setVelocity(player.getRotationVector().add(0, 0.25, 0));
            player.getEntityWorld().spawnEntity(potionEntity);
            player.getEntityWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_WITCH_THROW, SoundCategory.PLAYERS, 0.8F, 1.0F);
            player.getEntityWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_BUNDLE_DROP_CONTENTS, SoundCategory.PLAYERS, 0.8F, 1.0F);
        }
        player.incrementStat(Stats.USED.getOrCreateStat(itemStack.getItem()));
    }
}