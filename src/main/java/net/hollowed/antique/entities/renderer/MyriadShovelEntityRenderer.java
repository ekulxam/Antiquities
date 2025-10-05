package net.hollowed.antique.entities.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.client.renderer.cloth.ClothManager;
import net.hollowed.antique.entities.MyriadShovelEntity;
import net.hollowed.antique.index.AntiqueDataComponentTypes;
import net.hollowed.antique.util.resources.ClothSkinData;
import net.hollowed.antique.util.resources.ClothSkinListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class MyriadShovelEntityRenderer extends EntityRenderer<MyriadShovelEntity, MyriadShovelRenderState> {

	public MyriadShovelEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	@Override
	public void render(MyriadShovelRenderState myriadShovelRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
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
			ItemStack shovel = entity.getItemStack();
			shovel.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(myriadShovelRenderState.color));
			shovel.set(AntiqueDataComponentTypes.CLOTH_TYPE, myriadShovelRenderState.cloth);

			ItemRenderState stackRenderState = new ItemRenderState();
			MinecraftClient.getInstance().getItemModelManager().update(stackRenderState, shovel, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, MinecraftClient.getInstance().world, null, 1);
			stackRenderState.render(matrixStack, queue, myriadShovelRenderState.light, OverlayTexture.DEFAULT_UV, 0);

			ClothSkinData.ClothSubData data = ClothSkinListener.getTransform(shovel.getOrDefault(AntiqueDataComponentTypes.CLOTH_TYPE, "cloth"));

			ClothManager manager = ClothManager.getOrCreate(entity, Antiquities.id(entity.getId() + "_spade"));
			if(manager != null && shovel.get(DataComponentTypes.DYED_COLOR) != null) {
				matrixStack.translate(0.05, 0.3, 0.1);
				Vec3d itemWorldPos = ClothManager.matrixToVec(matrixStack);
				manager.renderCloth(
						itemWorldPos,
						matrixStack,
						queue,
						data.light() != 0 ? data.light() : myriadShovelRenderState.light,
						myriadShovelRenderState.glow,
						data.dyeable() ? new Color(myriadShovelRenderState.color) : Color.WHITE,
						new Color(myriadShovelRenderState.overlayColor),
						true,
						data.model(),
						Identifier.of(myriadShovelRenderState.pattern),
						data.length() != 0 ? data.length() : 1.4,
						data.width() != 0 ? data.width() : 0.1,
						data.bodyAmount() != 0 ? data.bodyAmount() : 8
				);
			}
		}
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
		myriadShovelRenderState.stack = myriadShovelEntity.getItemStack();
		myriadShovelRenderState.color = myriadShovelEntity.getDyeColor();
		myriadShovelRenderState.overlayColor = myriadShovelEntity.getOverlayColor();
		myriadShovelRenderState.isEnchanted = myriadShovelEntity.isEnchanted();
		myriadShovelRenderState.glow = myriadShovelEntity.getGlow();
		myriadShovelRenderState.cloth = myriadShovelEntity.getCloth();
		myriadShovelRenderState.pattern = myriadShovelEntity.getPattern();
	}
}