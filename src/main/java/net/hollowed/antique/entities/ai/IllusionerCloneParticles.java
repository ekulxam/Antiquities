package net.hollowed.antique.entities.ai;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class IllusionerCloneParticles {
    public static void particles(World world, double x, double y, double z) {
        for (int i = 0; i < 10; i++) {
            world.addImportantParticleClient(
                    ParticleTypes.CLOUD,
                    x + Math.random() - 0.5, y, z + Math.random() - 0.5,
                    0,
                    0,
                    0
            );
        }
        world.playSoundClient(x, y, z, SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE, SoundCategory.HOSTILE, 1.0F, 1.0F, false);
    }
}
