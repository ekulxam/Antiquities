package net.hollowed.antique.client.poses;

import net.minecraft.client.model.geom.ModelPart;

public class MyriadAxePosing {
    public static void hold(ModelPart holdingArm, ModelPart otherArm, ModelPart head, boolean rightArmed) {
        ModelPart modelPart = rightArmed ? holdingArm : otherArm;
        ModelPart modelPart2 = rightArmed ? otherArm : holdingArm;
        modelPart.xRot = Math.min(head.xRot - ((float) Math.PI / 2.0F), -((float) Math.PI / 2.0F) + 0.77F);
        modelPart2.xRot = Math.min(head.xRot - ((float) Math.PI / 2.0F), -((float) Math.PI / 2.0F) + 0.77F);
    }
}
