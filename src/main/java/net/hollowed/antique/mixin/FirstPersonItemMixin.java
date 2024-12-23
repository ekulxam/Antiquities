package net.hollowed.antique.mixin;

import net.hollowed.antique.Antiquities;
import net.hollowed.antique.enchantments.EnchantmentListener;
import net.hollowed.antique.items.custom.VelocityTransferMaceItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.feature.PlayerHeldItemFeatureRenderer;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class FirstPersonItemMixin {

    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"))
    private void spin(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {

        if (entity instanceof PlayerEntity player && player.getActiveItem().getItem() instanceof VelocityTransferMaceItem) {
            int useTime = player.getItemUseTime();
            float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);

            if (EnchantmentListener.hasCustomEnchantment(stack, "antique:kinematic")) {
                // Limit multiplier to avoid excessive spinning
                float multiplier = (useTime + tickDelta) * (useTime + tickDelta) / 5000000F;
                float time = (player.getWorld().getTime() + tickDelta);
                matrices.translate(0, 0, -0.2);
                if (useTime <= 80) {
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-time * multiplier));
                } else {
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-time * 110.0F + tickDelta));
                }
                matrices.translate(0, 0, 0.1);
            }
        }
    }
}