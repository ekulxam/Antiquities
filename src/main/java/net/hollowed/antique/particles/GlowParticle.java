package net.hollowed.antique.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class GlowParticle extends BillboardParticle {
	private final SpriteProvider spriteProvider;

	GlowParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
		super(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider.getFirst());
		this.velocityMultiplier = 0.96F;
		this.ascending = true;
		this.spriteProvider = spriteProvider;
		this.scale *= 0.75F;
		this.collidesWithWorld = false;
		this.updateSprite(spriteProvider);
	}

	@Override
	public RenderType getRenderType() {
		return RenderType.PARTICLE_ATLAS_TRANSLUCENT;
	}

	@Override
	public int getBrightness(float tint) {
		float f = (this.age + tint) / this.maxAge;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		int i = super.getBrightness(tint);
		int j = i & 0xFF;
		int k = i >> 16 & 0xFF;
		j += (int)(f * 15.0F * 16.0F);
		if (j > 240) {
			j = 240;
		}

		return j | k << 16;
	}

	@Override
	public void tick() {
		super.tick();
		this.updateSprite(this.spriteProvider);
	}

	@Environment(EnvType.CLIENT)
	public static class ScrapeFactory implements ParticleFactory<SimpleParticleType> {
		private final SpriteProvider spriteProvider;

		public ScrapeFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		@Override
		public @Nullable Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Random random) {
			GlowParticle glowParticle = new GlowParticle(world, x, y, z, 0.0, 0.0, 0.0, this.spriteProvider);
			if (world.random.nextBoolean()) {
				glowParticle.setColor(229 / 255.0F, 158 /255.0F, 88 / 255.0F);
			} else {
				glowParticle.setColor(210 / 255.0F, 126 / 255.0F, 86 / 255.0F);
			}

			glowParticle.setVelocity(velocityX * 0.01, velocityY * 0.01, velocityZ * 0.01);
			glowParticle.setMaxAge(world.random.nextInt(30) + 10);
			return glowParticle;
		}
	}
}
