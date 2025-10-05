package net.hollowed.antique.client.armor.renderers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.client.armor.models.VanillaArmorModel;
import net.hollowed.antique.index.AntiqueEntityLayers;
import net.hollowed.antique.index.AntiqueItemTags;
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
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VanillaArmorFeatureRenderer<S extends BipedEntityRenderState, M extends BipedEntityModel<S>> extends FeatureRenderer<S, M> {

    private final Identifier texture;
    private final VanillaArmorModel<S> model;
    private final TagKey<Item> tag;

    public VanillaArmorFeatureRenderer(FeatureRendererContext<S, M> context, int id, LoadedEntityModels modelLoader) {
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
        this.model = new VanillaArmorModel<>(modelLoader.getModelPart(AntiqueEntityLayers.VANILLA_ARMOR));
    }

    @Override
    public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, S state, float limbAngle, float limbDistance) {

        this.model.head.setTransform(this.getContextModel().head.getTransform());
        this.model.body.setTransform(this.getContextModel().body.getTransform());
        this.model.rightArm.setTransform(this.getContextModel().rightArm.getTransform());
        this.model.leftArm.setTransform(this.getContextModel().leftArm.getTransform());
        this.model.rightLeg.setTransform(this.getContextModel().rightLeg.getTransform());
        this.model.leftLeg.setTransform(this.getContextModel().leftLeg.getTransform());
        this.model.leggingsBody.setTransform(this.getContextModel().body.getTransform());
        this.model.rightBoot.setTransform(this.getContextModel().rightLeg.getTransform());
        this.model.leftBoot.setTransform(this.getContextModel().leftLeg.getTransform());

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

        if (state instanceof IsWitherGetter access && access.antiquities$getWither()) {
            this.model.rightArm.setTransform(this.getContextModel().rightArm.getTransform().moveOrigin(-0.375F, -4.625F, 0));
            this.model.leftArm.setTransform(this.getContextModel().leftArm.getTransform().moveOrigin(0.375F, -4.625F, 0));
            this.model.body.setTransform(this.getContextModel().body.getTransform().moveOrigin(0, -4.625F, 0).scaled(1.1F));
            this.model.leggingsBody.setTransform(this.getContextModel().body.getTransform().moveOrigin(0, -4.625F, 0).scaled(1.1F));
            this.model.head.setTransform(this.getContextModel().head.getTransform().moveOrigin(0, -5.625F, 0));
            this.model.rightLeg.setTransform(this.getContextModel().rightLeg.getTransform().moveOrigin(0, -4.375F, 0).scaled(1.1F));
            this.model.leftLeg.setTransform(this.getContextModel().leftLeg.getTransform().moveOrigin(0, -4.375F, 0).scaled(1.1F));
            this.model.rightBoot.setTransform(this.getContextModel().rightLeg.getTransform().moveOrigin(0, -1.625F, 0).scaled(1.1F));
            this.model.leftBoot.setTransform(this.getContextModel().leftLeg.getTransform().moveOrigin(0, -1.625F, 0).scaled(1.1F));
        }

        if (state instanceof IsHuskGetter access && access.antiquities$getHusk()) {
            this.model.rightArm.setTransform(this.getContextModel().rightArm.getTransform().moveOrigin(-0.375F, -1.375F, 0));
            this.model.leftArm.setTransform(this.getContextModel().leftArm.getTransform().moveOrigin(0.375F, -1.375F, 0));
            this.model.body.setTransform(this.getContextModel().body.getTransform().moveOrigin(0, -1.375F, 0));
            this.model.head.setTransform(this.getContextModel().head.getTransform().moveOrigin(0, -2.375F, 0).scaled(1.01F));
            this.model.rightLeg.setTransform(this.getContextModel().rightLeg.getTransform().moveOrigin(0, -2.375F, 0).scaled(1.1F));
            this.model.leftLeg.setTransform(this.getContextModel().leftLeg.getTransform().moveOrigin(0, -2.375F, 0).scaled(1.1F));
            this.model.rightBoot.setTransform(this.getContextModel().rightLeg.getTransform().moveOrigin(0, -1.625F, 0).scaled(1.1F));
            this.model.leftBoot.setTransform(this.getContextModel().leftLeg.getTransform().moveOrigin(0, -1.625F, 0).scaled(1.1F));
        }

        queue.getBatchingQueue(1).submitModel(
                this.model,
                state,
                matrices,
                RenderLayer.getArmorCutoutNoCull(texture),
                light,
                OverlayTexture.DEFAULT_UV,
                state.outlineColor,
                null
        );

//        this.model.head.render(matrices, helmetConsumer, light, OverlayTexture.DEFAULT_UV);
//
//        this.model.body.render(matrices, chestplateConsumer, light, OverlayTexture.DEFAULT_UV);
//        this.model.rightArm.render(matrices, chestplateConsumer, light, OverlayTexture.DEFAULT_UV);
//        this.model.leftArm.render(matrices, chestplateConsumer, light, OverlayTexture.DEFAULT_UV);
//
//        this.model.leggingsBody.render(matrices, leggingsConsumer, light, OverlayTexture.DEFAULT_UV);
//        this.model.rightLeg.render(matrices, leggingsConsumer, light, OverlayTexture.DEFAULT_UV);
//        this.model.leftLeg.render(matrices, leggingsConsumer, light, OverlayTexture.DEFAULT_UV);
//
//        this.model.rightBoot.render(matrices, bootsConsumer, light, OverlayTexture.DEFAULT_UV);
//        this.model.leftBoot.render(matrices, bootsConsumer, light, OverlayTexture.DEFAULT_UV);
    }
}
