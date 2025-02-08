package net.hollowed.antique.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hollowed.antique.particles.ModParticles;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Unique;

public class WallJumpPacketReceiver {
    public static void registerServerPacket() {
        ServerPlayNetworking.registerGlobalReceiver(WallJumpPacketPayload.ID, (payload, context) -> context.server().execute(() -> {
            Entity entity = context.player().getWorld().getEntityById(payload.entityId());

            if (entity != null) {
                entity.getWorld().playSound(null, entity.getBlockPos(), SoundEvents.ENTITY_GOAT_LONG_JUMP, SoundCategory.PLAYERS, 1.0F, 1.0F);

                // Create a push vector that moves the player away from the wall
                Vec3d pushVector = new Vec3d(0, 0, 0); // Apply horizontal push and vertical boost

                // Determine the direction of the collided wall and add velocity
                Box box = entity.getBoundingBox();
                double offset = 1;

                // Check in each cardinal direction for solid blocks
                boolean collidingWest = collidesWithSolidBlock(entity.getWorld(), box.offset(-offset, 0, 0), entity);
                boolean collidingEast = collidesWithSolidBlock(entity.getWorld(), box.offset(offset, 0, 0), entity);
                boolean collidingNorth = collidesWithSolidBlock(entity.getWorld(), box.offset(0, 0, -offset), entity);
                boolean collidingSouth = collidesWithSolidBlock(entity.getWorld(), box.offset(0, 0, offset), entity);

                double x = entity.getX();
                double z = entity.getZ();

                double particleX = entity.getX();
                double particleZ = entity.getZ();

                // Apply velocity in the opposite direction of the collision
                if (collidingWest) {
                    pushVector = pushVector.add(10, 0, 0); // Push east
                    x -= 0.15;
                    particleX += 0.1;
                }
                if (collidingEast) {
                    pushVector = pushVector.add(-10, 0, 0); // Push west
                    x += 0.15;
                    particleX -= 0.1;
                }
                if (collidingNorth) {
                    pushVector = pushVector.add(0, 0, 10); // Push south
                    z -= 0.15;
                    particleZ += 0.1;
                }
                if (collidingSouth) {
                    pushVector = pushVector.add(0, 0, -10); // Push north
                    z += 0.15;
                    particleZ -= 0.1;
                }

                if (entity instanceof ServerPlayerEntity serverPlayer) {
                    for (ServerPlayerEntity playerEntity : serverPlayer.getServerWorld().getPlayers().stream().toList()) {
                        ServerPlayNetworking.send(playerEntity, new WallJumpParticlePacketPayload((float) x, (float) entity.getY(), (float) z, (float) particleX, (float) particleZ, pushVector));
                    }
                }
            }
        }));
    }

    @Unique
    private static boolean collidesWithSolidBlock(World world, Box box, Entity entity) {
        // Check for collision with any solid block in the given bounding box
        return world.getBlockCollisions(entity, box).iterator().hasNext();
    }

    public static void particles(World world, float x, float y, float z, float particleX, float particleZ, Vec3d pushVector) {
        for (int i = 0; i < 10; i++) {
            world.addImportantParticle(
                    ModParticles.DUST_PARTICLE,
                    particleX + Math.random() * 0.5 - 0.25,
                    y + 0.5 + Math.random() * 0.5 - 0.5,
                    particleZ + Math.random() * 0.5 - 0.25,
                    pushVector.x * 0.0025,
                    pushVector.y * Math.random() * 0.05,
                    pushVector.z * 0.0025
            );
        }

        world.addImportantParticle(ModParticles.SPARKLE_PARTICLE, x, y + 0.5, z, pushVector.x, 0, pushVector.z);
    }
}
