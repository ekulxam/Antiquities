package net.hollowed.antique.client.armor.renderers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.index.AntiqueEntityLayers;
import net.hollowed.antique.client.armor.models.ArmorStandAdventureArmor;
import net.hollowed.antique.index.AntiqueItems;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AdventureArmorArmorStandFeatureRenderer extends FeatureRenderer<ArmorStandEntityRenderState, ArmorStandEntityModel> {

    private static final Identifier TEXTURE = Identifier.of(Antiquities.MOD_ID, "textures/entity/adventure_armor.png");
    private final ArmorStandAdventureArmor model;

    public AdventureArmorArmorStandFeatureRenderer(FeatureRendererContext<ArmorStandEntityRenderState, ArmorStandEntityModel> context, LoadedEntityModels modelLoader) {
        super(context);
        this.model = new ArmorStandAdventureArmor(modelLoader.getModelPart(AntiqueEntityLayers.ARMOR_STAND_ADVENTURE_ARMOR));
    }

    @Override
    public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, ArmorStandEntityRenderState state, float limbAngle, float limbDistance) {

        this.model.head.setTransform(this.getContextModel().head.getTransform());
        this.model.body.setTransform(this.getContextModel().body.getTransform());
        this.model.rightArm.setTransform(this.getContextModel().rightArm.getTransform());
        this.model.leftArm.setTransform(this.getContextModel().leftArm.getTransform());
        this.model.rightLeg.setTransform(this.getContextModel().rightLeg.getTransform());
        this.model.leftLeg.setTransform(this.getContextModel().leftLeg.getTransform());
        this.model.satchel.setTransform(this.getContextModel().body.getTransform());

        this.model.setVisible(false);
        this.model.leftArmThick.visible = false;
        this.model.rightArmThick.visible = false;

        if (state.equippedChestStack.getItem() == AntiqueItems.NETHERITE_PAULDRONS) {
            this.model.rightArm.visible = true;
            this.model.leftArm.visible = true;
            this.model.body.visible = true;
        }
        this.model.satchel.visible = state.equippedLegsStack.getItem() == AntiqueItems.SATCHEL;

        if (state.equippedFeetStack.getItem() == AntiqueItems.FUR_BOOTS) {
            this.model.rightLeg.visible = true;
            this.model.leftLeg.visible = true;
        }

        queue.submitModel(
                this.model,
                state,
                matrices,
                RenderLayer.getArmorCutoutNoCull(TEXTURE),
                light,
                OverlayTexture.DEFAULT_UV,
                state.outlineColor,
                null
        );

//        queue.submitCustom(matrices, RenderLayer.getArmorCutoutNoCull(TEXTURE), ((matricesEntry, vertexConsumer) -> {
//            this.model.body.render(matrices, vertexConsumer, light, LivingEntityRenderer.getOverlay(state, 0.0F));
//            this.model.rightArm.render(matrices, vertexConsumer, light, LivingEntityRenderer.getOverlay(state, 0.0F));
//            this.model.leftArm.render(matrices, vertexConsumer, light, LivingEntityRenderer.getOverlay(state, 0.0F));
//        }));

//        VertexConsumer satchelConsumers = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(TEXTURE),
//                state.equippedLegsStack.hasGlint());
//        this.model.satchel.render(matrices, satchelConsumers, light, LivingEntityRenderer.getOverlay(state, 0.0F));
//
//        VertexConsumer bootsConsumers = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(TEXTURE),
//                state.equippedFeetStack.hasGlint());
//        this.model.rightLeg.render(matrices, bootsConsumers, light, LivingEntityRenderer.getOverlay(state, 0.0F));
//        this.model.leftLeg.render(matrices, bootsConsumers, light, LivingEntityRenderer.getOverlay(state, 0.0F));
    }
}
