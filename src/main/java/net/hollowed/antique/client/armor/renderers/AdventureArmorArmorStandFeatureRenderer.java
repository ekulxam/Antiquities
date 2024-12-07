package net.hollowed.antique.client.armor.renderers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.client.ModEntityLayers;
import net.hollowed.antique.client.armor.models.ArmorStandAdventureArmor;
import net.hollowed.antique.items.ModItems;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
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
        this.model = new ArmorStandAdventureArmor(modelLoader.getModelPart(ModEntityLayers.ADVENTURE_ARMOR));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorStandEntityRenderState state, float limbAngle, float limbDistance) {
        // Copy the player's model transforms to the armor model
        this.getContextModel().copyTransforms(this.model);

        // Link the armor parts to the correct body parts
        this.model.head.copyTransform(this.getContextModel().head);
        this.model.body.copyTransform(this.getContextModel().body);
        this.model.rightArm.copyTransform(this.getContextModel().rightArm);
        this.model.leftArm.copyTransform(this.getContextModel().leftArm);
        this.model.rightLeg.copyTransform(this.getContextModel().rightLeg);
        this.model.leftLeg.copyTransform(this.getContextModel().leftLeg);
        this.model.satchel.copyTransform(this.getContextModel().body);

        this.model.setVisible(false);
        this.model.leftArmThick.visible = false;
        this.model.rightArmThick.visible = false;

        if (state.equippedChestStack.getItem() == ModItems.NETHERITE_PAULDRONS) {
            this.model.rightArm.visible = true;
            this.model.leftArm.visible = true;
            this.model.body.visible = true;
        }
        this.model.satchel.visible = state.equippedLegsStack.getItem() == ModItems.SATCHEL;

        if (state.equippedFeetStack.getItem() == ModItems.FUR_BOOTS) {
            this.model.rightLeg.visible = true;
            this.model.leftLeg.visible = true;
        }

        // Render the armor
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(TEXTURE));
        this.model.render(matrices, vertexConsumer, light, LivingEntityRenderer.getOverlay(state, 0.0F));
    }
}
