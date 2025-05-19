package net.hollowed.antique.util;

import net.hollowed.antique.component.ModComponents;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class Temp {

    public static void method1(ItemRenderer itemRenderer, LivingEntity living, Arm arm, int light, MatrixStack matrices, VertexConsumerProvider vertexConsumers, MinecraftClient client) {
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-10));
        matrices.translate(0, 0.955, 0.035);
        matrices.scale(0.55F, 0.55F, 0.55F);
        ItemStack stackToRender = living.getStackInArm(arm).getOrDefault(ModComponents.MYRIAD_STACK, ItemStack.EMPTY);

        MyriadStaffTransformData data = MyriadStaffTransformResourceReloadListener.getTransform(Registries.ITEM.getId(stackToRender.getItem()));
        matrices.scale(data.scale().get(0), data.scale().get(1), data.scale().get(2));
        matrices.translate(data.translation().get(0), data.translation().get(1), data.translation().get(2));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(data.rotation().getFirst()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(data.rotation().get(1)));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(data.rotation().get(2)));

        Identifier customModel = stackToRender.getOrDefault(DataComponentTypes.ITEM_MODEL, Registries.ITEM.getId(stackToRender.getItem()));
        stackToRender.set(DataComponentTypes.ITEM_MODEL, data.model());

        if (stackToRender.isOf(Blocks.CONDUIT.asItem())) {

        }

        itemRenderer.renderItem(
                stackToRender,
                ItemDisplayContext.NONE,
                light,
                OverlayTexture.DEFAULT_UV,
                matrices,
                vertexConsumers,
                client.world,
                1
        );

        stackToRender.set(DataComponentTypes.ITEM_MODEL, customModel);
    }

    public static void method2(ItemRenderer itemRenderer, ItemStack stack, int light, MatrixStack matrices, VertexConsumerProvider vertexConsumers, MinecraftClient client, ItemDisplayContext context) {
        if (!context.equals(ItemDisplayContext.NONE)) {
            matrices.translate(0.25, 0.5, 0.025);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(20));
            matrices.scale(0.45F, 0.45F, 0.45F);

            ItemStack stackToRender = stack.getOrDefault(ModComponents.MYRIAD_STACK, ItemStack.EMPTY);

            MyriadStaffTransformData data = MyriadStaffTransformResourceReloadListener.getTransform(Registries.ITEM.getId(stackToRender.getItem()));
            matrices.scale(data.scale().get(0), data.scale().get(1), data.scale().get(2));
            matrices.translate(data.translation().get(0), data.translation().get(1), data.translation().get(2));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(data.rotation().getFirst()));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(data.rotation().get(1)));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(data.rotation().get(2)));

            Identifier customModel = stackToRender.getOrDefault(DataComponentTypes.ITEM_MODEL, Registries.ITEM.getId(stackToRender.getItem()));
            stackToRender.set(DataComponentTypes.ITEM_MODEL, data.model());

            itemRenderer.renderItem(
                    stackToRender,
                    ItemDisplayContext.NONE,
                    light,
                    OverlayTexture.DEFAULT_UV,
                    matrices,
                    vertexConsumers,
                    client.world,
                    1
            );

            stackToRender.set(DataComponentTypes.ITEM_MODEL, customModel);
        }
    }
}
