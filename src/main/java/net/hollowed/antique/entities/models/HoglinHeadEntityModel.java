package net.hollowed.antique.entities.models;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.entity.model.BoggedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;

@Environment(EnvType.CLIENT)
public class HoglinHeadEntityModel extends SkullBlockEntityModel {
	private final ModelPart head;

	public HoglinHeadEntityModel(ModelPart root) {
		super(root);
		this.head = root.getChild(EntityModelPartNames.HEAD);
	}

	public static ModelData getModelData() {
		return HoglinHeadEntityModel.getModelData();
	}

	@Override
	public void setHeadRotation(float animationProgress, float yaw, float pitch) {
		this.head.yaw = yaw * (float) (Math.PI / 180.0);
		this.head.pitch = pitch * (float) (Math.PI / 180.0);
	}
}
