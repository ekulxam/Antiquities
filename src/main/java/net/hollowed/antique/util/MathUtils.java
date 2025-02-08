package net.hollowed.antique.util;

import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MathUtils {
    public static Quaternionf vec3ToQuaternion(Vec3d normal) {
        // Normalize the vector
        Vector3f normalized = new Vector3f(normal.toVector3f()).normalize();

        // Default "up" vector
        Vector3f up = new Vector3f(0, 1, 0);

        // Compute axis of rotation using cross product
        Vector3f axis = up.cross(normalized, new Vector3f());

        // Handle edge case where the normal aligns with up or down
        if (axis.lengthSquared() == 0) {
            return normalized.y > 0 ? new Quaternionf().identity() : new Quaternionf().rotateX((float) Math.PI);
        }

        // Compute angle of rotation using dot product
        float angle = (float) Math.acos(up.dot(normalized));

        // Create quaternion from axis and angle
        return new Quaternionf().fromAxisAngleRad(axis.normalize(), angle);
    }
}
