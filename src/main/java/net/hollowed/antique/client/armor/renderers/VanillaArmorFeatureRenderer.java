package net.hollowed.antique.client.armor.renderers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.client.armor.models.VanillaArmorModel;
import net.hollowed.antique.index.AntiqueEntityLayers;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VanillaArmorFeatureRenderer implements ArmorRenderer {

    private final VanillaArmorModel<BipedEntityRenderState> model;

    public VanillaArmorFeatureRenderer(EntityRendererFactory.Context context) {
        this.model = new VanillaArmorModel<>(context.getEntityModels().getModelPart(AntiqueEntityLayers.VANILLA_ARMOR));
    }

    @Override
    public void render(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, ItemStack stack, BipedEntityRenderState state, EquipmentSlot slot, int light, BipedEntityModel<BipedEntityRenderState> contextModel) {
        ArmorRenderer.submitTransformCopyingModel(
                contextModel,
                state,
                model,
                state,
                true,
                orderedRenderCommandQueue,
                matrices,
                RenderLayer.getArmorCutoutNoCull(getArmorTextureFromStack(stack)),
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
                    orderedRenderCommandQueue,
                    matrices,
                    RenderLayer.getArmorEntityGlint(),
                    light,
                    OverlayTexture.DEFAULT_UV,
                    state.outlineColor,
                    null
            );
        }
    }

    private Identifier getArmorTextureFromStack(ItemStack stack) {
        EquippableComponent equippableComponent = stack.get(DataComponentTypes.EQUIPPABLE);
        if (equippableComponent == null) return Antiquities.id("empty");
        return Antiquities.id("textures/entity/" + equippableComponent.assetId().orElseThrow().getValue().getPath() + ".png");
    }

    public static class Factory implements ArmorRenderer.Factory {
        @Override
        public ArmorRenderer createArmorRenderer(EntityRendererFactory.Context context) {
            return new VanillaArmorFeatureRenderer(context);
        }
    }
}
