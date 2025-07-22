package net.hollowed.antique.client.poses;

import net.minecraft.client.model.ModelPart;

public class MyriadAxePosing {
    public static void hold(ModelPart holdingArm, ModelPart otherArm, ModelPart head, boolean rightArmed) {
        ModelPart modelPart = rightArmed ? holdingArm : otherArm;
        ModelPart modelPart2 = rightArmed ? otherArm : holdingArm;
        modelPart.pitch = Math.min(head.pitch - ((float) Math.PI / 2.0F), -((float) Math.PI / 2.0F) + 0.77F);
        modelPart2.pitch = Math.min(head.pitch - ((float) Math.PI / 2.0F), -((float) Math.PI / 2.0F) + 0.77F);
    }
}
