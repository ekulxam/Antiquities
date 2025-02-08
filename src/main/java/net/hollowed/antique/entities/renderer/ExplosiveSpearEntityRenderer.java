package net.hollowed.antique.entities.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.antique.client.item.explosive_spear.ClothManager;
import net.hollowed.antique.entities.custom.ExplosiveSpearEntity;
import net.hollowed.antique.entities.custom.MyriadShovelEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
public class ExplosiveSpearEntityRenderer extends EntityRenderer<ExplosiveSpearEntity, ExplosiveSpearRenderState> {

	public ExplosiveSpearEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	public void render(ExplosiveSpearRenderState myriadShovelRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
		// Push the matrix stack for transformations
		matrixStack.push();

		float multiplier = 1.25F;

		matrixStack.translate(myriadShovelRenderState.entity.getRotationVec(0).multiply(multiplier, multiplier, -multiplier));

		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(myriadShovelRenderState.entity.getYaw() - 180.0F));
		matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(myriadShovelRenderState.entity.getPitch() - 105.0F));

		matrixStack.scale(1.5F, 1.5F, 1.5F);
		matrixStack.translate(-0.1, 0, -0.1);

		matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(15));

		if (myriadShovelRenderState.entity instanceof ExplosiveSpearEntity entity) {
			ItemStack shovel = entity.shovelStack;
			shovel.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, entity.isEnchanted());

			// Use the ItemRenderer to render the trident item with a FIRST_PERSON_RIGHT_HAND transformation
			ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
			itemRenderer.renderItem(shovel, ModelTransformationMode.FIRST_PERSON_RIGHT_HAND, light, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider, MinecraftClient.getInstance().world, 0);

			ClothManager manager = entity.manager;
			if(manager != null) {
				matrixStack.translate(0.1, 0.5, 0.1);
				Matrix4f matrix = matrixStack.peek().getPositionMatrix();

				Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
				Vec3d itemWorldPos = transformToWorld(matrix, camera);

				manager.tick(MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false));
				manager.renderCloth(itemWorldPos, matrixStack, vertexConsumerProvider, light);
			}
		}
		// Pop the matrix stack to clean up transformations
		matrixStack.pop();
	}

	@Unique
	private Vec3d transformToWorld(Matrix4f matrix, Camera camera) {
		// Convert (0,0,0) in local item space to transformed coordinates
		Vector4f localPos = new Vector4f(0, 0, 0, 1);
		matrix.transform(localPos);

		// Convert view space to world space by adding the camera position
		Vec3d cameraPos = camera.getPos();
		return new Vec3d(cameraPos.x + localPos.x(), cameraPos.y + localPos.y(), cameraPos.z + localPos.z());
	}

	public ExplosiveSpearRenderState createRenderState() {
		return new ExplosiveSpearRenderState();
	}

	public void updateRenderState(ExplosiveSpearEntity myriadShovelEntity, ExplosiveSpearRenderState myriadShovelRenderState, float f) {
		super.updateRenderState(myriadShovelEntity, myriadShovelRenderState, f);
		myriadShovelRenderState.entity = myriadShovelEntity;
	}
}