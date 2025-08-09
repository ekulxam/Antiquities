package net.hollowed.antique.client.armor.models;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;

public class ArmorStandVanillaArmorModel extends BipedEntityModel<ArmorStandEntityRenderState> {

    public final ModelPart rightBoot;
    public final ModelPart leftBoot;
    public final ModelPart leggingsBody;

	public ArmorStandVanillaArmorModel(ModelPart root) {
        super(root);
        this.rightBoot = root.getChild("right_boot");
        this.leftBoot = root.getChild("left_boot");
        this.leggingsBody = root.getChild("leggings_body");
	}
}