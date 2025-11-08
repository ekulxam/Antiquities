package net.hollowed.antique.client.armor.models;

import net.hollowed.antique.AntiquitiesClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

public class VanillaArmorModel<S extends BipedEntityRenderState> extends BipedEntityModel<S> {

    public final ModelPart armorHead;
    public final ModelPart armorBody;
    public final ModelPart armorRightArm;
    public final ModelPart armorLeftArm;
    public final ModelPart armorRightLeg;
    public final ModelPart armorLeftLeg;
    public final ModelPart rightBoot;
    public final ModelPart leftBoot;
    public final ModelPart leggingsBody;

	public VanillaArmorModel(ModelPart root) {
        super(root);
        this.armorHead = root.getChild(EntityModelPartNames.HEAD).getChild("armor_head");
        this.armorBody = root.getChild(EntityModelPartNames.BODY).getChild("armor_body");
        this.armorRightArm = root.getChild(EntityModelPartNames.RIGHT_ARM).getChild("armor_right_arm");
        this.armorLeftArm = root.getChild(EntityModelPartNames.LEFT_ARM).getChild("armor_left_arm");
        this.armorRightLeg = root.getChild(EntityModelPartNames.RIGHT_LEG).getChild("armor_right_leg");
        this.armorLeftLeg = root.getChild(EntityModelPartNames.LEFT_LEG).getChild("armor_left_leg");
        this.rightBoot = root.getChild(EntityModelPartNames.RIGHT_LEG).getChild("right_boot");
        this.leftBoot = root.getChild(EntityModelPartNames.LEFT_LEG).getChild("left_boot");
        this.leggingsBody = root.getChild(EntityModelPartNames.BODY).getChild("leggings_body");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = BipedEntityModel.getModelData(Dilation.NONE, 0.0F);
		ModelPartData modelPartData = modelData.getRoot();

        ModelPartData head = modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create(), ModelTransform.NONE);
        head.addChild(EntityModelPartNames.HAT, ModelPartBuilder.create(), ModelTransform.NONE);

        ModelPartData body = modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create(), ModelTransform.NONE);
        ModelPartData rightArm = modelPartData.addChild(EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create(), ModelTransform.origin(-5, 2, 0));
        ModelPartData leftArm = modelPartData.addChild(EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create(), ModelTransform.origin(5, 2, 0));
        ModelPartData rightLeg = modelPartData.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create(), ModelTransform.origin(-1.9F, 12, 0));
        ModelPartData leftLeg = modelPartData.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create(), ModelTransform.origin(1.9F, 12, 0));

        head.addChild("armor_head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.27F))
                .uv(0, 16).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(1.0F))
                .uv(0, 32).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(1.5F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        body.addChild("armor_body", ModelPartBuilder.create().uv(32, 0).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.27F))
                .uv(32, 16).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(1.02F))
                .uv(32, 32).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(1.5F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        rightArm.addChild("armor_right_arm", ModelPartBuilder.create().uv(56, 32).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.27F))
                .uv(16, 64).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(1.03F))
                .uv(72, 0).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(1.4F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        leftArm.addChild("armor_left_arm", ModelPartBuilder.create().uv(0, 64).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.27F))
                .uv(32, 64).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(1.03F))
                .uv(72, 16).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(1.4F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        rightLeg.addChild("armor_right_leg", ModelPartBuilder.create().uv(0, 80).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.27F))
                .uv(16, 80).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.5F))
                .uv(32, 80).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(1.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        leftLeg.addChild("armor_left_leg", ModelPartBuilder.create().uv(48, 80).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.27F))
                .uv(64, 80).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.5F))
                .uv(80, 64).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(1.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        body.addChild("leggings_body", ModelPartBuilder.create().uv(0, 48).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.28F))
                .uv(24, 48).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.51F))
                .uv(48, 48).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(1.01F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        rightLeg.addChild("right_boot", ModelPartBuilder.create().uv(56, 0).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.27F))
                .uv(64, 64).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.49F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        leftLeg.addChild("left_boot", ModelPartBuilder.create().uv(56, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.27F))
                .uv(48, 64).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.49F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        return TexturedModelData.of(modelData, 128, 128);
	}

    @Override
    public void setAngles(S state) {
        this.head.visible = true;
        this.body.visible = true;
        this.rightArm.visible = true;
        this.leftArm.visible = true;
        this.rightLeg.visible = true;
        this.leftLeg.visible = true;

        this.armorHead.visible = listContains(state.equippedHeadStack);
        this.armorBody.visible = this.armorRightArm.visible = this.armorLeftArm.visible = listContains(state.equippedChestStack);
        this.armorRightLeg.visible = this.armorLeftLeg.visible = this.leggingsBody.visible = listContains(state.equippedLegsStack);
        this.rightBoot.visible = this.leftBoot.visible = listContains(state.equippedFeetStack);
    }

    private boolean listContains(ItemStack stack) {
        return AntiquitiesClient.BETTER_ARMOR_LIST.contains(Registries.ITEM.getId(stack.getItem()));
    }
}