package net.hollowed.antique.mixin.entities.living.boat_ghast;

import net.hollowed.antique.util.interfaces.duck.BoatControllable;
import net.hollowed.combatamenities.util.interfaces.EntityFreezer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HappyGhast.class)
public abstract class HappyGhastMixin extends Animal implements BoatControllable {

    @Shadow public abstract @Nullable LivingEntity getControllingPassenger();

    @Unique
    @Nullable
    private Entity controller;

    @Unique
    @Nullable
    private Entity boat;

    protected HappyGhastMixin(EntityType<? extends Animal> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "getControllingPassenger", at = @At("HEAD"), cancellable = true)
    public void passenger(CallbackInfoReturnable<LivingEntity> cir) {
        if (this.controller != null && this.controller instanceof Player player) {
            cir.setReturnValue(player);
        }
    }

    @Inject(method = "scanPlayerAboveGhast", at = @At("HEAD"), cancellable = true)
    public void standingOnBoat(CallbackInfoReturnable<Boolean> cir) {
        if (this.boat != null) {
            AABB box = this.boat.getBoundingBox();
            AABB box2 = new AABB(box.minX - 1.0, box.maxY, box.minZ - 1.0, box.maxX + 1.0, box.maxY + box.getYsize() * 1.5, box.maxZ + 1.0);

            for (Player playerEntity : this.level().players()) {
                if (!playerEntity.isSpectator()) {
                    Entity entity = playerEntity.getRootVehicle();
                    if (!(entity instanceof HappyGhast) && box2.contains(entity.position())) {
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
        if (this.boat instanceof AbstractBoat abstractBoat) {
            if (abstractBoat.getLeashData().leashHolder != this) {
                this.boat = null;
            }
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
