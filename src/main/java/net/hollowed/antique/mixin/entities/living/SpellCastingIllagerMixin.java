package net.hollowed.antique.mixin.entities.living;

import net.hollowed.antique.entities.IllusionerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpellcastingIllagerEntity.class)
public abstract class SpellCastingIllagerMixin extends IllagerEntity {

    protected SpellCastingIllagerMixin(EntityType<? extends IllagerEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/SpellcastingIllagerEntity;isSpellcasting()Z"), cancellable = true)
    private void tick(CallbackInfo ci) {
        if ((SpellcastingIllagerEntity) (Object) this instanceof IllusionerEntity) {
            ci.cancel();
        }
    }
}
