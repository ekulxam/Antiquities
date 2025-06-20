package net.hollowed.antique.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemSteerable;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PigEntity.class)
public abstract class PigMixin extends AnimalEntity {

    protected PigMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "getControllingPassenger", at = @At("HEAD"), cancellable = true)
    public void passenger(CallbackInfoReturnable<LivingEntity> cir) {
        if (this.getLeashData() != null && this.getLeashData().leashHolder != null) {
            cir.setReturnValue(null);
        }
    }
}
