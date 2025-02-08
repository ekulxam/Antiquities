package net.hollowed.antique.mixin;

import net.hollowed.antique.client.item.explosive_spear.ClothManager;
import net.hollowed.antique.items.ModItems;
import net.hollowed.antique.util.SpearClothAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class FirstPersonHeldItemRendererMixin {

    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    public void renderItem(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        matrices.push();
        matrices.translate((float)(leftHanded ? -1 : 1) / 16.0F, 0.125F, -0.625F);
        matrices.translate(0, 0.4, 0.7);
        if (renderMode == ModelTransformationMode.NONE) {
            matrices.translate(0, -0.5, -0.1);
        }

        // Extract transformation matrix
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        // Convert local position to world space
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        Vec3d itemWorldPos = transformToWorld(matrix, camera);

        ClothManager manager;

        if (entity instanceof SpearClothAccess clothAccess) {
            if (entity instanceof LivingEntity && stack.isOf(ModItems.EXPLOSIVE_SPEAR)) {
                manager = !leftHanded ? clothAccess.antique$getRightArmCloth() : clothAccess.antique$getLeftArmCloth();
                if (renderMode == ModelTransformationMode.NONE) {
                    manager = clothAccess.antique$getBackCloth();
                }
                if (renderMode == ModelTransformationMode.GUI) {
                    manager = null;
                }
                if(manager != null) {
                    manager.tick(MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false));
                    manager.renderCloth(itemWorldPos, matrices, vertexConsumers, light);
                }
            }
        }

        matrices.pop();
    }

    // **Helper Method: Converts Rendered Item Position to World Space**
    @Unique
    private Vec3d transformToWorld(Matrix4f matrix, Camera camera) {
        // Convert (0,0,0) in local item space to transformed coordinates
        Vector4f localPos = new Vector4f(0, 0, 0, 1);
        matrix.transform(localPos);

        // Convert view space to world space by adding the camera position
        Vec3d cameraPos = camera.getPos();
        return new Vec3d(cameraPos.x + localPos.x(), cameraPos.y + localPos.y(), cameraPos.z + localPos.z());
    }
}
