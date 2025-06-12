package net.hollowed.antique.particles.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public class CakeSmearParticle extends SpriteBillboardParticle {
	private final double dirX;
	private final double dirY;
	private final double dirZ;
	private final ClientWorld world;

	CakeSmearParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
		super(world, x, y, z);

		this.setPos(x, y, z);
		this.world = world;

		this.dirX = velocityX;
		this.dirY = velocityY;
		this.dirZ = velocityZ;

		this.velocityX = 0;
		this.velocityY = 0;
		this.velocityZ = 0;

		this.maxAge = 100;
		this.setSpriteForAge(spriteProvider);
		this.gravityStrength = 0;
		this.scale = 1;
	}

	@Override
	public void tick() {
		if (this.age >= 40 && this.alpha > 0.01F) {
			this.alpha -= 0.02F;
		}
		if (this.age++ >= this.maxAge) {
			this.markDead();
		}
	}

	@Override
	public void move(double dx, double dy, double dz) {
		this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
		this.repositionFromBoundingBox();
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<SimpleParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			CakeSmearParticle particle = new CakeSmearParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
			particle.setSprite(this.spriteProvider);
			return particle;
		}
	}

	@Override
	public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		Vec3d direction = new Vec3d(this.dirX, this.dirY, this.dirZ);
		Quaternionf quaternionf = new Quaternionf();
		if (direction.x == 1) quaternionf.rotateY((float) Math.toRadians(90));
		this.render(vertexConsumer, camera, quaternionf, tickDelta);

		Quaternionf quaternionf1 = new Quaternionf();
		if (direction.x == 1) quaternionf1.rotateY((float) Math.toRadians(-90));
		if (direction.z == 1) quaternionf1.rotateY((float) Math.toRadians(180));
		this.render(vertexConsumer, camera, quaternionf1, tickDelta);
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public int getBrightness(float tint) {
		BlockPos blockPos = BlockPos.ofFloored(this.x, this.y, this.z);
		return WorldRenderer.getLightmapCoordinates(this.world, blockPos);
	}
}
