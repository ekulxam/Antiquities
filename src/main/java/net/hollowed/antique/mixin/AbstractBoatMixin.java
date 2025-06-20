package net.hollowed.antique.mixin;

import net.hollowed.antique.util.BoatControllable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBoatEntity.class)
public abstract class AbstractBoatMixin extends VehicleEntity {

    public AbstractBoatMixin(EntityType<?> entityType, World world) {
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
        }
    }
}
