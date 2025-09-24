package net.hollowed.antique.mixin.entities.living.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Predicate;

@Mixin(PlayerEntity.class)
public class ProjectileFixMixin {

    @ModifyVariable(method = "getProjectileType", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/RangedWeaponItem;getProjectiles()Ljava/util/function/Predicate;"))
    private Predicate<ItemStack> fixTheDamnCrossbowsPlease(Predicate<ItemStack> value, ItemStack stack) {
        return ((RangedWeaponItem)stack.getItem()).getHeldProjectiles();
    }
}
