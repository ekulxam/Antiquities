package net.hollowed.antique.client.armor.models;

import net.hollowed.antique.index.AntiqueItems;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;

public class ArmorStandAdventureArmor extends ArmorStandEntityModel {
	public final ModelPart satchel;
	public final ModelPart rightArmArmorThick;
	public final ModelPart leftArmArmorThick;
	public final ModelPart rightArmArmor;
	public final ModelPart leftArmArmor;
	public final ModelPart realBody;
	public final ModelPart rightBoot;
	public final ModelPart leftBoot;
	private final ModelPart rightBodyStick;
	private final ModelPart leftBodyStick;
	private final ModelPart shoulderStick;
	private final ModelPart basePlate;

	public ArmorStandAdventureArmor(ModelPart root) {
		super(root);
		this.realBody = root.getChild(EntityModelPartNames.BODY).getChild("realBody");
		this.satchel = root.getChild(EntityModelPartNames.BODY).getChild("satchel");
		this.rightArmArmor = root.getChild(EntityModelPartNames.RIGHT_ARM).getChild("rightArmArmor");
		this.rightArmArmorThick = root.getChild(EntityModelPartNames.RIGHT_ARM).getChild("rightArmArmorThick");
		this.leftArmArmor = root.getChild(EntityModelPartNames.LEFT_ARM).getChild("leftArmArmor");
		this.leftArmArmorThick = root.getChild(EntityModelPartNames.LEFT_ARM).getChild("leftArmArmorThick");
		this.rightBoot = root.getChild(EntityModelPartNames.RIGHT_LEG).getChild("rightBoot");
		this.leftBoot = root.getChild(EntityModelPartNames.LEFT_LEG).getChild("leftBoot");

		this.rightBodyStick = root.getChild("right_body_stick");
		this.leftBodyStick = root.getChild("left_body_stick");
		this.shoulderStick = root.getChild("shoulder_stick");
		this.basePlate = root.getChild("base_plate");
	}

	public static TexturedModelData getTexturedModelData() {
		return AdventureArmor.getTexturedModelData();
	}

	@Override
	public void setAngles(ArmorStandEntityRenderState state) {
		super.setAngles(state);

		rightBodyStick.visible = false;
		leftBodyStick.visible = false;
		shoulderStick.visible = false;
		basePlate.visible = false;

		rightArmArmorThick.visible = leftArmArmorThick.visible = false;

		realBody.visible = rightArm.visible = leftArm.visible = state.equippedChestStack.isOf(AntiqueItems.NETHERITE_PAULDRONS);
		satchel.visible = state.equippedLegsStack.isOf(AntiqueItems.SATCHEL);
		rightBoot.visible = leftBoot.visible = state.equippedFeetStack.isOf(AntiqueItems.FUR_BOOTS);
	}
}
