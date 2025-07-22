package net.hollowed.antique.util;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MovementUtilsClass {

    public static Vec3d applyAxeClimbingSpeed(Vec3d motion, LivingEntity entity) {
        Box box = entity.getBoundingBox();
        double offset = 0.35;

        boolean collidingWest = MovementUtilsClass.collidesWithSolidBlock(entity.getWorld(), box.offset(-offset, 0, 0), entity);
        boolean collidingEast = MovementUtilsClass.collidesWithSolidBlock(entity.getWorld(), box.offset(offset, 0, 0), entity);
        boolean collidingNorth = MovementUtilsClass.collidesWithSolidBlock(entity.getWorld(), box.offset(0, 0, -offset), entity);
        boolean collidingSouth = MovementUtilsClass.collidesWithSolidBlock(entity.getWorld(), box.offset(0, 0, offset), entity);

        boolean ledgeWest = !MovementUtilsClass.collidesWithSolidBlock(entity.getWorld(), box.offset(-offset, 1.75, 0), entity);
        boolean ledgeEast = !MovementUtilsClass.collidesWithSolidBlock(entity.getWorld(), box.offset(offset, 1.75, 0), entity);
        boolean ledgeNorth = !MovementUtilsClass.collidesWithSolidBlock(entity.getWorld(), box.offset(0, 1.75, -offset), entity);
        boolean ledgeSouth = !MovementUtilsClass.collidesWithSolidBlock(entity.getWorld(), box.offset(0, 1.75, offset), entity);

        if (entity.isClimbing()) {
            entity.onLanding();
            double d = MathHelper.clamp(motion.x, -1F, 1F);
            double e = MathHelper.clamp(motion.z, -1F, 1F);
            double g = Math.max(motion.y, -0.15F);
            if (g < 0.0 && !entity.getBlockStateAtPos().isOf(Blocks.SCAFFOLDING)) {
                entity.isHoldingOntoLadder();
            }
            if (entity.isSneaking()) {
                g = Math.max(motion.y, -0.6F);
                if (collidingWest && ledgeWest) {
                    g = Math.max(motion.y, 0.01F);
                }
                if (collidingEast && ledgeEast) {
                    g = Math.max(motion.y, 0.01F);
                }
                if (collidingNorth && ledgeNorth) {
                    g = Math.max(motion.y, 0.01F);
                }
                if (collidingSouth && ledgeSouth) {
                    g = Math.max(motion.y, 0.01F);
                }
            }

            motion = new Vec3d(d, g, e);
        }

        return motion;
    }

    public static boolean collidesWithSolidBlock(World world, Box box, Entity entity) {
        // Check for collision with any solid block in the given bounding box
        return world.getBlockCollisions(entity, box).iterator().hasNext();
    }
}
