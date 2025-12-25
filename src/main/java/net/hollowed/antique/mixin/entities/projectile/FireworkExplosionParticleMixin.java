package net.hollowed.antique.mixin.entities.projectile;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.particle.FireworkParticles$SparkParticle")
public abstract class FireworkExplosionParticleMixin extends SimpleAnimatedParticle {

    protected FireworkExplosionParticleMixin(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider, float upwardsAcceleration) {
        super(world, x, y, z, spriteProvider, upwardsAcceleration);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, ParticleEngine particleManager, SpriteSet spriteProvider, CallbackInfo ci) {
        this.quadSize *= 1.5F;
    }
}
