package net.hollowed.antique.entities.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.client.item.explosive_spear.ClothManager;
import net.hollowed.antique.entities.custom.MyriadShovelEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class MyriadShovelEntityRenderer extends EntityRenderer<MyriadShovelEntity, MyriadShovelRenderState> {

	public MyriadShovelEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	public void render(MyriadShovelRenderState myriadShovelRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
		// Push the matrix stack for transformations
		matrixStack.push();

		float multiplier = 1.25F;

		matrixStack.translate(myriadShovelRenderState.entity.getRotationVec(0).multiply(multiplier, multiplier, -multiplier));

		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(myriadShovelRenderState.entity.getYaw() - 180.0F));
		matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(myriadShovelRenderState.entity.getPitch() - 105.0F));

		matrixStack.scale(1.5F, 1.5F, 1.5F);
		matrixStack.translate(0, 0, 0.125);

		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
		matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(15.0F));
		matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-20.0F));

		if (myriadShovelRenderState.entity instanceof MyriadShovelEntity entity) {
			ItemStack shovel = myriadShovelRenderState.stack;
			shovel.set(DataComponentTypes.ITEM_MODEL, Antiquities.id("myriad_shovel"));
			shovel.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, myriadShovelRenderState.isEnchanted);
			shovel.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(myriadShovelRenderState.color));

			// Use the ItemRenderer to render the trident item with a FIRST_PERSON_RIGHT_HAND transformation
			ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
			itemRenderer.renderItem(shovel, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, light, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider, MinecraftClient.getInstance().world, 0);

			ClothManager manager = entity.manager;
			if(manager != null && shovel.get(DataComponentTypes.DYED_COLOR) != null) {
				matrixStack.translate(0.05, 0.3, 0.1);
				Vec3d itemWorldPos = ClothManager.matrixToVec(matrixStack);
				manager.renderCloth(itemWorldPos, matrixStack, vertexConsumerProvider, light, new Color(myriadShovelRenderState.color), false, ClothManager.TATTERED_CLOTH_STRIP, 2, 0.1);
			}
		}
		// Pop the matrix stack to clean up transformations
		matrixStack.pop();
	}

	@Override
	protected boolean canBeCulled(MyriadShovelEntity entity) {
		return false;
	}

	public MyriadShovelRenderState createRenderState() {
		return new MyriadShovelRenderState();
	}

	public void updateRenderState(MyriadShovelEntity myriadShovelEntity, MyriadShovelRenderState myriadShovelRenderState, float f) {
		super.updateRenderState(myriadShovelEntity, myriadShovelRenderState, f);
		myriadShovelRenderState.entity = myriadShovelEntity;
		myriadShovelRenderState.stack = myriadShovelEntity.shovelStack;
		myriadShovelRenderState.color = myriadShovelEntity.getDyeColor();
		myriadShovelRenderState.isEnchanted = myriadShovelEntity.isEnchanted();
	}
}