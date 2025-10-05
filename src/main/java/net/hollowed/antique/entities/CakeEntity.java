package net.hollowed.antique.entities;

import net.hollowed.antique.index.AntiqueSounds;
import net.hollowed.antique.index.AntiqueParticles;
import net.hollowed.antique.util.delay.TickDelayScheduler;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class CakeEntity extends PersistentProjectileEntity {
    private static final TrackedData<Boolean> FROZEN = DataTracker.registerData(CakeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Float> YAW = DataTracker.registerData(CakeEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> PITCH = DataTracker.registerData(CakeEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public CakeEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.setDamage(0);
        this.dataTracker.set(FROZEN, false);
        this.dataTracker.set(PITCH, this.getPitch());
        this.dataTracker.set(YAW, this.getYaw());
    }

    @Override
    public void handleStatus(byte status) {
        if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) {
            this.getEntityWorld().addBlockBreakParticles(this.getBlockPos(), Blocks.CAKE.getDefaultState());
        }
        if (status == EntityStatuses.ADD_DEATH_PARTICLES) {
            BlockPos entityPos = this.getBlockPos();
            World world = this.getEntityWorld();

            for (Direction direction : Direction.values()) {
                BlockPos offsetPos = entityPos.offset(direction);
                if (world.getBlockState(offsetPos).isFullCube(world, offsetPos) && direction.getAxis().isHorizontal()) {
                    Vec3d vec = Vec3d.ZERO;
                    switch (direction) {
                        case NORTH, SOUTH -> vec = new Vec3d(0, 0, 1);
                        case EAST, WEST -> vec = new Vec3d(1, 0, 0);
                    }

                    Vec3d particlePos = this.getEntityPos();

                    world.addParticleClient(
                            AntiqueParticles.CAKE_SMEAR,
                            particlePos.x + direction.getOffsetX() * (Math.random() / 10.0),
                            particlePos.y + direction.getOffsetY() * (Math.random() / 10.0),
                            particlePos.z + direction.getOffsetZ() * (Math.random() / 10.0),
                            vec.x,
                            vec.y,
                            vec.z
                    );
                    break;
                }
            }
        }
    }

    public ItemStack getPickBlockStack() {
        return Items.CAKE.getDefaultStack();
    }

    public boolean noRotate() {
        return this.dataTracker.get(FROZEN);
    }

    public float getStoredPitch() {
        return this.dataTracker.get(PITCH);
    }

    public float getStoredYaw() {
        return this.dataTracker.get(YAW);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(FROZEN, false);
        builder.add(PITCH, this.getPitch());
        builder.add(YAW, this.getYaw());
    }

    private int lowGravityTicksRemaining = 0;
    private int lastParticleTime = 0;

    @Override
    public void tick() {

        if (lastParticleTime > 0) {
            lastParticleTime--;
        }
        if (this.age > 400) {
            this.discard();
        }
        if (lowGravityTicksRemaining > 0 && this.getVelocity().getY() < -0.05) {
            this.addVelocity(0, 0.04, 0);
            lowGravityTicksRemaining--;
        }
        if (!this.noRotate()) {
            this.dataTracker.set(PITCH, this.getPitch());
            this.dataTracker.set(YAW, this.getYaw());
        }
        if (this.getVelocity().getY() < 0 && this.getVelocity().horizontalLength() < 0.2 && lastParticleTime <= 0) {
            this.getEntityWorld().sendEntityStatus(this, EntityStatuses.ADD_DEATH_PARTICLES);
            if (this.getVelocity().getY() < -0.5) {
                this.lastParticleTime = 2;
            } else if (this.getVelocity().getY() < -0.2) {
                this.lastParticleTime = 6;
            } else {
                this.lastParticleTime = 10;
            }
        }

        super.tick();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (!this.getEntityWorld().isClient() && !(entityHitResult.getEntity() instanceof CakeEntity)) {
            this.playSound(this.getSound(), 1.0F, 1.0F / (this.random.nextFloat() * 0.2F + 0.9F));
            this.getEntityWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
            this.setVelocity(Vec3d.ZERO);
            this.setNoGravity(true);
            this.dataTracker.set(FROZEN, true);

            TickDelayScheduler.schedule(5, () -> {
                this.setNoGravity(false);
                this.lowGravityTicksRemaining = 20;
            });
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        if (!this.getEntityWorld().isClient()) {
            this.playSound(this.getSound(), 1.0F, 1.0F / (this.random.nextFloat() * 0.2F + 0.9F));
            this.getEntityWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
            this.setVelocity(0, 0, 0);
            this.setNoGravity(true);
            this.dataTracker.set(FROZEN, true);

            Vector3f normal = blockHitResult.getSide().getUnitVector();
            this.setPosition(
                    this.getX() + normal.x * 0.1,
                    this.getY(),
                    this.getZ() + normal.z * 0.1
            );

            TickDelayScheduler.schedule(5, () -> {
                this.setNoGravity(false);
                this.lowGravityTicksRemaining = 20;
            });
            TickDelayScheduler.schedule(60, this::discard);
        }

        super.onBlockHit(blockHitResult);
        this.setSound(AntiqueSounds.CAKE_SPLAT);
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return Items.CAKE.getDefaultStack();
    }

    @Override
    protected SoundEvent getHitSound() {
        return AntiqueSounds.CAKE_SPLAT;
    }
}
