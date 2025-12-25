package net.hollowed.antique.mixin.entities.living;

import net.hollowed.antique.index.AntiqueItems;
import net.hollowed.antique.mixin.accessors.CanRemoveSaddleAccessor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityShearMixin {

    @Shadow public abstract boolean shearOffAllLeashConnections(@Nullable Player player);

    @Shadow protected abstract boolean attemptToShearEquipment(Player player, InteractionHand hand, ItemStack shears, Mob entity);

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void addShears(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(AntiqueItems.MYRIAD_PICK_HEAD) && this.shearOffAllLeashConnections(player)) {
            itemStack.hurtAndBreak(1, player, hand);
            cir.setReturnValue(InteractionResult.SUCCESS);
        } else {
            if ((Entity) (Object) this instanceof Mob mobEntity && mobEntity instanceof CanRemoveSaddleAccessor accessor) {
                if (itemStack.is(AntiqueItems.MYRIAD_PICK_HEAD) && accessor.canRemoveSaddle(player) && !player.isSecondaryUseActive() && this.attemptToShearEquipment(player, hand, itemStack, mobEntity)) {
                    cir.setReturnValue(InteractionResult.SUCCESS);
                }
            }
        }
    }
}
