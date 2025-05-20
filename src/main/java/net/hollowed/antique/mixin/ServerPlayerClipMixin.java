package net.hollowed.antique.mixin;

import net.hollowed.antique.Antiquities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class ServerPlayerClipMixin {

    @Inject(method = "pushOutOfBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;ofFloored(DDD)Lnet/minecraft/util/math/BlockPos;"), cancellable = true)
    public void enableNoClip(CallbackInfo ci) {
        if ((Entity) (Object) this instanceof LivingEntity entity && entity.hasStatusEffect(Antiquities.ANIME_EFFECT)) {
            ci.cancel();
        }
    }
}
