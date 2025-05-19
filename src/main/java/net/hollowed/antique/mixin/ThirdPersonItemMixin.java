package net.hollowed.antique.mixin;

import net.hollowed.antique.enchantments.EnchantmentListener;
import net.hollowed.antique.items.custom.VelocityTransferMaceItem;
import net.hollowed.combatamenities.util.interfaces.PlayerEntityRenderStateAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.PlayerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerHeldItemFeatureRenderer.class)
public class ThirdPersonItemMixin<S extends PlayerEntityRenderState, M extends EntityModel<S> & ModelWithArms & ModelWithHead> extends HeldItemFeatureRenderer<S, M> {

    public ThirdPersonItemMixin(FeatureRendererContext<S, M> featureRendererContext) {
        super(featureRendererContext);
    }

    @Inject(method = "renderItem(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;Lnet/minecraft/client/render/item/ItemRenderState;Lnet/minecraft/util/Arm;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"), cancellable = true)
    private void spin(PlayerEntityRenderState playerEntityRenderState, ItemRenderState itemRenderState, Arm arm, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {

        if (playerEntityRenderState instanceof PlayerEntityRenderStateAccess) {
            Entity entity = ((PlayerEntityRenderStateAccess) playerEntityRenderState).combat_Amenities$getPlayerEntity();
            if (entity instanceof PlayerEntity player && player.getActiveItem().getItem() instanceof VelocityTransferMaceItem) {
                int useTime = player.getItemUseTime();
                float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(true);

                ItemStack stack = player.getActiveItem();

                if (EnchantmentListener.hasEnchantment(stack, "antique:kinematic")) {
                    // Define maximum angular velocity and an acceleration factor
                    final float totalRotation = getTotalRotation(useTime, tickDelta);

                    matrixStack.push();
                    matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-10));
                    matrixStack.translate(0, -0.2, 0.2);
                    matrixStack.translate(0, 0.6, -0.3);

                    // Smooth rotation
                    matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(totalRotation));

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

    @Unique
    private static float getTotalRotation(int useTime, float tickDelta) {
        float maxAngularVelocity = 120.0F; // degrees per tick
        float accelerationFactor = 0.004F; // Adjust to control smooth acceleration

        // Calculate eased angular velocity
        float progress = Math.min(1.0F, (useTime + tickDelta) * accelerationFactor); // Clamped to 1.0
        float easedVelocity = maxAngularVelocity * (1 - (float) Math.pow(1 - progress, 3)); // Smooth transition

        // Compute total rotation
        return (useTime + tickDelta) * easedVelocity;
    }
}