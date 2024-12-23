package net.hollowed.antique.mixin;

import net.hollowed.antique.enchantments.EnchantmentListener;
import net.hollowed.antique.entities.renderer.ExtendedPlayerRenderState;
import net.hollowed.antique.items.custom.VelocityTransferMaceItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.PlayerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Arm;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerHeldItemFeatureRenderer.class)
public class PlayerHeldItemMixin<S extends PlayerEntityRenderState, M extends EntityModel<S> & ModelWithArms & ModelWithHead> extends HeldItemFeatureRenderer<S, M> {

    public PlayerHeldItemMixin(FeatureRendererContext<S, M> featureRendererContext) {
        super(featureRendererContext);
    }

    @Inject(method = "renderItem(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;Lnet/minecraft/client/render/item/ItemRenderState;Lnet/minecraft/util/Arm;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"), cancellable = true)
    private void spin(PlayerEntityRenderState playerEntityRenderState, ItemRenderState itemRenderState, Arm arm, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {

        if (playerEntityRenderState instanceof ExtendedPlayerRenderState renderState) {
            Entity entity = renderState.entity;
            if (entity instanceof PlayerEntity player && player.getActiveItem().getItem() instanceof VelocityTransferMaceItem) {
                int useTime = player.getItemUseTime();
                float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);

                ItemStack stack = player.getActiveItem();

                if (EnchantmentListener.hasCustomEnchantment(stack, "antique:kinematic")) {
                    // Limit multiplier to avoid excessive spinning
                    float multiplier = (useTime + tickDelta) * (useTime + tickDelta) / 5000000F;
                    float time = (player.getWorld().getTime() + tickDelta);
                    matrixStack.push();
                    matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-10));
                    matrixStack.translate(0, -0.2, 0.2);
                    matrixStack.translate(0, 0.6, -0.3);
                    if (multiplier <= 0.00125F) {
                        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(time * multiplier));
                    } else {
                        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(time * 110F + tickDelta));
                    }
                    matrixStack.translate(-0.2, -0.6, 0.3);
                    this.getContextModel().setArmAngle(arm, matrixStack);
                    matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
                    matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
                    boolean bl = arm == Arm.LEFT;
                    matrixStack.translate((float)(bl ? -1 : 1) / 16.0F, 0.125F, -0.625F);
                    itemRenderState.render(matrixStack, vertexConsumerProvider, i, 0);
                    ci.cancel();
                    matrixStack.pop();
                }
            }
        }
    }
}