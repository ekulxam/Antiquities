package net.hollowed.antique.client.armor.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.client.armor.models.VanillaArmorModel;
import net.hollowed.antique.index.AntiqueEntityLayers;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class VanillaArmorFeatureRenderer implements ArmorRenderer {

    private final VanillaArmorModel<@NotNull HumanoidRenderState> model;

    public VanillaArmorFeatureRenderer(EntityRendererProvider.Context context) {
        this.model = new VanillaArmorModel<>(context.getModelSet().bakeLayer(AntiqueEntityLayers.VANILLA_ARMOR));
    }

    @SuppressWarnings("all")
    @Override
    public void render(@NotNull PoseStack matrices, @NotNull SubmitNodeCollector orderedRenderCommandQueue, @NotNull ItemStack stack, @NotNull HumanoidRenderState state, @NotNull EquipmentSlot slot, int light, @NotNull HumanoidModel<@NotNull HumanoidRenderState> contextModel) {
        ArmorRenderer.submitTransformCopyingModel(
                contextModel,
                state,
                model,
                state,
                true,
                orderedRenderCommandQueue,
                matrices,
                RenderTypes.armorCutoutNoCull(getArmorTextureFromStack(stack)),
                light,
                OverlayTexture.NO_OVERLAY,
                state.outlineColor,
                null
        );

        System.out.println("rendering");

        if (stack.hasFoil()) {
            ArmorRenderer.submitTransformCopyingModel(
                    contextModel,
                    state,
                    model,
                    state,
                    true,
                    orderedRenderCommandQueue,
                    matrices,
                    RenderTypes.armorEntityGlint(),
                    light,
                    OverlayTexture.NO_OVERLAY,
                    state.outlineColor,
                    null
            );
        }
    }

    private Identifier getArmorTextureFromStack(ItemStack stack) {
        Equippable equippableComponent = stack.get(DataComponents.EQUIPPABLE);
        if (equippableComponent == null) return Antiquities.id("empty");
        return Antiquities.id("textures/entity/" + equippableComponent.assetId().orElseThrow().identifier().getPath() + ".png");
    }

    public static class Factory implements ArmorRenderer.Factory {
        @Override
        public @NotNull ArmorRenderer createArmorRenderer(EntityRendererProvider.@NotNull Context context) {
            return new VanillaArmorFeatureRenderer(context);
        }
    }
}
