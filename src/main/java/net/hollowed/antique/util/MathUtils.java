package net.hollowed.antique.util;

import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MathUtils {
    public static Quaternionf vec3ToQuaternion(Vec3d normal) {
        Vector3f fromDir = new Vector3f(0, 1, 0);
        Vector3f toDir = normal.toVector3f();
        Quaternionf quaternion = new Quaternionf();
        quaternion.rotationTo(fromDir, toDir);
        return quaternion;
    }
}
