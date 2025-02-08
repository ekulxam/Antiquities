package net.hollowed.antique.mixin;

import net.hollowed.antique.client.item.explosive_spear.ClothBody;
import net.hollowed.antique.client.item.explosive_spear.ClothManager;
import net.hollowed.antique.items.ModItems;
import net.hollowed.antique.util.ArmedRenderStateAccess;
import net.hollowed.antique.util.SpearClothAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Arm;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemFeatureRenderer.class)
public abstract class HeldItemRendererMixin<S extends ArmedEntityRenderState, M extends EntityModel<S> & ModelWithArms> extends FeatureRenderer<S, M> {

    public HeldItemRendererMixin(FeatureRendererContext<S, M> context) {
        super(context);
    }

    @Inject(method = "renderItem", at = @At("HEAD"))
    public void renderItem(S entityState, ItemRenderState itemState, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (entityState instanceof ArmedRenderStateAccess access) {
            matrices.push();
            this.getContextModel().setArmAngle(arm, matrices);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
            boolean bl = arm == Arm.LEFT;
            matrices.translate((float)(bl ? -1 : 1) / 16.0F, 0.125F, -0.625F);
            matrices.translate(0, 0.6, 0);

            Entity entity = access.antique$getEntity();

            // Extract transformation matrix
            Matrix4f matrix = matrices.peek().getPositionMatrix();

            // Convert local position to world space
            Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
            Vec3d itemWorldPos = transformToWorld(matrix, camera);

            ClothManager manager;

            if (entity instanceof SpearClothAccess clothAccess) {
                if (entity instanceof LivingEntity living && living.getStackInArm(arm).isOf(ModItems.EXPLOSIVE_SPEAR)) {
                    manager = arm == Arm.RIGHT ? clothAccess.antique$getRightArmCloth() : clothAccess.antique$getLeftArmCloth();
                    if(manager != null) {
                        manager.tick(MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false));
                        manager.renderCloth(itemWorldPos, matrices, vertexConsumers, light);
                    }
                }
            }

            //this.renderDebugPoint(matrices, vertexConsumers);

//            if (entity instanceof LivingEntity living && living.getStackInArm(arm).isOf(ModItems.EXPLOSIVE_SPEAR)) {
//                world.addParticle(ParticleTypes.SMALL_FLAME, itemWorldPos.x, itemWorldPos.y, itemWorldPos.z, 0, 0, 0);
//            }

            matrices.pop();
        }
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


    @Unique
    private void renderDebugPoint(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {
		DebugRenderer.drawBox(
				matrixStack,
				vertexConsumerProvider,
				-0.05F, -0.05F, -0.05F, // Box min (relative to pivot)
				0.05F,  0.05F,  0.05F, // Box max (relative to pivot)
				1.0F, 0.0F, 0.0F, 1.0F  // Red color
		);
    }
}
