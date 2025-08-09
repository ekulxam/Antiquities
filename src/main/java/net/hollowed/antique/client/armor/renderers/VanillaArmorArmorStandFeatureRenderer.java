package net.hollowed.antique.client.armor.renderers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.client.armor.models.ArmorStandVanillaArmorModel;
import net.hollowed.antique.index.AntiqueEntityLayers;
import net.hollowed.antique.index.AntiqueItemTags;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VanillaArmorArmorStandFeatureRenderer extends FeatureRenderer<ArmorStandEntityRenderState, ArmorStandEntityModel> {

    private final Identifier texture;
    private final ArmorStandVanillaArmorModel model;
    private final TagKey<Item> tag;

    public VanillaArmorArmorStandFeatureRenderer(FeatureRendererContext<ArmorStandEntityRenderState, ArmorStandEntityModel> context, int id, LoadedEntityModels modelLoader) {
        super(context);
        this.texture = switch (id) {
            case 0 -> Antiquities.id("textures/entity/iron.png");
            case 1 -> Antiquities.id("textures/entity/chain.png");
            case 2 -> Antiquities.id("textures/entity/gold.png");
            case 3 -> Antiquities.id("textures/entity/diamond.png");
            case 4 -> Antiquities.id("textures/entity/netherite.png");
            default -> throw new IllegalStateException("Unexpected value: " + id);
        };
        this.tag = switch (id) {
            case 0 -> AntiqueItemTags.IRON_ARMOR;
            case 1 -> AntiqueItemTags.CHAIN_ARMOR;
            case 2 -> AntiqueItemTags.GOLD_ARMOR;
            case 3 -> AntiqueItemTags.DIAMOND_ARMOR;
            case 4 -> AntiqueItemTags.NETHERITE_ARMOR;
            default -> throw new IllegalStateException("Unexpected value: " + id);
        };
        this.model = new ArmorStandVanillaArmorModel(modelLoader.getModelPart(AntiqueEntityLayers.VANILLA_ARMOR));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorStandEntityRenderState state, float limbAngle, float limbDistance) {
        VertexConsumer helmetConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(texture), state.equippedHeadStack.hasGlint());
        VertexConsumer chestplateConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(texture), state.equippedChestStack.hasGlint());
        VertexConsumer leggingsConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(texture), state.equippedLegsStack.hasGlint());
        VertexConsumer bootsConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(texture), state.equippedFeetStack.hasGlint());

        this.getContextModel().copyTransforms(this.model);

        this.model.leggingsBody.copyTransform(this.getContextModel().body);
        this.model.rightBoot.copyTransform(this.getContextModel().rightLeg);
        this.model.leftBoot.copyTransform(this.getContextModel().leftLeg);

        this.model.setVisible(false);

        this.model.leggingsBody.visible = false;
        this.model.rightBoot.visible = false;
        this.model.leftBoot.visible = false;

        if (state.equippedHeadStack.isIn(tag)) {
            this.model.head.visible = true;
        }

        if (state.equippedChestStack.isIn(tag)) {
            this.model.body.visible = true;
            this.model.leftArm.visible = true;
            this.model.rightArm.visible = true;
        }

        if (state.equippedLegsStack.isIn(tag)) {
            this.model.leggingsBody.visible = true;
            this.model.leftLeg.visible = true;
            this.model.rightLeg.visible = true;
        }

        if (state.equippedFeetStack.isIn(tag)) {
            this.model.rightBoot.visible = true;
            this.model.leftBoot.visible = true;
        }

        this.model.head.render(matrices, helmetConsumer, light, OverlayTexture.DEFAULT_UV);

        this.model.body.render(matrices, chestplateConsumer, light, OverlayTexture.DEFAULT_UV);
        this.model.rightArm.render(matrices, chestplateConsumer, light, OverlayTexture.DEFAULT_UV);
        this.model.leftArm.render(matrices, chestplateConsumer, light, OverlayTexture.DEFAULT_UV);

        this.model.leggingsBody.render(matrices, leggingsConsumer, light, OverlayTexture.DEFAULT_UV);
        this.model.rightLeg.render(matrices, leggingsConsumer, light, OverlayTexture.DEFAULT_UV);
        this.model.leftLeg.render(matrices, leggingsConsumer, light, OverlayTexture.DEFAULT_UV);

        this.model.rightBoot.render(matrices, bootsConsumer, light, OverlayTexture.DEFAULT_UV);
        this.model.leftBoot.render(matrices, bootsConsumer, light, OverlayTexture.DEFAULT_UV);
    }
}
