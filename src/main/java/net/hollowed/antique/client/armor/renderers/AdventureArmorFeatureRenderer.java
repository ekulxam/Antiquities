package net.hollowed.antique.client.armor.renderers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.client.armor.models.AdventureArmor;
import net.hollowed.antique.client.armor.models.ArmorStandAdventureArmor;
import net.hollowed.antique.index.AntiqueEntityLayers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;
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

    private static AdventureArmor<BipedEntityRenderState> model;
    private static ArmorStandAdventureArmor armorStandModel;

    @Override
    public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, ItemStack stack, BipedEntityRenderState state, EquipmentSlot slot, int light, BipedEntityModel<BipedEntityRenderState> contextModel) {
        if (model == null) {
            model = new AdventureArmor<>(MinecraftClient.getInstance().getLoadedEntityModels().getModelPart(AntiqueEntityLayers.ADVENTURE_ARMOR));
        }
        if (armorStandModel == null) {
            armorStandModel = new ArmorStandAdventureArmor(MinecraftClient.getInstance().getLoadedEntityModels().getModelPart(AntiqueEntityLayers.ARMOR_STAND_ADVENTURE_ARMOR));
        }

        boolean slim = state instanceof PlayerEntityRenderState playerState && playerState.skinTextures.model() == PlayerSkinType.SLIM || state instanceof SkeletonEntityRenderState;

        if (state instanceof ArmorStandEntityRenderState standState) {
            queue.getBatchingQueue(1).submitModel(
                    armorStandModel,
                    standState,
                    matrices,
                    RenderLayer.getArmorCutoutNoCull(TEXTURE),
                    light,
                    OverlayTexture.DEFAULT_UV,
                    standState.outlineColor,
                    null
            );
        } else {
            queue.getBatchingQueue(1).submitModel(
                    model,
                    state,
                    matrices,
                    RenderLayer.getArmorCutoutNoCull(slim ? TEXTURE : THICK_TEXTURE),
                    light,
                    OverlayTexture.DEFAULT_UV,
                    state.outlineColor,
                    null
            );
        }
    }
}
