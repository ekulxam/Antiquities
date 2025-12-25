package net.hollowed.antique.mixin.entities.states;

import net.hollowed.antique.util.interfaces.duck.BipedEntityRenderStateAccess;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidMobRenderer.class)
public class BipedEntityRendererMixin {

    @Inject(method = "extractHumanoidRenderState", at = @At("HEAD"))
    private static void updateBipedRenderState(LivingEntity entity, HumanoidRenderState state, float tickDelta, ItemModelResolver itemModelResolver, CallbackInfo ci) {
        if (state instanceof BipedEntityRenderStateAccess access) {
            access.antique$setEntity(entity);
        }
    }
}
