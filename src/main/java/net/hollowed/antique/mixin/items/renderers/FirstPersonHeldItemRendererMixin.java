package net.hollowed.antique.mixin.items.renderers;

import net.hollowed.antique.client.renderer.cloth.ClothManager;
import net.hollowed.antique.index.AntiqueComponents;
import net.hollowed.antique.index.AntiqueItems;
import net.hollowed.antique.util.interfaces.duck.SpearClothAccess;
import net.hollowed.antique.util.resources.MyriadStaffTransformData;
import net.hollowed.antique.util.resources.MyriadStaffTransformResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.Objects;

@Mixin(HeldItemRenderer.class)
public abstract class FirstPersonHeldItemRendererMixin {

    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    public void renderItem(LivingEntity entity, ItemStack stack, ItemDisplayContext renderMode, MatrixStack matrices, VertexConsumerProvider vertexConsumer, int light, CallbackInfo ci) {
        matrices.push();
        boolean leftHanded = entity.getMainArm() == Arm.LEFT;
        matrices.translate((float)(leftHanded ? -1 : 1) / 16.0F, 0.125F, -0.625F);
        switch (renderMode) {
            case ItemDisplayContext.FIRST_PERSON_RIGHT_HAND -> matrices.translate(leftHanded ? 0.1 : 0, 0, 0);
            case ItemDisplayContext.FIRST_PERSON_LEFT_HAND -> matrices.translate(!leftHanded ? -0.1 : 0, 0, 0);
        }

        matrices.translate(0, 0.4, 0.7);
        if (renderMode == ItemDisplayContext.NONE) {
            matrices.translate(0, -0.5, -0.1);
        }

        MinecraftClient client = MinecraftClient.getInstance();
        ItemRenderer itemRenderer = client.getItemRenderer();

        ClothManager manager;
        Vec3d itemWorldPos;

        if (entity instanceof SpearClothAccess clothAccess) {
            if (entity instanceof PlayerEntity player) {
                if (stack.isOf(AntiqueItems.MYRIAD_TOOL) || stack.isOf(AntiqueItems.MYRIAD_STAFF)) {
                    if (renderMode != ItemDisplayContext.NONE) {
                        matrices.translate(0, -0.1, 0.1);
                    }
                    if (stack.getOrDefault(AntiqueComponents.MYRIAD_STACK, ItemStack.EMPTY).isOf(AntiqueItems.MYRIAD_AXE_HEAD) && entity.isUsingItem()) {
                        matrices.translate(renderMode == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND ? -0.5 : 0.5, -0.1, 0);
                    }
                    if (stack.getOrDefault(AntiqueComponents.MYRIAD_STACK, ItemStack.EMPTY).isOf(AntiqueItems.MYRIAD_SHOVEL_HEAD) && entity.isUsingItem()) {
                        matrices.translate(renderMode == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND ? 0.1 : -0.1, 0, -0.2);
                    }
                    if (renderMode == ItemDisplayContext.NONE && (
                            stack.getOrDefault(AntiqueComponents.MYRIAD_STACK, ItemStack.EMPTY).isOf(AntiqueItems.MYRIAD_CLEAVER_BLADE)
                            || stack.isOf(AntiqueItems.MYRIAD_STAFF))) {
                        matrices.translate(-0.15, -0.15, 0);
                    }
                    if (renderMode.isFirstPerson() && stack.isOf(AntiqueItems.MYRIAD_STAFF)) {
                        matrices.translate(-0.1, -0.1, 0);
                    }
                    itemWorldPos = ClothManager.matrixToVec(matrices);
                    manager = player.getMainHandStack().equals(stack) ? clothAccess.antique$getRightArmCloth() : clothAccess.antique$getLeftArmCloth();
                    switch (renderMode) {
                        case ItemDisplayContext.NONE -> manager = clothAccess.antique$getBackCloth();
                        case ItemDisplayContext.GUI -> manager = null;
                    }
                    if (player.getInventory().getStack(42).equals(stack)) {
                        manager = clothAccess.antique$getBeltCloth();
                    }
                    if (manager != null && stack.get(DataComponentTypes.DYED_COLOR) != null) {
                        Object name = stack.getOrDefault(DataComponentTypes.CUSTOM_NAME, "");
                        if (!(stack.isOf(AntiqueItems.MYRIAD_STAFF) && (name.equals(Text.literal("Perfected Staff")) || name.equals(Text.literal("Orb Staff")) || name.equals(Text.literal("Lapis Staff"))))) {
                            manager.renderCloth(itemWorldPos, matrices, vertexConsumer, light, new Color(Objects.requireNonNull(stack.get(DataComponentTypes.DYED_COLOR)).rgb()), false, ClothManager.TATTERED_CLOTH_STRIP, 1.4, 0.1);
                        }
                    }
                }
            }
        }

        if (stack.isOf(AntiqueItems.MYRIAD_STAFF)) {
            if (!renderMode.equals(ItemDisplayContext.NONE)) {
                matrices.translate(0.25, 0.5, 0.025);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(20));
                matrices.scale(0.45F, 0.45F, 0.45F);

                ItemStack stackToRender = stack.getOrDefault(AntiqueComponents.MYRIAD_STACK, ItemStack.EMPTY);

                matrices.scale(0.875F, 0.875F, 0.875F);
                matrices.translate(0.0, -0.035, 0.05);

                MyriadStaffTransformData data = MyriadStaffTransformResourceReloadListener.getTransform(Registries.ITEM.getId(stackToRender.getItem()));
                matrices.scale(data.scale().get(0), data.scale().get(1), data.scale().get(2));
                matrices.translate(data.translation().get(0), data.translation().get(1), data.translation().get(2));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(data.rotation().getFirst()));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(data.rotation().get(1)));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(data.rotation().get(2)));

                Identifier customModel = stackToRender.getOrDefault(DataComponentTypes.ITEM_MODEL, Registries.ITEM.getId(stackToRender.getItem()));
                if (!data.model().equals(Identifier.of("default"))) {
                    stackToRender.set(DataComponentTypes.ITEM_MODEL, data.model());
                }

                itemRenderer.renderItem(
                        stackToRender,
                        ItemDisplayContext.NONE,
                        light,
                        OverlayTexture.DEFAULT_UV,
                        matrices,
                        vertexConsumer,
                        client.world,
                        1
                );

                stackToRender.set(DataComponentTypes.ITEM_MODEL, customModel);
            }
        }

        matrices.pop();
    }
}
