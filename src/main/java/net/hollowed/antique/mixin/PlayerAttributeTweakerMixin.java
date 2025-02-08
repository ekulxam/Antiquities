package net.hollowed.antique.mixin;

import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerAttributeTweakerMixin {
    @Inject(method = "createPlayerAttributes", at = @At("RETURN"), cancellable = true)
    private static void addSafeFallDistanceAttribute(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        cir.setReturnValue(cir.getReturnValue()
                .add(EntityAttributes.SAFE_FALL_DISTANCE, 15)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.1075)
        );
    }
}
