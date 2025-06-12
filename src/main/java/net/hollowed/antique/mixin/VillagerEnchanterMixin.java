package net.hollowed.antique.mixin;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class VillagerEnchanterMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>
        extends EntityRenderer<T, S>
        implements FeatureRendererContext<S, M> {

    @Shadow public abstract M getModel();

    @Shadow protected abstract boolean isVisible(S state);

    @Shadow protected abstract @Nullable RenderLayer getRenderLayer(S state, boolean showBody, boolean translucent, boolean showOutline);

    @Unique
    private LivingEntityRenderState livingEntityRenderState = new LivingEntityRenderState();

    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    public void setLivingEntityRenderState(S livingEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        this.livingEntityRenderState = livingEntityRenderState;
    }

    protected VillagerEnchanterMixin(EntityRendererFactory.Context context) {
        super(context);
    }

//    @Redirect(
//            method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;"
//            )
//    )
//    private VertexConsumer redirectGetBuffer(VertexConsumerProvider provider, RenderLayer layer) {
//        boolean bl = this.isVisible((S) livingEntityRenderState);
//        boolean bl2 = !bl && !livingEntityRenderState.invisibleToPlayer;
//        RenderLayer renderLayer = this.getRenderLayer((S) livingEntityRenderState, bl, bl2, livingEntityRenderState.hasOutline);
//        //return VertexConsumers.union(provider.getBuffer(RenderLayer.getEntityGlint()), provider.getBuffer(renderLayer));
//        return provider.getBuffer(renderLayer);
//    }
}
