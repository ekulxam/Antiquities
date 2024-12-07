package net.hollowed.antique.mixin;

import net.hollowed.antique.items.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class FurBootsSoulSandMixin extends LivingEntity {

    protected FurBootsSoulSandMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "slowMovement", // This method applies slowness effects from blocks
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventSlowness(BlockState state, Vec3d multiplier, CallbackInfo ci) {
        ItemStack boots = this.getEquippedStack(EquipmentSlot.FEET);

        // Check if the player is wearing fur boots
        if (boots.isOf(ModItems.FUR_BOOTS)) {
            ci.cancel(); // Cancel the slowness application
        }
    }
}
