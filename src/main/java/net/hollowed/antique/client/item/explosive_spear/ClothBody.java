package net.hollowed.antique.client.item.explosive_spear;

import org.joml.Vector3d;

public class ClothBody {

    Vector3d pos;
    Vector3d vel = new Vector3d();
    Vector3d accel = new Vector3d();

    public ClothBody(Vector3d worldPos) {
        pos = new Vector3d(worldPos);
    }

    public ClothBody(Vector3d worldPos, Vector3d worldVel) {
        pos = new Vector3d(worldPos);
        vel = new Vector3d(worldVel);
    }

    public void update(double delta) {
        vel.add(accel.mul(delta));
        accel = new Vector3d();
        pos.add(vel.mul(delta));
    }

    public void containDistance(ClothBody other) {
        var MAX_DISTANCE = 0.1;

        if (pos.distance(other.pos) >= MAX_DISTANCE) {
            var dir = pos.sub(other.pos, new Vector3d());
            other.vel = reflectVelocity(other.vel, dir);

            var correction = dir.mul((MAX_DISTANCE - pos.distance(other.pos)), new Vector3d());
            other.pos.add(correction);
        }

        other.vel.mul(0.98);
    }

    private Vector3d reflectVelocity(Vector3d velocity, Vector3d normal){
        Vector3d normalizedNormal = normal.normalize();
        return velocity.sub(normalizedNormal.mul(velocity.dot(normalizedNormal), new Vector3d()));
    }

    public Vector3d getPos() { return new Vector3d(pos); }
}
