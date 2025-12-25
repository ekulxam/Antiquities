package net.hollowed.antique.mixin.entities.living.boat_ghast;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class HappyGhastControllerMixin {

    @Shadow private @Nullable Entity vehicle;

    @ModifyReturnValue(method = {"getRootVehicle"}, at = @At("RETURN"))
    public Entity method(Entity original) {
        if ((Entity) (Object) this instanceof Player && this.vehicle instanceof AbstractBoat boat) {
            if (boat.getLeashData() != null && boat.getLeashData().leashHolder instanceof HappyGhast ghast) {
                return ghast;
            }
        }
        return original;
    }
}
