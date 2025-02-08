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
            World world = entity.getWorld();

            // Extract transformation matrix
            Matrix4f matrix = matrices.peek().getPositionMatrix();

            // Convert local position to world space
            Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
            Vec3d itemWorldPos = transformToWorld(matrix, camera);

            ClothManager manager = new ClothManager(new Vector3d(itemWorldPos.x, itemWorldPos.y, itemWorldPos.z), 4);

            if (entity instanceof SpearClothAccess clothAccess) {
                manager = arm == Arm.RIGHT ? clothAccess.antique$getRightArmCloth() : clothAccess.antique$getLeftArmCloth();
            }

            renderCloth(manager, itemWorldPos, matrices, vertexConsumers, light);
            //this.renderDebugPoint(matrices, vertexConsumers);

            if (entity instanceof LivingEntity living && living.getStackInArm(arm).isOf(ModItems.EXPLOSIVE_SPEAR)) {
                world.addParticle(ParticleTypes.SMALL_FLAME, itemWorldPos.x, itemWorldPos.y, itemWorldPos.z, 0, 0, 0);
            }

            matrices.pop();
        }
    }

    @Unique
    public void renderCloth(ClothManager manager, Vec3d position, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getCutoutMipped());
        Vector3d danglePos = new Vector3d(position.x, position.y, position.z);

        manager.pos = new Vector3d(danglePos);
        double thickness = 0.1;

        int count = manager.bodies.size();
        for (int i = 0; i < count - 1; i++) {

            ClothBody body = manager.bodies.get(i);
            ClothBody nextBody = manager.bodies.get(i + 1);

            var pos = body.getPos();
            var nextPos = nextBody.getPos();

            float uvTop = (1f / count) * i;
            float uvBot = uvTop + (1f / count);

            var a1 = pos.add(thickness, 0.0, 0.0, new Vector3d());
            var b1 = pos.add(-thickness, 0.0, 0.0, new Vector3d());
            var c1 = nextPos.add(-thickness, 0.0, 0.0, new Vector3d());
            drawTriangle(
                    new Matrix4f(),
                    vertexConsumer,
                    a1, b1, c1,
                    new Vec2f(0f,uvTop),
                    new Vec2f(1f,uvTop),
                    new Vec2f(0f,uvBot),
                    light);

            var a2 = nextPos.add(-thickness, 0.0, 0.0, new Vector3d());
            var b2 = pos.add(thickness, 0.0, 0.0, new Vector3d());
            var c2 = nextPos.add(thickness, 0.0, 0.0, new Vector3d());
            drawTriangle(
                    new Matrix4f(),
                    vertexConsumer,
                    a2, b2, c2,
                    new Vec2f(0f,uvBot),
                    new Vec2f(1f,uvTop),
                    new Vec2f(1f,uvBot),
                    light);
        }

        matrices.pop();
    }

    @Unique
    public void drawTriangle(Matrix4f matrix, VertexConsumer vertexConsumer, Vector3d posA, Vector3d posB, Vector3d posC, Vec2f uvA, Vec2f uvB, Vec2f uvC, int light) {
        // Draw a line from pos1 to pos2
        vertexConsumer.vertex(matrix, (float) posA.x, (float) posA.y, (float) posA.z).color(255, 255, 255, 255).normal(1f,0f,0f).light(light).texture(uvA.x, uvA.y);
        vertexConsumer.vertex(matrix, (float) posB.x, (float) posB.y, (float) posB.z).color(255, 255, 255, 255).normal(1f,0f,0f).light(light).texture(uvB.x, uvB.y);
        vertexConsumer.vertex(matrix, (float) posC.x, (float) posC.y, (float) posC.z).color(255, 255, 255, 255).normal(1f,0f,0f).light(light).texture(uvC.x, uvC.y);
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
