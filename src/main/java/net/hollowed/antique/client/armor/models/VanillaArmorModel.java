package net.hollowed.antique.client.armor.models;

import net.hollowed.antique.AntiquitiesClient;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

public class VanillaArmorModel<S extends HumanoidRenderState> extends HumanoidModel<S> {

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
        this.armorHead = root.getChild(PartNames.HEAD).getChild("armor_head");
        this.armorBody = root.getChild(PartNames.BODY).getChild("armor_body");
        this.armorRightArm = root.getChild(PartNames.RIGHT_ARM).getChild("armor_right_arm");
        this.armorLeftArm = root.getChild(PartNames.LEFT_ARM).getChild("armor_left_arm");
        this.armorRightLeg = root.getChild(PartNames.RIGHT_LEG).getChild("armor_right_leg");
        this.armorLeftLeg = root.getChild(PartNames.LEFT_LEG).getChild("armor_left_leg");
        this.rightBoot = root.getChild(PartNames.RIGHT_LEG).getChild("right_boot");
        this.leftBoot = root.getChild(PartNames.LEFT_LEG).getChild("left_boot");
        this.leggingsBody = root.getChild(PartNames.BODY).getChild("leggings_body");
	}

	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
		PartDefinition modelPartData = modelData.getRoot();

        PartDefinition head = modelPartData.addOrReplaceChild(PartNames.HEAD, CubeListBuilder.create(), PartPose.ZERO);
        head.addOrReplaceChild(PartNames.HAT, CubeListBuilder.create(), PartPose.ZERO);

        PartDefinition body = modelPartData.addOrReplaceChild(PartNames.BODY, CubeListBuilder.create(), PartPose.ZERO);
        PartDefinition rightArm = modelPartData.addOrReplaceChild(PartNames.RIGHT_ARM, CubeListBuilder.create(), PartPose.offset(-5, 2, 0));
        PartDefinition leftArm = modelPartData.addOrReplaceChild(PartNames.LEFT_ARM, CubeListBuilder.create(), PartPose.offset(5, 2, 0));
        PartDefinition rightLeg = modelPartData.addOrReplaceChild(PartNames.RIGHT_LEG, CubeListBuilder.create(), PartPose.offset(-1.9F, 12, 0));
        PartDefinition leftLeg = modelPartData.addOrReplaceChild(PartNames.LEFT_LEG, CubeListBuilder.create(), PartPose.offset(1.9F, 12, 0));

        head.addOrReplaceChild("armor_head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.27F))
                .texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
                .texOffs(0, 32).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        body.addOrReplaceChild("armor_body", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.27F))
                .texOffs(32, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.02F))
                .texOffs(32, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        rightArm.addOrReplaceChild("armor_right_arm", CubeListBuilder.create().texOffs(56, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.27F))
                .texOffs(16, 64).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.03F))
                .texOffs(72, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.4F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        leftArm.addOrReplaceChild("armor_left_arm", CubeListBuilder.create().texOffs(0, 64).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.27F))
                .texOffs(32, 64).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.03F))
                .texOffs(72, 16).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.4F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        rightLeg.addOrReplaceChild("armor_right_leg", CubeListBuilder.create().texOffs(0, 80).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.27F))
                .texOffs(16, 80).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F))
                .texOffs(32, 80).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        leftLeg.addOrReplaceChild("armor_left_leg", CubeListBuilder.create().texOffs(48, 80).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.27F))
                .texOffs(64, 80).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F))
                .texOffs(80, 64).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        body.addOrReplaceChild("leggings_body", CubeListBuilder.create().texOffs(0, 48).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.28F))
                .texOffs(24, 48).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.51F))
                .texOffs(48, 48).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        rightLeg.addOrReplaceChild("right_boot", CubeListBuilder.create().texOffs(56, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.27F))
                .texOffs(64, 64).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.49F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        leftLeg.addOrReplaceChild("left_boot", CubeListBuilder.create().texOffs(56, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.27F))
                .texOffs(48, 64).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.49F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(modelData, 128, 128);
	}

    @Override
    public void setupAnim(S state) {
        this.head.visible = true;
        this.body.visible = true;
        this.rightArm.visible = true;
        this.leftArm.visible = true;
        this.rightLeg.visible = true;
        this.leftLeg.visible = true;

        this.armorHead.visible = listContains(state.headEquipment);
        this.armorBody.visible = this.armorRightArm.visible = this.armorLeftArm.visible = listContains(state.chestEquipment);
        this.armorRightLeg.visible = this.armorLeftLeg.visible = this.leggingsBody.visible = listContains(state.legsEquipment);
        this.rightBoot.visible = this.leftBoot.visible = listContains(state.feetEquipment);
    }

    private boolean listContains(ItemStack stack) {
        return AntiquitiesClient.BETTER_ARMOR_LIST.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }
}