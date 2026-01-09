package net.hollowed.antique.mixin.entities.living.boat_ghast;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Strider.class)
public abstract class StriderMixin extends Animal {

    protected StriderMixin(EntityType<? extends Animal> entityType, Level world) {
        super(entityType, world);
    }

    @SuppressWarnings("all")
    @Inject(method = "getControllingPassenger", at = @At("HEAD"), cancellable = true)
    public void passenger(CallbackInfoReturnable<LivingEntity> cir) {
        if (this.getLeashData() != null && this.getLeashData().leashHolder != null) {
            cir.setReturnValue(null);
        }
    }
}
