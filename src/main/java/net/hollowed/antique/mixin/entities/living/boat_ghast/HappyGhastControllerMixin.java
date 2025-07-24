package net.hollowed.antique.mixin.entities.living.boat_ghast;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class HappyGhastControllerMixin {

    @Shadow private @Nullable Entity vehicle;

    @ModifyReturnValue(method = {"getRootVehicle"}, at = @At("RETURN"))
    public Entity method(Entity original) {
        if ((Entity) (Object) this instanceof PlayerEntity && this.vehicle instanceof AbstractBoatEntity boat) {
            if (boat.getLeashData() != null && boat.getLeashData().leashHolder instanceof HappyGhastEntity ghast) {
                return ghast;
            }
        }
        return original;
    }
}
