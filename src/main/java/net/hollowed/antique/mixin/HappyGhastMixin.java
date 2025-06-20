package net.hollowed.antique.mixin;

import net.hollowed.antique.util.BoatControllable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HappyGhastEntity.class)
public abstract class HappyGhastMixin extends AnimalEntity implements BoatControllable {

    @Shadow public abstract boolean method_72227();

    @Unique
    @Nullable
    private Entity controller;

    protected HappyGhastMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "getControllingPassenger", at = @At("HEAD"), cancellable = true)
    public void passenger(CallbackInfoReturnable<LivingEntity> cir) {
        if (this.controller != null && this.controller instanceof PlayerEntity player && !this.method_72227()) {
            cir.setReturnValue(player);
        }
    }

    @Override
    public void antique$setEntity(Entity entity) {
        this.controller = entity;
    }
}
