package net.hollowed.antique.client.armor.renderers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.index.AntiqueEntityLayers;
import net.hollowed.antique.client.armor.models.AdventureArmor;
import net.hollowed.antique.index.AntiqueItems;
import net.hollowed.antique.util.interfaces.duck.IsHuskGetter;
import net.hollowed.antique.util.interfaces.duck.IsWitherGetter;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AdventureArmorFeatureRenderer<S extends BipedEntityRenderState, M extends BipedEntityModel<S>> extends FeatureRenderer<S, M> {

    private final boolean slim;

    private static final Identifier TEXTURE = Identifier.of(Antiquities.MOD_ID, "textures/entity/adventure_armor.png");
    private static final Identifier THICK_TEXTURE = Identifier.of(Antiquities.MOD_ID, "textures/entity/adventure_armor_thick.png");
    private final AdventureArmor<S> model;

    public AdventureArmorFeatureRenderer(FeatureRendererContext<S, M> context, LoadedEntityModels modelLoader, boolean slim) {
        super(context);
        this.slim = slim;
        this.model = new AdventureArmor<>(modelLoader.getModelPart(AntiqueEntityLayers.ADVENTURE_ARMOR));
    }

    @Override
    public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, S state, float limbAngle, float limbDistance) {

        this.model.head.setTransform(this.getContextModel().head.getTransform());
        this.model.body.setTransform(this.getContextModel().body.getTransform());
        this.model.rightArm.setTransform(this.getContextModel().rightArm.getTransform());
        this.model.leftArm.setTransform(this.getContextModel().leftArm.getTransform());
        this.model.rightLeg.setTransform(this.getContextModel().rightLeg.getTransform());
        this.model.leftLeg.setTransform(this.getContextModel().leftLeg.getTransform());
        this.model.satchel.setTransform(this.getContextModel().body.getTransform());

        this.model.rightArmThick.setTransform(this.getContextModel().rightArm.getTransform());
        this.model.leftArmThick.setTransform(this.getContextModel().leftArm.getTransform());

        this.model.setVisible(false);
        this.model.leftArmThick.visible = false;
        this.model.rightArmThick.visible = false;

        if (state.equippedChestStack.getItem() == AntiqueItems.NETHERITE_PAULDRONS) {
            this.model.body.visible = true;
            if (this.slim) {
                this.model.leftArm.visible = true;
                this.model.rightArm.visible = true;
            } else {
                this.model.rightArmThick.visible = true;
                this.model.leftArmThick.visible = true;
            }
        }
        this.model.satchel.visible = state.equippedLegsStack.getItem() == AntiqueItems.SATCHEL;

        if (state.equippedFeetStack.getItem() == AntiqueItems.FUR_BOOTS) {
            this.model.rightLeg.visible = true;
            this.model.leftLeg.visible = true;
        }

        if (this.slim) {
            if (state instanceof IsWitherGetter access && access.antiquities$getWither()) {
                this.model.rightArm.setTransform(this.getContextModel().rightArm.getTransform().moveOrigin(-0.375F, -4.625F, 0));
                this.model.leftArm.setTransform(this.getContextModel().leftArm.getTransform().moveOrigin(0.375F, -4.625F, 0));
                this.model.body.setTransform(this.getContextModel().body.getTransform().moveOrigin(0, -4.625F, 0).scaled(1.1F));
                this.model.satchel.setTransform(this.getContextModel().body.getTransform().moveOrigin(0, -4.625F, 0).scaled(1.1F));
                this.model.rightLeg.setTransform(this.getContextModel().rightLeg.getTransform().moveOrigin(0, -1.625F, 0).scaled(1.1F));
                this.model.leftLeg.setTransform(this.getContextModel().leftLeg.getTransform().moveOrigin(0, -1.625F, 0).scaled(1.1F));
            }

            queue.getBatchingQueue(1).submitModel(
                    this.model,
                    state,
                    matrices,
                    RenderLayer.getArmorCutoutNoCull(TEXTURE),
                    light,
                    OverlayTexture.DEFAULT_UV,
                    state.outlineColor,
                    null
            );
        } else {
            if (state instanceof IsHuskGetter access && access.antiquities$getHusk()) {
                this.model.rightArmThick.setTransform(this.getContextModel().rightArm.getTransform().moveOrigin(-0.375F, -1.375F, 0));
                this.model.leftArmThick.setTransform(this.getContextModel().leftArm.getTransform().moveOrigin(0.375F, -1.375F, 0));
                this.model.body.setTransform(this.getContextModel().body.getTransform().moveOrigin(0, -1.375F, 0));
                this.model.satchel.setTransform(this.getContextModel().body.getTransform().moveOrigin(0, -1.375F, 0));
                this.model.rightLeg.setTransform(this.getContextModel().rightLeg.getTransform().moveOrigin(0, -1.625F, 0).scaled(1.1F));
                this.model.leftLeg.setTransform(this.getContextModel().leftLeg.getTransform().moveOrigin(0, -1.625F, 0).scaled(1.1F));
            }

            queue.getBatchingQueue(1).submitModel(
                    this.model,
                    state,
                    matrices,
                    RenderLayer.getArmorCutoutNoCull(THICK_TEXTURE),
                    light,
                    OverlayTexture.DEFAULT_UV,
                    state.outlineColor,
                    null
            );
        }

//        if (this.slim) {
//            VertexConsumer chestConsumers = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(TEXTURE),
//                    state.equippedChestStack.hasGlint());
//            matrices.push();
//            if (state instanceof IsWitherGetter access && access.antiquities$getWither()) matrices.scale(1.2F, 1.2F, 1.2F);
//            this.model.body.render(matrices, chestConsumers, light, LivingEntityRenderer.getOverlay(state, 0.0F));
//            matrices.pop();
//            this.model.rightArm.render(matrices, chestConsumers, light, LivingEntityRenderer.getOverlay(state, 0.0F));
//            this.model.leftArm.render(matrices, chestConsumers, light, LivingEntityRenderer.getOverlay(state, 0.0F));
//
//            VertexConsumer satchelConsumers = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(TEXTURE),
//                    state.equippedLegsStack.hasGlint());
//            this.model.satchel.render(matrices, satchelConsumers, light, LivingEntityRenderer.getOverlay(state, 0.0F));
//
//            VertexConsumer bootsConsumers = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(TEXTURE),
//                    state.equippedFeetStack.hasGlint());
//            this.model.rightLeg.render(matrices, bootsConsumers, light, LivingEntityRenderer.getOverlay(state, 0.0F));
//            this.model.leftLeg.render(matrices, bootsConsumers, light, LivingEntityRenderer.getOverlay(state, 0.0F));
//        } else {
//            VertexConsumer chestConsumers = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(THICK_TEXTURE),
//                    state.equippedChestStack.hasGlint());
//            matrices.push();
//            if (state instanceof IsHuskGetter access && access.antiquities$getHusk()) matrices.translate(0, -0.1, 0);
//            this.model.body.render(matrices, chestConsumers, light, LivingEntityRenderer.getOverlay(state, 0.0F));
//            matrices.pop();
//            matrices.push();
//            if (state instanceof IsHuskGetter access && access.antiquities$getHusk()) matrices.translate(-0.05, -0.1, 0);
//            this.model.rightArmThick.render(matrices, chestConsumers, light, LivingEntityRenderer.getOverlay(state, 0.0F));
//            matrices.pop();
//            matrices.push();
//            if (state instanceof IsHuskGetter access && access.antiquities$getHusk()) matrices.translate(0.05, -0.1, 0);
//            this.model.leftArmThick.render(matrices, chestConsumers, light, LivingEntityRenderer.getOverlay(state, 0.0F));
//            matrices.pop();
//
//            VertexConsumer satchelConsumers = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(THICK_TEXTURE),
//                    state.equippedLegsStack.hasGlint());
//            matrices.push();
//            if (state instanceof ZombieEntityRenderState) matrices.translate(0, -0.1, 0);
//            this.model.satchel.render(matrices, satchelConsumers, light, LivingEntityRenderer.getOverlay(state, 0.0F));
//            matrices.pop();
//
//            VertexConsumer bootsConsumers = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(THICK_TEXTURE),
//                    state.equippedFeetStack.hasGlint());
//            matrices.push();
//            if (state instanceof ZombieEntityRenderState) {
//                matrices.scale(1.1F, 1.1F, 1.1F);
//                matrices.translate(0, -0.1, 0);
//            }
//            this.model.rightLeg.render(matrices, bootsConsumers, light, LivingEntityRenderer.getOverlay(state, 0.0F));
//            this.model.leftLeg.render(matrices, bootsConsumers, light, LivingEntityRenderer.getOverlay(state, 0.0F));
//            matrices.pop();
//        }
    }
}
