package net.hollowed.antique.mixin.entities.projectile;

import net.minecraft.client.particle.FireworkParticles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FireworkParticles.Starter.class)
public class FireworkParticleMixin {

    @ModifyArg(method = "createParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleEngine;createParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)Lnet/minecraft/client/particle/Particle;"), index = 4)
    private double modifyXVelocity(double x) {
        return x * 2;
    }

    @ModifyArg(method = "createParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleEngine;createParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)Lnet/minecraft/client/particle/Particle;"), index = 5)
    private double modifyYVelocity(double y) {
        return y * 2;
    }

    @ModifyArg(method = "createParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleEngine;createParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)Lnet/minecraft/client/particle/Particle;"), index = 6)
    private double modifyZVelocity(double z) {
        return z * 2;
    }
}
