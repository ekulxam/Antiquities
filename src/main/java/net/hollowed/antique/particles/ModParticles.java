package net.hollowed.antique.particles;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.particles.custom.DustParticle;
import net.hollowed.antique.particles.custom.ModEndRodParticle;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {
    public static final SimpleParticleType SPARKLE_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType DUST_PARTICLE = FabricParticleTypes.simple();

    public static void initialize() {
	    Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Antiquities.MOD_ID, "sparkle_particle"), SPARKLE_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Antiquities.MOD_ID, "dust"), DUST_PARTICLE);
    }

    public static void initializeClient() {
        // For this example, we will use the end rod particle behaviour.
        ParticleFactoryRegistry.getInstance().register(SPARKLE_PARTICLE, ModEndRodParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(DUST_PARTICLE, DustParticle.CosySmokeFactory::new);
    }
}
