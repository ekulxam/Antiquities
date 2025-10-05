package net.hollowed.antique.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.antique.util.MathUtils;
import net.minecraft.block.MapColor;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public class FacingRingParticle extends AnimatedParticle {
	private final double dirX;
	private final double dirY;
	private final double dirZ;
	private final ClientWorld world;

	FacingRingParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
		super(world, x, y, z, spriteProvider, 0);

		this.setPos(x, y, z);
		this.world = world;

		this.dirX = velocityX;
		this.dirY = velocityY;
		this.dirZ = velocityZ;

		this.velocityX = 0;
		this.velocityY = 0;
		this.velocityZ = 0;

		this.maxAge = 7;
		this.updateSprite(spriteProvider);
		this.gravityStrength = 0;
		this.scale = 1;
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

		@Override
		public @Nullable Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Random random) {
			return new FacingRingParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
		}
	}

	@Override
	public void render(BillboardParticleSubmittable submittable, Camera camera, float tickProgress) {
		Vec3d direction = new Vec3d(this.dirX, this.dirY, this.dirZ).normalize();
		BlockPos blockPos = BlockPos.ofFloored(new Vec3d(this.x, this.y, this.z).subtract(direction.x, 0.2, direction.z));
		int color = world.getBlockState(blockPos).getMapColor(world, blockPos).color;
		color = ColorHelper.scaleRgb(ColorHelper.fullAlpha(color), 1.5F, 1.5F, 1.5F);
		if (world.getBlockState(blockPos).getMapColor(world, blockPos) != MapColor.CLEAR) {
			this.setColor(color);
		}

		Quaternionf quaternionf = MathUtils.vec3ToQuaternion(direction).rotateX((float) Math.toRadians(-90.0F));

		if (this.zRotation != 0.0F) {
			quaternionf.rotateZ(MathHelper.lerp(tickProgress, this.lastZRotation, this.zRotation));
		}

		this.render(submittable, camera, quaternionf, tickProgress);
		Quaternionf quaternionf1 = MathUtils.vec3ToQuaternion(new Vec3d(-direction.x, -direction.y, -direction.z)).rotateX((float) Math.toRadians(-90.0F));
		this.render(submittable, camera, quaternionf1, tickProgress);
	}

	@Override
	public int getBrightness(float tint) {
		BlockPos blockPos = BlockPos.ofFloored(this.x, this.y, this.z);
		return WorldRenderer.getLightmapCoordinates(this.world, blockPos);
	}
}
