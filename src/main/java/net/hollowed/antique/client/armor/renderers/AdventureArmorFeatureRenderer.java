package net.hollowed.antique.client.armor.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.client.armor.models.AdventureArmor;
import net.hollowed.antique.index.AntiqueEntityLayers;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class AdventureArmorFeatureRenderer implements ArmorRenderer {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Antiquities.MOD_ID, "textures/entity/adventure_armor.png");
    private static final Identifier THICK_TEXTURE = Identifier.fromNamespaceAndPath(Antiquities.MOD_ID, "textures/entity/adventure_armor_thick.png");

    private final AdventureArmor<HumanoidRenderState> model;

    public AdventureArmorFeatureRenderer(EntityRendererProvider.Context context) {
        this.model = new AdventureArmor<>(context.getModelSet().bakeLayer(AntiqueEntityLayers.ADVENTURE_ARMOR));
    }

    @Override
    public void render(PoseStack matrices, SubmitNodeCollector queue, ItemStack stack, HumanoidRenderState state, EquipmentSlot slot, int light, HumanoidModel<HumanoidRenderState> contextModel) {
        boolean slim = state instanceof AvatarRenderState playerState && playerState.skin.model() == PlayerModelType.SLIM || state instanceof SkeletonRenderState;

        ArmorRenderer.submitTransformCopyingModel(
                contextModel,
                state,
                model,
                state,
                true,
                queue,
                matrices,
                RenderTypes.armorCutoutNoCull(slim ? TEXTURE : THICK_TEXTURE),
                light,
                OverlayTexture.NO_OVERLAY,
                state.outlineColor,
                null
        );

        if (stack.hasFoil()) {
            ArmorRenderer.submitTransformCopyingModel(
                    contextModel,
                    state,
                    model,
                    state,
                    true,
                    queue,
                    matrices,
                    RenderTypes.armorEntityGlint(),
                    light,
                    OverlayTexture.NO_OVERLAY,
                    state.outlineColor,
                    null
            );
        }
    }

    public static class Factory implements ArmorRenderer.Factory {
        @Override
        public ArmorRenderer createArmorRenderer(EntityRendererProvider.Context context) {
            return new AdventureArmorFeatureRenderer(context);
        }
    }
}
