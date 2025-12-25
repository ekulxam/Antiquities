package net.hollowed.antique.util;

import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MathUtils {
    public static Quaternionf vec3ToQuaternion(Vec3 normal) {
        Vector3f fromDir = new Vector3f(0, 1, 0);
        Vector3f toDir = normal.toVector3f();
        Quaternionf quaternion = new Quaternionf();
        quaternion.rotationTo(fromDir, toDir);
        return quaternion;
    }
}
