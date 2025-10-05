package net.hollowed.antique.entities.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class PaleWardenHeldItemFeatureRenderer<S extends ArmedEntityRenderState, M extends EntityModel<S> & ModelWithArms<S>>
        extends FeatureRenderer<S, M> {

    public PaleWardenHeldItemFeatureRenderer(FeatureRendererContext<S, M> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, S state, float limbAngle, float limbDistance) {
//        this.renderItem(armedEntityRenderState.rightHandItemState, Arm.RIGHT, matrixStack, vertexConsumerProvider, light);
//        this.renderItem(armedEntityRenderState.leftHandItemState, Arm.LEFT, matrixStack, vertexConsumerProvider, light);
    }
//
//    protected void renderItem(ItemRenderState itemState, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
//        if (!itemState.isEmpty()) {
//            matrices.push();
//
//            // Translate to the pivot point
//            matrices.translate(0, 0.25, 0);
//
//            // Apply arm rotations
//            this.getContextModel().setArmAngle(arm, matrices);
//
//            // Apply additional rotations
//            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-100.0F));
//            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
//
//            // Translate back to original position
//            matrices.translate(0, -0.25, 0);
//            matrices.translate(-0.05, 0.1, 0.45);
//
//            // Apply final translation for the held item positioning
//            boolean isLeftArm = arm == Arm.LEFT;
//            matrices.translate((isLeftArm ? -1 : 1) / 16.0F, 0.125F, -0.625F);
//
//            if (isLeftArm) {
//                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(182.5F));
//                matrices.translate(0.135, -0.9, 0.535);
//            } else {
//                matrices.translate(-0.1, 0, 0.1);
//                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(6F));
//                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-1F));
//            }
//
//            // Render the item
//            itemState.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
//
//            matrices.pop();
//        }
//    }
}
