package net.hollowed.antique.mixin.items;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Objects;

@Mixin(BowItem.class)
public abstract class BowItemMixin extends RangedWeaponItem {

    public BowItemMixin(Settings settings) {
        super(settings);
    }

    @Override
    public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);
        if (entity instanceof PlayerEntity user) {
            ItemStack projectile = user.getProjectileType(stack);
            if (user.isInCreativeMode() || !projectile.isEmpty()) {
                stack.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.of(projectile));
                if (projectile.get(DataComponentTypes.POTION_CONTENTS) != null) {
                    stack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(Objects.requireNonNull(projectile.get(DataComponentTypes.POTION_CONTENTS)).getColor()));
                }
            }
        }
    }
}
