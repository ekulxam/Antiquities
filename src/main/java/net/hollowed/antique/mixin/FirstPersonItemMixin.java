package net.hollowed.antique.mixin;

import net.hollowed.antique.enchantments.EnchantmentListener;
import net.hollowed.antique.items.custom.VelocityTransferMaceItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class FirstPersonItemMixin {

    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"))
    private void spin(LivingEntity entity, ItemStack stack, ItemDisplayContext renderMode, MatrixStack matrices, VertexConsumerProvider vertexConsumer, int light, CallbackInfo ci) {

        if (entity instanceof PlayerEntity player && player.getActiveItem().getItem() instanceof VelocityTransferMaceItem) {
            int useTime = player.getItemUseTime();
            float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(true);

            if (EnchantmentListener.hasEnchantment(stack, "antique:kinematic") && player.getActiveItem().equals(stack)) {
                // Define maximum angular velocity and an acceleration factor
                final float totalRotation = getTotalRotation(useTime, tickDelta);

                matrices.translate(0, 0, -0.2);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-totalRotation));
                matrices.translate(0, 0, 0.1);
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