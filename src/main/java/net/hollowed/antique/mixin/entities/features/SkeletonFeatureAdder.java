package net.hollowed.antique.mixin.entities.features;

import net.hollowed.antique.util.interfaces.duck.IsWitherGetter;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.client.render.entity.state.SkeletonEntityRenderState;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSkeletonEntityRenderer.class)
public abstract class SkeletonFeatureAdder<T extends AbstractSkeletonEntity, S extends SkeletonEntityRenderState>
        extends BipedEntityRenderer<T, S, SkeletonEntityModel<S>> {

    public SkeletonFeatureAdder(EntityRendererFactory.Context context, SkeletonEntityModel<S> model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/mob/AbstractSkeletonEntity;Lnet/minecraft/client/render/entity/state/SkeletonEntityRenderState;F)V", at = @At("HEAD"))
    public void updateRenderState(T abstractSkeletonEntity, S skeletonEntityRenderState, float f, CallbackInfo ci) {
        if (abstractSkeletonEntity instanceof WitherSkeletonEntity) {
            if (skeletonEntityRenderState instanceof IsWitherGetter access) {
                access.antiquities$setWither(true);
            }
        }
    }
}
