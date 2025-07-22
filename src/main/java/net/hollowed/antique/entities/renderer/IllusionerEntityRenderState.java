package net.hollowed.antique.entities.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.IllagerEntityRenderState;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class IllusionerEntityRenderState extends IllagerEntityRenderState {
    public Vec3d[] mirrorCopyOffsets = new Vec3d[0];
    public boolean spellcasting;

    public IllusionerEntityRenderState() {
    }
}
