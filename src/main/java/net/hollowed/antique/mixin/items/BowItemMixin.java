package net.hollowed.antique.mixin.items;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Objects;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.DyedItemColor;

@Mixin(BowItem.class)
public abstract class BowItemMixin extends ProjectileWeaponItem {

    public BowItemMixin(Properties settings) {
        super(settings);
    }

    @Override
    public boolean allowComponentsUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);
        if (entity instanceof Player user) {
            ItemStack projectile = user.getProjectile(stack);
            if (user.hasInfiniteMaterials() || !projectile.isEmpty()) {
                stack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(projectile));
                if (projectile.get(DataComponents.POTION_CONTENTS) != null) {
                    stack.set(DataComponents.DYED_COLOR, new DyedItemColor(Objects.requireNonNull(projectile.get(DataComponents.POTION_CONTENTS)).getColor()));
                }
            }
        }
    }
}
