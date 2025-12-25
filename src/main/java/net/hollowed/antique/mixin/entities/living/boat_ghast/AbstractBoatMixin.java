package net.hollowed.antique.mixin.entities.living.boat_ghast;

import net.hollowed.antique.util.interfaces.duck.BoatControllable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBoat.class)
public abstract class AbstractBoatMixin extends VehicleEntity {

    public AbstractBoatMixin(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Shadow @Nullable public abstract Leashable.LeashData getLeashData();

    @Inject(method = "getControllingPassenger", at = @At("HEAD"), cancellable = true)
    public void passenger(CallbackInfoReturnable<LivingEntity> cir) {
        if (this.getLeashData() != null && this.getLeashData().leashHolder != null) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void passenger(CallbackInfo ci) {
        if (this.getLeashData() != null && this.getLeashData().leashHolder instanceof BoatControllable controllable) {
            controllable.antique$setEntity(this.getFirstPassenger());
            controllable.antique$setBoat(this);
        }
    }
}
