package net.hollowed.antique.particles;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.particles.custom.CakeSmearParticle;
import net.hollowed.antique.particles.custom.DustParticle;
import net.hollowed.antique.particles.custom.FacingRingParticle;
import net.hollowed.antique.particles.custom.HitMarkerParticle;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {
    public static final SimpleParticleType SPARKLE_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType DUST_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType CAKE_SMEAR = FabricParticleTypes.simple();
    public static final SimpleParticleType HIT_MARKER = FabricParticleTypes.simple();

    public static void initialize() {
	    Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Antiquities.MOD_ID, "sparkle_particle"), SPARKLE_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Antiquities.MOD_ID, "dust"), DUST_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Antiquities.MOD_ID, "cake_smear"), CAKE_SMEAR);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Antiquities.MOD_ID, "hit_marker"), HIT_MARKER);
    }

    public static void initializeClient() {
        ParticleFactoryRegistry.getInstance().register(SPARKLE_PARTICLE, FacingRingParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(CAKE_SMEAR, CakeSmearParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(DUST_PARTICLE, DustParticle.CosySmokeFactory::new);
        ParticleFactoryRegistry.getInstance().register(HIT_MARKER, HitMarkerParticle.Factory::new);
    }
}
