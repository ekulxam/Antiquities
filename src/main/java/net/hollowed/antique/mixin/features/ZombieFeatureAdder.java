package net.hollowed.antique.mixin.features;

import net.hollowed.antique.client.armor.renderers.AdventureArmorFeatureRenderer;
import net.hollowed.antique.util.IsHuskGetter;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ZombieBaseEntityRenderer;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.ZombieEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieBaseEntityRenderer.class)
public abstract class ZombieFeatureAdder<T extends ZombieEntity, S extends ZombieEntityRenderState, M extends ZombieEntityModel<S>>
        extends BipedEntityRenderer<T, S, M> {

    public ZombieFeatureAdder(EntityRendererFactory.Context context, M model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(EntityRendererFactory.Context ctx, M mainModel, M babyMainModel, M armorInnerModel, M armorOuterModel, M babyArmorInnerModel, M babyArmorOuterModel, CallbackInfo ci) {
        this.addFeature(new AdventureArmorFeatureRenderer<>(this, ctx.getEntityModels(), false));
    }

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/mob/ZombieEntity;Lnet/minecraft/client/render/entity/state/ZombieEntityRenderState;F)V", at = @At("HEAD"))
    public void updateRenderState(T zombieEntity, S zombieEntityRenderState, float f, CallbackInfo ci) {
        if (zombieEntity instanceof HuskEntity) {
            if (zombieEntityRenderState instanceof IsHuskGetter access) {
                access.antiquities$setHusk(true);
            }
        }
    }
}
