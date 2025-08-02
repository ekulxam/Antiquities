package net.hollowed.antique.mixin.entities.living;

import net.hollowed.antique.index.AntiqueItems;
import net.hollowed.antique.mixin.accessors.CanRemoveSaddleAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityShearMixin {

    @Shadow public abstract boolean snipAllHeldLeashes(@Nullable PlayerEntity player);

    @Shadow protected abstract boolean shearEquipment(PlayerEntity player, Hand hand, ItemStack shears, MobEntity entity);

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void addShears(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isOf(AntiqueItems.MYRIAD_PICK_HEAD) && this.snipAllHeldLeashes(player)) {
            itemStack.damage(1, player, hand);
            cir.setReturnValue(ActionResult.SUCCESS);
        } else {
            if ((Entity) (Object) this instanceof MobEntity mobEntity && mobEntity instanceof CanRemoveSaddleAccessor accessor) {
                if (itemStack.isOf(AntiqueItems.MYRIAD_PICK_HEAD) && accessor.canRemoveSaddle(player) && !player.shouldCancelInteraction() && this.shearEquipment(player, hand, itemStack, mobEntity)) {
                    cir.setReturnValue(ActionResult.SUCCESS);
                }
            }
        }
    }
}
