package net.hollowed.antique.mixin;

import net.hollowed.antique.util.BoatControllable;
import net.hollowed.combatamenities.util.interfaces.EntityFreezer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HappyGhastEntity.class)
public abstract class HappyGhastMixin extends AnimalEntity implements BoatControllable {

    @Shadow public abstract boolean method_72227();

    @Shadow public abstract @Nullable LivingEntity getControllingPassenger();

    @Unique
    @Nullable
    private Entity controller;

    @Unique
    @Nullable
    private Entity boat;

    protected HappyGhastMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "getControllingPassenger", at = @At("HEAD"), cancellable = true)
    public void passenger(CallbackInfoReturnable<LivingEntity> cir) {
        if (this.controller != null && this.controller instanceof PlayerEntity player && !this.method_72227()) {
            cir.setReturnValue(player);
        }
    }

    @Inject(method = "hasPlayerOnTop", at = @At("HEAD"), cancellable = true)
    public void standingOnBoat(CallbackInfoReturnable<Boolean> cir) {
        if (this.boat != null) {
            Box box = this.boat.getBoundingBox();
            Box box2 = new Box(box.minX - 1.0, box.maxY, box.minZ - 1.0, box.maxX + 1.0, box.maxY + box.getLengthY() * 1.5, box.maxZ + 1.0);

            for (PlayerEntity playerEntity : this.getWorld().getPlayers()) {
                if (!playerEntity.isSpectator()) {
                    Entity entity = playerEntity.getRootVehicle();
                    if (!(entity instanceof HappyGhastEntity) && box2.contains(entity.getPos())) {
                        if (this.boat instanceof EntityFreezer freezer) {
                            freezer.antiquities$setFrozen(true, 1);
                        }
                        cir.setReturnValue(true);
                    }
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (this.boat instanceof AbstractBoatEntity abstractBoat && (abstractBoat.getLeashData() == null || abstractBoat.getLeashData() != null && abstractBoat.getLeashData().leashHolder != this)) {
            this.boat = null;
        }
    }

    @Override
    public void antique$setEntity(@Nullable Entity entity) {
        this.controller = entity;
    }

    @Unique
    public void antique$setBoat(@Nullable Entity boat) {
        this.boat = boat;
    }
}
