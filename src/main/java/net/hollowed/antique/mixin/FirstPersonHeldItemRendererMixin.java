package net.hollowed.antique.mixin;

import net.hollowed.antique.client.item.explosive_spear.ClothManager;
import net.hollowed.antique.items.ModItems;
import net.hollowed.antique.util.SpearClothAccess;
import net.hollowed.antique.util.Temp;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.Objects;

@Mixin(HeldItemRenderer.class)
public abstract class FirstPersonHeldItemRendererMixin {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    public void renderItem(LivingEntity entity, ItemStack stack, ItemDisplayContext renderMode, MatrixStack matrices, VertexConsumerProvider vertexConsumer, int light, CallbackInfo ci) {
        matrices.push();
        boolean leftHanded = entity.getMainArm() == Arm.LEFT;
        matrices.translate((float)(leftHanded ? -1 : 1) / 16.0F, 0.125F, -0.625F);
        matrices.translate(0, 0.4, 0.7);
        if (renderMode == ItemDisplayContext.NONE) {
            matrices.translate(0, -0.5, -0.1);
        }

        MinecraftClient client = MinecraftClient.getInstance();
        ItemRenderer itemRenderer = client.getItemRenderer();

        ClothManager manager;
        Vec3d itemWorldPos = ClothManager.matrixToVec(matrices);

        if (entity instanceof SpearClothAccess clothAccess) {
            if (entity instanceof PlayerEntity player) {
                if (stack.isOf(ModItems.EXPLOSIVE_SPEAR)) {
                    manager = !leftHanded ? clothAccess.antique$getRightArmCloth() : clothAccess.antique$getLeftArmCloth();
                    switch (renderMode) {
                        case ItemDisplayContext.NONE -> manager = clothAccess.antique$getBackCloth();
                        case ItemDisplayContext.GUI -> manager = null;
                    }
                    if (player.getInventory().getStack(42).equals(stack)) {
                        manager = clothAccess.antique$getBeltCloth();
                    }
                    if (manager != null) {
                        manager.renderCloth(itemWorldPos, matrices, vertexConsumer, light, renderMode != ItemDisplayContext.NONE, new Color(255, 0, 0, 255), false, ClothManager.BLANK_CLOTH_STRIP, 2, 0.1);
                    }
                }
                if (stack.isOf(ModItems.MYRIAD_TOOL) || stack.isOf(ModItems.MYRIAD_STAFF)) {
                    if (renderMode != ItemDisplayContext.NONE) {
                        matrices.translate(0, -0.1, 0.1);
                    }
                    if (stack.get(net.hollowed.combatamenities.util.items.ModComponents.INTEGER_PROPERTY) != null && Objects.requireNonNull(stack.get(net.hollowed.combatamenities.util.items.ModComponents.INTEGER_PROPERTY)) == 2 && entity.isUsingItem()) {
                        matrices.translate(-0.5, -0.1, 0);
                    }
                    if (renderMode == ItemDisplayContext.NONE && (
                            stack.getOrDefault(net.hollowed.combatamenities.util.items.ModComponents.INTEGER_PROPERTY, -1) == 4)
                            || stack.isOf(ModItems.MYRIAD_STAFF)) {
                        matrices.translate(-0.15, -0.15, 0);
                    }
                    if (renderMode.isFirstPerson() && stack.isOf(ModItems.MYRIAD_STAFF)) {
                        matrices.translate(-0.1, -0.1, 0);
                    }
                    itemWorldPos = ClothManager.matrixToVec(matrices);
                    manager = !leftHanded ? clothAccess.antique$getRightArmCloth() : clothAccess.antique$getLeftArmCloth();
                    switch (renderMode) {
                        case ItemDisplayContext.NONE -> manager = clothAccess.antique$getBackCloth();
                        case ItemDisplayContext.GUI -> manager = null;
                    }
                    if (player.getInventory().getStack(42).equals(stack)) {
                        manager = clothAccess.antique$getBeltCloth();
                    }
                    if (manager != null && stack.get(DataComponentTypes.DYED_COLOR) != null) {
                        Object name = stack.getOrDefault(DataComponentTypes.CUSTOM_NAME, "");
                        if (!(stack.isOf(ModItems.MYRIAD_STAFF) && (name.equals(Text.literal("Perfected Staff")) || name.equals(Text.literal("Orb Staff")) || name.equals(Text.literal("Lapis Staff"))))) {
                            manager.renderCloth(itemWorldPos, matrices, vertexConsumer, light, renderMode != ItemDisplayContext.NONE, new Color(Objects.requireNonNull(stack.get(DataComponentTypes.DYED_COLOR)).rgb()), false, ClothManager.TATTERED_CLOTH_STRIP, 2, 0.1);
                        }
                    }
                }
            }
        }

        if (stack.isOf(ModItems.MYRIAD_STAFF)) {
            Temp.method2(itemRenderer, stack, light, matrices, vertexConsumer, client, renderMode);
        }

        matrices.pop();
    }
}
