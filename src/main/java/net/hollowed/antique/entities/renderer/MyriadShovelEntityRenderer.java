package net.hollowed.antique.entities.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.client.renderer.cloth.ClothManager;
import net.hollowed.antique.entities.MyriadShovelEntity;
import net.hollowed.antique.index.AntiqueDataComponentTypes;
import net.hollowed.antique.index.AntiqueItems;
import net.hollowed.antique.items.components.MyriadToolComponent;
import net.hollowed.antique.util.resources.ClothSkinData;
import net.hollowed.antique.util.resources.ClothSkinListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.awt.*;

@Environment(EnvType.CLIENT)
public class MyriadShovelEntityRenderer extends EntityRenderer<MyriadShovelEntity, MyriadShovelRenderState> {

	public MyriadShovelEntityRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void submit(MyriadShovelRenderState myriadShovelRenderState, PoseStack matrixStack, SubmitNodeCollector queue, CameraRenderState cameraState) {
		matrixStack.pushPose();

		float multiplier = 1.25F;

		matrixStack.translate(myriadShovelRenderState.entity.getViewVector(0).multiply(multiplier, multiplier, -multiplier));

		matrixStack.mulPose(Axis.YP.rotationDegrees(myriadShovelRenderState.entity.getYRot() - 180.0F));
		matrixStack.mulPose(Axis.XP.rotationDegrees(myriadShovelRenderState.entity.getXRot() - 105.0F));

		matrixStack.scale(1.5F, 1.5F, 1.5F);
		matrixStack.translate(0, 0, 0.125);

		matrixStack.mulPose(Axis.YP.rotationDegrees(90.0F));
		matrixStack.mulPose(Axis.ZP.rotationDegrees(15.0F));
		matrixStack.mulPose(Axis.XP.rotationDegrees(-20.0F));

		if (myriadShovelRenderState.entity instanceof MyriadShovelEntity entity) {
			ItemStack shovel = entity.getPickupItemStackOrigin();
			shovel.set(AntiqueDataComponentTypes.MYRIAD_TOOL, new MyriadToolComponent(
					AntiqueItems.MYRIAD_SHOVEL_HEAD.getDefaultInstance(),
					myriadShovelRenderState.cloth,
					myriadShovelRenderState.pattern,
					myriadShovelRenderState.color,
					myriadShovelRenderState.overlayColor
			));

			ItemStackRenderState stackRenderState = new ItemStackRenderState();
			Minecraft.getInstance().getItemModelResolver().appendItemLayers(stackRenderState, shovel, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, Minecraft.getInstance().level, null, 1);
			stackRenderState.submit(matrixStack, queue, myriadShovelRenderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);

			ClothSkinData.ClothSubData data = ClothSkinListener.getTransform(shovel.getOrDefault(AntiqueDataComponentTypes.MYRIAD_TOOL, Antiquities.getDefaultMyriadTool()).clothType());

			ClothManager manager = ClothManager.getOrCreate(entity, Antiquities.id(entity.getId() + "_spade"));
			if(manager != null) {
				matrixStack.translate(0.05, 0.3, 0.1);
				manager.renderCloth(
						matrixStack,
						queue,
						data.light() != 0 ? data.light() : myriadShovelRenderState.lightCoords,
						myriadShovelRenderState.glow,
						data.dyeable() ? new Color(myriadShovelRenderState.color) : Color.WHITE,
						new Color(myriadShovelRenderState.overlayColor),
						!myriadShovelRenderState.cloth.isEmpty() ? data.model() : null,
						Identifier.parse(myriadShovelRenderState.pattern),
						data.length() != 0 ? data.length() : 1.4,
						data.width() != 0 ? data.width() : 0.1,
						data.bodyAmount() != 0 ? data.bodyAmount() : 8
				);
			}
		}
		matrixStack.popPose();
	}

	@Override
	protected boolean affectedByCulling(MyriadShovelEntity entity) {
		return false;
	}

	public MyriadShovelRenderState createRenderState() {
		return new MyriadShovelRenderState();
	}

	public void extractRenderState(MyriadShovelEntity myriadShovelEntity, MyriadShovelRenderState myriadShovelRenderState, float f) {
		super.extractRenderState(myriadShovelEntity, myriadShovelRenderState, f);
		myriadShovelRenderState.entity = myriadShovelEntity;
		myriadShovelRenderState.stack = myriadShovelEntity.getPickupItemStackOrigin();
		myriadShovelRenderState.color = myriadShovelEntity.getDyeColor();
		myriadShovelRenderState.overlayColor = myriadShovelEntity.getOverlayColor();
		myriadShovelRenderState.isEnchanted = myriadShovelEntity.isEnchanted();
		myriadShovelRenderState.glow = myriadShovelEntity.getGlow();
		myriadShovelRenderState.cloth = myriadShovelEntity.getCloth();
		myriadShovelRenderState.pattern = myriadShovelEntity.getPattern();
	}
}