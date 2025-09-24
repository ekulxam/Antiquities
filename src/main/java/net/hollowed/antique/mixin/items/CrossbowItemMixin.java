package net.hollowed.antique.mixin.items;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Objects;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin extends RangedWeaponItem {

    public CrossbowItemMixin(Settings settings) {
        super(settings);
    }

    @Override
    public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);
        if (entity instanceof PlayerEntity user && slot != null) {
            ItemStack projectile = user.getProjectileType(stack);
            if (user.isInCreativeMode() || !projectile.isEmpty()) {
                if (projectile.get(DataComponentTypes.POTION_CONTENTS) != null) {
                    stack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(Objects.requireNonNull(projectile.get(DataComponentTypes.POTION_CONTENTS)).getColor()));
                }
            }
        }
    }
}
