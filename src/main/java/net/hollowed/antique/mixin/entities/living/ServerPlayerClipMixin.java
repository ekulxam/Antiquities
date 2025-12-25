package net.hollowed.antique.mixin.entities.living;

import net.hollowed.antique.index.AntiqueEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class ServerPlayerClipMixin {

    @Inject(method = "moveTowardsClosestSpace", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;"), cancellable = true)
    public void enableNoClip(CallbackInfo ci) {
        if ((Entity) (Object) this instanceof LivingEntity entity && entity.hasEffect(AntiqueEffects.ANIME_EFFECT)) {
            ci.cancel();
        }
    }
}
