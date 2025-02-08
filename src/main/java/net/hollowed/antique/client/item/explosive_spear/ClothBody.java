package net.hollowed.antique.client.item.explosive_spear;

import org.joml.Vector3d;

public class ClothBody {

    Vector3d pos = new Vector3d();
    Vector3d posCache = new Vector3d();
    Vector3d accel = new Vector3d();

    public ClothBody(Vector3d worldPos) {
        pos = new Vector3d(worldPos);
        posCache = new Vector3d(worldPos);
    }

    public ClothBody(Vector3d worldPos, Vector3d worldVel) {
        pos = new Vector3d(worldPos);
        posCache = pos.sub(new Vector3d(worldVel));
    }

    public void update(double delta) {
        Vector3d vel = pos.sub(posCache, new Vector3d());
        posCache = new Vector3d(pos);

        var dAccel = accel.mul(delta * delta, new Vector3d());
        pos.add(vel.add(dAccel, new Vector3d()));
        accel = new Vector3d();
    }

    public void containDistance(ClothBody other, double distance) {

        Vector3d axis = pos.sub(other.pos, new Vector3d());
        double dist = axis.length();
        Vector3d norm = axis.div(dist, new Vector3d());
        double delta = distance - dist;
        pos.add(norm.mul(0.5 * delta, new Vector3d()));
        other.pos.sub(norm.mul(0.5 * delta, new Vector3d()));
    }

    private Vector3d reflectVelocity(Vector3d velocity, Vector3d normal){
        Vector3d normalizedNormal = normal.normalize();
        return velocity.sub(normalizedNormal.mul(velocity.dot(normalizedNormal), new Vector3d()));
    }

    public Vector3d getPos() { return new Vector3d(pos); }
}
