package net.hollowed.antique.mixin.entities.projectile;

import net.minecraft.client.particle.FireworksSparkParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FireworksSparkParticle.FireworkParticle.class)
public class FireworkParticleMixin {

    @ModifyArg(method = "addExplosionParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleManager;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;"), index = 4)
    private double modifyXVelocity(double x) {
        return x * 2;
    }

    @ModifyArg(method = "addExplosionParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleManager;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;"), index = 5)
    private double modifyYVelocity(double y) {
        return y * 2;
    }

    @ModifyArg(method = "addExplosionParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleManager;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;"), index = 6)
    private double modifyZVelocity(double z) {
        return z * 2;
    }
}
