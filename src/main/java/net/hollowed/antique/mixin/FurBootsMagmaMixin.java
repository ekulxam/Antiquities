package net.hollowed.antique.mixin;

import net.hollowed.antique.items.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.MagmaBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MagmaBlock.class)
public abstract class FurBootsMagmaMixin {

    @Inject(
        method = "onSteppedOn",
        at = @At("HEAD"),
            cancellable = true)
    private void preventMagmaDamage(World world, BlockPos pos, BlockState state, Entity entity, CallbackInfo ci) {
        if (entity instanceof LivingEntity livingEntity) {
            ItemStack boots = livingEntity.getEquippedStack(EquipmentSlot.FEET);
            if (boots.isOf(ModItems.FUR_BOOTS)) {
                ci.cancel();
            }
        }
    }
}
