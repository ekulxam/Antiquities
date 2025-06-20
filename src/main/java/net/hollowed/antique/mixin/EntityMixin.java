package net.hollowed.antique.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "getRootVehicle", at = @At("HEAD"), cancellable = true)
    private void happyGhastVehicle(CallbackInfoReturnable<Entity> cir) {
        if (!((Entity) (Object) this instanceof PlayerEntity player)) {
            return;
        }
        Entity hg = antique$getRealVehicle(player);
        if (hg != null) {
            while (hg.hasVehicle()) {
                hg = hg.getVehicle();
            }

            cir.setReturnValue(hg);
        }
    }

    @Unique
    @Nullable
    private static HappyGhastEntity antique$getRealVehicle(PlayerEntity player) {;
        if (player == null) {
            return null;
        }
        if (!(player.getVehicle() instanceof BoatEntity boatEntity)) {
            return null;
        }
        Leashable.LeashData leashData = boatEntity.getLeashData();
        if (leashData == null) {
            return null;
        }
        return leashData.leashHolder instanceof HappyGhastEntity happyGhast ? happyGhast : null;
    }
}
