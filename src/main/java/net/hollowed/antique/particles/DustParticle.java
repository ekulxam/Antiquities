package net.hollowed.antique.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class DustParticle extends BillboardParticle {

	DustParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, boolean signal, SpriteProvider provider) {
		super(world, x, y, z, provider.getFirst());
		this.scale(2.0F);
		this.setBoundingBoxSpacing(0.15F, 0.15F);
		if (signal) {
			this.maxAge = this.random.nextInt(50) + 280;
		} else {
			this.maxAge = this.random.nextInt(12) + 20;
		}

		this.gravityStrength = 3.0E-6F;
		this.velocityX = velocityX;
		this.velocityY = velocityY + (double)(this.random.nextFloat() / 500.0F);
		this.velocityZ = velocityZ;
	}

	@Override
	public void tick() {
		this.lastX = this.x;
		this.lastY = this.y;
		this.lastZ = this.z;
		if (this.age++ < this.maxAge && !(this.alpha <= 0.0F)) {
			this.velocityX = this.velocityX + (double)(this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean() ? 1 : -1));
			this.velocityZ = this.velocityZ + (double)(this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean() ? 1 : -1));
			this.velocityY = this.velocityY - (double)this.gravityStrength;
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			if (this.age >= this.maxAge - 60 && this.alpha > 0.01F) {
				this.alpha -= 0.015F;
			}
		} else {
			this.markDead();
		}
	}

	@Override
	public RenderType getRenderType() {
		return RenderType.PARTICLE_ATLAS_TRANSLUCENT;
	}

	@Environment(EnvType.CLIENT)
	public static class CosySmokeFactory implements ParticleFactory<SimpleParticleType> {
		private final SpriteProvider spriteProvider;

		public CosySmokeFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		@Override
		public @Nullable Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Random random) {
			DustParticle campfireSmokeParticle = new DustParticle(world, x, y, z, velocityX, velocityY, velocityZ, false, this.spriteProvider);
			campfireSmokeParticle.setAlpha(0.9F);
			campfireSmokeParticle.setSprite(this.spriteProvider.getFirst());
			return campfireSmokeParticle;
		}
	}
}
