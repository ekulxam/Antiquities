package net.hollowed.antique.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MovementUtilsClass {

    public static Vec3 applyAxeClimbingSpeed(Vec3 motion, LivingEntity entity) {
        AABB box = entity.getBoundingBox();
        double offset = 0.35;

        boolean collidingWest = MovementUtilsClass.collidesWithSolidBlock(entity.level(), box.move(-offset, 0, 0), entity);
        boolean collidingEast = MovementUtilsClass.collidesWithSolidBlock(entity.level(), box.move(offset, 0, 0), entity);
        boolean collidingNorth = MovementUtilsClass.collidesWithSolidBlock(entity.level(), box.move(0, 0, -offset), entity);
        boolean collidingSouth = MovementUtilsClass.collidesWithSolidBlock(entity.level(), box.move(0, 0, offset), entity);

        AABB ledgeBox = box.setMaxY(box.maxY - 0.5);

        boolean ledgeWest = !MovementUtilsClass.collidesWithSolidBlock(entity.level(), ledgeBox.move(-offset, 1.75, 0), entity);
        boolean ledgeEast = !MovementUtilsClass.collidesWithSolidBlock(entity.level(), ledgeBox.move(offset, 1.75, 0), entity);
        boolean ledgeNorth = !MovementUtilsClass.collidesWithSolidBlock(entity.level(), ledgeBox.move(0, 1.75, -offset), entity);
        boolean ledgeSouth = !MovementUtilsClass.collidesWithSolidBlock(entity.level(), ledgeBox.move(0, 1.75, offset), entity);

        if (entity.onClimbable()) {
            entity.resetFallDistance();
            double d = Mth.clamp(motion.x, -1F, 1F);
            double e = Mth.clamp(motion.z, -1F, 1F);
            double g = Math.max(motion.y, -0.15F);
            if (g < 0.0 && !entity.getInBlockState().is(Blocks.SCAFFOLDING)) {
                entity.isSuppressingSlidingDownLadder();
            }
            if (entity.isShiftKeyDown()) {
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

            motion = new Vec3(d, g, e);
        }

        return motion;
    }

    public static boolean collidesWithSolidBlock(Level world, AABB box, Entity entity) {
        // Check for collision with any solid block in the given bounding box
        return world.getBlockCollisions(entity, box).iterator().hasNext();
    }
}
