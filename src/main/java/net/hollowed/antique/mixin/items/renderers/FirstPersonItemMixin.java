package net.hollowed.antique.mixin.items.renderers;

import net.hollowed.antique.enchantments.EnchantmentListener;
import net.hollowed.antique.index.AntiqueDataComponentTypes;
import net.hollowed.antique.index.AntiqueItems;
import net.hollowed.antique.items.ScepterItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class FirstPersonItemMixin {

    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V",
            at = @At("HEAD"))
    private void itemTransforms(LivingEntity entity, ItemStack stack, ItemDisplayContext renderMode, MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, int light, CallbackInfo ci) {
        if (entity instanceof PlayerEntity player) {
            ItemStack activeStack = player.getActiveItem();
            int useTime = player.getItemUseTime();
            float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(true);
            boolean right = !renderMode.isLeftHand();
            if (activeStack.getItem() instanceof ScepterItem) {
                if (EnchantmentListener.hasEnchantment(stack, "antique:kinematic") && activeStack.equals(stack)) {
                    final float totalRotation = getTotalRotation(useTime, tickDelta);
                    matrices.translate(0, 0, -0.2);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-totalRotation));
                    matrices.translate(0, 0, 0.1);
                }
            } else if (activeStack.isOf(AntiqueItems.MYRIAD_TOOL) && activeStack.getOrDefault(AntiqueDataComponentTypes.MYRIAD_STACK, ItemStack.EMPTY).isOf(AntiqueItems.MYRIAD_CLEAVER_BLADE)) {
                matrices.translate(right ? 0.2 : -0.2, 0.15,0);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(45));
            }
        }
    }

    @Unique
    private static float getTotalRotation(int useTime, float tickDelta) {
        float maxAngularVelocity = 120.0F;
        float accelerationFactor = 0.004F;
        float progress = Math.min(1.0F, (useTime + tickDelta) * accelerationFactor);
        float easedVelocity = maxAngularVelocity * (1 - (float) Math.pow(1 - progress, 3));
        return (useTime + tickDelta) * easedVelocity;
    }
}