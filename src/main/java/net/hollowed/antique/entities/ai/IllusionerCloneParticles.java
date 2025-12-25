package net.hollowed.antique.entities.ai;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

public class IllusionerCloneParticles {
    public static void particles(Level world, double x, double y, double z) {
        for (int i = 0; i < 10; i++) {
            world.addAlwaysVisibleParticle(
                    ParticleTypes.CLOUD,
                    x + Math.random() - 0.5, y, z + Math.random() - 0.5,
                    0,
                    0,
                    0
            );
        }
        world.playLocalSound(x, y, z, SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.HOSTILE, 1.0F, 1.0F, false);
    }
}
