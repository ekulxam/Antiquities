package net.hollowed.antique.client.armor.renderers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.client.armor.models.AdventureArmor;
import net.hollowed.antique.index.AntiqueEntityLayers;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.entity.state.SkeletonEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AdventureArmorFeatureRenderer implements ArmorRenderer {

    private static final Identifier TEXTURE = Identifier.of(Antiquities.MOD_ID, "textures/entity/adventure_armor.png");
    private static final Identifier THICK_TEXTURE = Identifier.of(Antiquities.MOD_ID, "textures/entity/adventure_armor_thick.png");

    private final AdventureArmor<BipedEntityRenderState> model;

    public AdventureArmorFeatureRenderer(EntityRendererFactory.Context context) {
        this.model = new AdventureArmor<>(context.getEntityModels().getModelPart(AntiqueEntityLayers.ADVENTURE_ARMOR));
    }

    @Override
    public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, ItemStack stack, BipedEntityRenderState state, EquipmentSlot slot, int light, BipedEntityModel<BipedEntityRenderState> contextModel) {
        boolean slim = state instanceof PlayerEntityRenderState playerState && playerState.skinTextures.model() == PlayerSkinType.SLIM || state instanceof SkeletonEntityRenderState;

        ArmorRenderer.submitTransformCopyingModel(
                contextModel,
                state,
                model,
                state,
                true,
                queue,
                matrices,
                RenderLayer.getArmorCutoutNoCull(slim ? TEXTURE : THICK_TEXTURE),
                light,
                OverlayTexture.DEFAULT_UV,
                state.outlineColor,
                null
        );

        if (stack.hasGlint()) {
            ArmorRenderer.submitTransformCopyingModel(
                    contextModel,
                    state,
                    model,
                    state,
                    true,
                    queue,
                    matrices,
                    RenderLayer.getArmorEntityGlint(),
                    light,
                    OverlayTexture.DEFAULT_UV,
                    state.outlineColor,
                    null
            );
        }
    }

    public static class Factory implements ArmorRenderer.Factory {
        @Override
        public ArmorRenderer createArmorRenderer(EntityRendererFactory.Context context) {
            return new AdventureArmorFeatureRenderer(context);
        }
    }
}
