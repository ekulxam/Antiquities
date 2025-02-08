package net.hollowed.antique.client.item.explosive_spear;

import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.joml.Vector3d;

import java.util.List;

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

    public Vector3d getPos() { return new Vector3d(pos); }

    public Vec3d slideOutOfBlocks(ClientWorld world) {
        double padding = 0.0001;

        Vec3d startPos = new Vec3d(pos.x, pos.y, pos.z);
        BlockPos blockPos = BlockPos.ofFloored(startPos);
        BlockState state = world.getBlockState(blockPos);

        // If there's no collision shape, just return the original point
        if (state.isAir()) return startPos;
        VoxelShape shape = state.getCollisionShape(world, blockPos);
        if (shape.isEmpty()) return startPos;

        // Convert the shape to world space
        VoxelShape worldShape = shape.offset(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        List<Box> collBoxes = worldShape.getBoundingBoxes();

        // We'll treat the point as an itty-bitty bounding box
        double x = startPos.x;
        double y = startPos.y;
        double z = startPos.z;

        // Build a small bounding box around the point
        Box pointBox = new Box(x - padding, y - padding, z - padding, x + padding, y + padding, z + padding);

        // Try sliding out by checking overlaps
        double dx = 0, dy = 0, dz = 0;
        for (Box box : collBoxes) {
            if (box.intersects(pointBox)) {
                double xOverlap = getOverlap(pointBox.minX, pointBox.maxX, box.minX, box.maxX);
                double yOverlap = getOverlap(pointBox.minY, pointBox.maxY, box.minY, box.maxY);
                double zOverlap = getOverlap(pointBox.minZ, pointBox.maxZ, box.minZ, box.maxZ);

                // Pick the smallest overlap to push out
                if (Math.abs(xOverlap) < Math.abs(yOverlap) && Math.abs(xOverlap) < Math.abs(zOverlap)) {
                    dx += xOverlap;
                } else if (Math.abs(yOverlap) < Math.abs(zOverlap)) {
                    dy += yOverlap;
                } else {
                    dz += zOverlap;
                }
            }
        }

        pos = new Vector3d(x + dx, y + dy, z + dz);
        return new Vec3d(x + dx, y + dy, z + dz);
    }

    // Helper to find overlap between two intervals
    private double getOverlap(double minA, double maxA, double minB, double maxB) {
        if (maxA <= minB || minA >= maxB) return 0.0;  // no overlap
        double push1 = maxB - minA;  // push forward
        double push2 = minB - maxA;  // push backward
        return Math.abs(push1) < Math.abs(push2) ? push1 : push2;
    }
}
