package net.hollowed.antique.entities.renderer;

import net.hollowed.antique.entities.custom.IllusionerArrowEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.RotationAxis;

public class IllusionerArrowEntityRenderer extends EntityRenderer<IllusionerArrowEntity, IllusionerArrowRenderState> {

    public IllusionerArrowEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(IllusionerArrowRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(state, matrices, vertexConsumers, light);
        MinecraftClient client = MinecraftClient.getInstance();
        ItemRenderer itemRenderer = client.getItemRenderer();

        float multiplier = 0.25F;

        // Apply translation
        matrices.translate(state.entity.getRotationVector().multiply(multiplier, multiplier, -multiplier));

        // Apply rotations for the projectile's orientation
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(state.entity.getYaw() - 90.0F));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(state.entity.getPitch() - 45.0F));

        ItemStack stack = Items.FIREWORK_ROCKET.getDefaultStack();

        itemRenderer.renderItem(stack, ItemDisplayContext.NONE, light, 0, matrices, vertexConsumers, state.entity.getWorld(), 0);
    }

    @Override
    public IllusionerArrowRenderState createRenderState() {
        return new IllusionerArrowRenderState();
    }

    @Override
    public void updateRenderState(IllusionerArrowEntity entity, IllusionerArrowRenderState state, float tickProgress) {
        super.updateRenderState(entity, state, tickProgress);
        state.entity = entity;
    }
}
