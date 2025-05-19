package net.hollowed.antique.util;

import net.hollowed.antique.Antiquities;
import net.hollowed.antique.entities.renderer.IllusionerEntityRenderState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class IllusionerRenderUtil {

    public static void renderIllusioner(IllusionerEntityRenderState illusionerEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        if (illusionerEntityRenderState.invisible) {
            matrixStack.push();
            matrixStack.translate(0, 1.4, 0);
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-(illusionerEntityRenderState.relativeHeadYaw + illusionerEntityRenderState.bodyYaw)));
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(illusionerEntityRenderState.pitch));
            ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
            ItemStack stack = Items.APPLE.getDefaultStack();
            stack.set(DataComponentTypes.ITEM_MODEL, Identifier.of(Antiquities.MOD_ID, "illusioner_idol"));
            itemRenderer.renderItem(stack, ItemDisplayContext.NONE, i, 0, matrixStack, vertexConsumerProvider, MinecraftClient.getInstance().world, 0);
            matrixStack.pop();
        }
    }
}
