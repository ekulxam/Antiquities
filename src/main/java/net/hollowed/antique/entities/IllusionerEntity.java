//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.hollowed.antique.entities;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hollowed.antique.index.AntiqueEntities;
import net.hollowed.antique.networking.IllusionerParticlePacketPayload;
import net.hollowed.antique.util.FireworkUtil;
import net.hollowed.antique.util.delay.TickDelayScheduler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class IllusionerEntity extends SpellcastingIllagerEntity implements RangedAttackMob {
    public static final TrackedData<Vector3f> SPELL_COLOR = DataTracker.registerData(IllusionerEntity.class, TrackedDataHandlerRegistry.VECTOR_3F);
    private int mirrorSpellTimer;
    private final Vec3d[][] mirrorCopyOffsets;

    public IllusionerEntity(EntityType<? extends IllusionerEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 5;
        this.mirrorCopyOffsets = new Vec3d[2][4];

        for(int i = 0; i < 4; ++i) {
            this.mirrorCopyOffsets[0][i] = Vec3d.ZERO;
            this.mirrorCopyOffsets[1][i] = Vec3d.ZERO;
        }
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(SPELL_COLOR, new Vector3f(0, 0, 0));
    }

    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new SpellcastingIllagerEntity.LookAtTargetGoal());
        this.goalSelector.add(3, new FleeEntityGoal<>(this, CreakingEntity.class, 8.0F, 1.0, 1.2));
        this.goalSelector.add(3, new IllusionerCloneEntity.TeleportGoal(this));
        this.goalSelector.add(4, new SmokeBombGoal());
        this.goalSelector.add(5, new GiveInvisibilityGoal());
        this.goalSelector.add(6, new BowAttackGoal<>(this, 0.5, 60, 15.0F));
        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
        this.targetSelector.add(1, (new RevengeGoal(this, RaiderEntity.class)).setGroupRevenge());
        this.targetSelector.add(2, (new ActiveTargetGoal<>(this, PlayerEntity.class, true)).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, (new ActiveTargetGoal<>(this, MerchantEntity.class, false)).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, (new ActiveTargetGoal<>(this, IronGolemEntity.class, false)).setMaxTimeWithoutVisibility(300));
    }

    public static DefaultAttributeContainer.Builder createIllusionerAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MOVEMENT_SPEED, 0.5)
                .add(EntityAttributes.FOLLOW_RANGE, 36.0)
                .add(EntityAttributes.GRAVITY, 0.05)
                .add(EntityAttributes.SAFE_FALL_DISTANCE, 8)
                .add(EntityAttributes.MAX_HEALTH, 32.0);
    }

    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        ItemStack bow = Items.BOW.getDefaultStack();
        bow.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.of(Items.FIREWORK_ROCKET.getDefaultStack()));
        this.equipStack(EquipmentSlot.MAINHAND, bow);
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient && this.isSpellcasting()) {
            float i = this.bodyYaw * 0.017453292F + MathHelper.cos((float)this.age * 0.6662F) * 0.25F;
            float j = MathHelper.cos(i);
            float k = MathHelper.sin(i);
            double d = 0.6 * (double)this.getScale();
            double e = 1.8 * (double)this.getScale();
            this.getWorld().addParticleClient(EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, this.dataTracker.get(SPELL_COLOR).x, this.dataTracker.get(SPELL_COLOR).y, this.dataTracker.get(SPELL_COLOR).z), this.getX() + (double)j * d, this.getY() + e, this.getZ() + (double)k * d, 0.0, 0.0, 0.0);
            this.getWorld().addParticleClient(EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, this.dataTracker.get(SPELL_COLOR).x, this.dataTracker.get(SPELL_COLOR).y, this.dataTracker.get(SPELL_COLOR).z), this.getX() - (double)j * d, this.getY() + e, this.getZ() - (double)k * d, 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void applyDamage(ServerWorld world, DamageSource source, float amount) {
        IllusionerCloneEntity clone = new IllusionerCloneEntity(AntiqueEntities.ILLUSIONER_CLONE, this.getWorld());
        clone.equipStack(EquipmentSlot.MAINHAND, Items.BOW.getDefaultStack());
        clone.setPosition(this.getX(), this.getY(), this.getZ());
        clone.setOwner(this);
        clone.setTarget(this.getTarget());
        clone.setBodyYaw(this.getBodyYaw());
        clone.setHeadYaw(this.getHeadYaw());
        if (IllusionerCloneEntity.teleportRandomly(this)) {
            this.getWorld().spawnEntity(clone);
        }
        super.applyDamage(world, source, amount);
    }

    public void tickMovement() {
        super.tickMovement();

        if (this.getVelocity().y < -0.2) {
            this.getWorld().addParticleClient(ParticleTypes.POOF, this.getParticleX(0.5), this.getY(), this.getBodyZ(0.5), 0.0, 0.0, 0.0);
        }
        if (this.isInvisible()) {
            this.getWorld().addParticleClient(ParticleTypes.LARGE_SMOKE, this.getParticleX(0.1), this.getY() + 1.5, this.getBodyZ(0.1), 0.0, 0.0, 0.0);
        }

        if (this.getWorld().isClient && this.isInvisible()) {
            --this.mirrorSpellTimer;
            if (this.mirrorSpellTimer < 0) {
                this.mirrorSpellTimer = 0;
            }

            if (this.hurtTime != 1 && this.age % 1200 != 0) {
                if (this.hurtTime == this.maxHurtTime - 1) {
                    this.mirrorSpellTimer = 3;

                    for(int k = 0; k < 4; ++k) {
                        this.mirrorCopyOffsets[0][k] = this.mirrorCopyOffsets[1][k];
                        this.mirrorCopyOffsets[1][k] = new Vec3d(0.0, 0.0, 0.0);
                    }
                }
            } else {
                this.mirrorSpellTimer = 3;

                int j;
                for(j = 0; j < 4; ++j) {
                    this.mirrorCopyOffsets[0][j] = this.mirrorCopyOffsets[1][j];
                    this.mirrorCopyOffsets[1][j] = new Vec3d((double)(-6.0F + (float)this.random.nextInt(13)) * 0.5, Math.max(0, this.random.nextInt(6) - 4), (double)(-6.0F + (float)this.random.nextInt(13)) * 0.5);
                }

                for(j = 0; j < 16; ++j) {
                    this.getWorld().addParticleClient(ParticleTypes.CLOUD, this.getParticleX(0.5), this.getRandomBodyY(), this.getBodyZ(0.5), 0.0, 0.0, 0.0);
                }

                this.getWorld().playSoundClient(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE, this.getSoundCategory(), 1.0F, 1.0F, false);
            }
        }

    }

    public SoundEvent getCelebratingSound() {
        return SoundEvents.ENTITY_ILLUSIONER_AMBIENT;
    }

    public Vec3d[] getMirrorCopyOffsets(float tickProgress) {
        if (this.mirrorSpellTimer <= 0) {
            return this.mirrorCopyOffsets[1];
        } else {
            double d = ((float)this.mirrorSpellTimer - tickProgress) / 3.0F;
            d = Math.pow(d, 0.25);
            Vec3d[] vec3ds = new Vec3d[4];

            for(int i = 0; i < 4; ++i) {
                vec3ds[i] = this.mirrorCopyOffsets[1][i].multiply(1.0 - d).add(this.mirrorCopyOffsets[0][i].multiply(d));
            }

            return vec3ds;
        }
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ILLUSIONER_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ILLUSIONER_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ILLUSIONER_HURT;
    }

    protected SoundEvent getCastSpellSound() {
        return SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL;
    }

    public void addBonusForWave(ServerWorld world, int wave, boolean unused) {
    }

    @Override
    public boolean isInvulnerableTo(ServerWorld world, DamageSource source) {
        if (source.isOf(DamageTypes.FIREWORKS)) return true;
        return super.isInvulnerableTo(world, source);
    }

    public void shootAt(LivingEntity target, float pullProgress) {
        World world = this.getWorld();
        ItemStack stack = Items.FIREWORK_ROCKET.getDefaultStack();
        stack.set(DataComponentTypes.FIREWORKS, FireworkUtil.randomFirework());
        if (world instanceof ServerWorld serverWorld) {
            FireworkRocketEntity projectile = new FireworkRocketEntity(world, stack, this.getX(), this.getY() + 1, this.getZ(), true);
            Vec3d direction = this.getPos().subtract(target.getPos());
            projectile.setVelocity(direction.normalize().multiply(-1.75));
            projectile.setOwner(this);
            projectile.velocityModified = true;
            serverWorld.spawnEntity(projectile);
        }
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.playSound(SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    public IllagerEntity.State getState() {
        if (this.isSpellcasting()) {
            return State.SPELLCASTING;
        } else {
            return this.isAttacking() ? State.BOW_AND_ARROW : State.CROSSED;
        }
    }

    private class GiveInvisibilityGoal extends SpellcastingIllagerEntity.CastSpellGoal {
        GiveInvisibilityGoal() {
            super();
        }

        public boolean canStart() {
            if (!super.canStart()) {
                return false;
            } else {
                return !IllusionerEntity.this.hasStatusEffect(StatusEffects.INVISIBILITY);
            }
        }

        protected int getSpellTicks() {
            return 80;
        }

        protected int startTimeDelay() {
            return 340;
        }

        protected void castSpell() {
            IllusionerEntity.this.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 300), IllusionerEntity.this);

            int count = 0;
            switch (IllusionerEntity.this.getWorld().getLocalDifficulty(IllusionerEntity.this.getBlockPos()).getGlobalDifficulty()) {
                case Difficulty.EASY -> count = 2;
                case Difficulty.NORMAL -> count = 4;
                case Difficulty.HARD -> count = 6;
            }

            for (int i = 0; i < count; i++) {
                for (int tries = 0; tries < 10; tries++) {
                    double offsetX = (IllusionerEntity.this.getRandom().nextDouble() - 0.5) * 12.0;
                    double offsetZ = (IllusionerEntity.this.getRandom().nextDouble() - 0.5) * 12.0;
                    double x = IllusionerEntity.this.getX() + offsetX;
                    double z = IllusionerEntity.this.getZ() + offsetZ;

                    double originY = IllusionerEntity.this.getY();
                    int bestY = -1;
                    double bestDistance = Double.MAX_VALUE;

                    for (int yOffset = -15; yOffset <= 15; yOffset++) {
                        int testY = (int) (originY + yOffset);
                        if (!IllusionerEntity.this.getWorld().isOutOfHeightLimit(testY)) {
                            if (IllusionerEntity.this.getWorld().getBlockState(new BlockPos((int)x, testY, (int)z)).isAir()) {
                                double distance = Math.abs(originY - testY);
                                if (distance < bestDistance) {
                                    bestY = testY;
                                    bestDistance = distance;
                                }
                            }
                        }
                    }

                    if (bestY != -1) {
                        IllusionerCloneEntity clone = new IllusionerCloneEntity(AntiqueEntities.ILLUSIONER_CLONE, IllusionerEntity.this.getWorld());
                        clone.equipStack(EquipmentSlot.MAINHAND, Items.BOW.getDefaultStack());
                        clone.setPosition(x, bestY, z);
                        clone.setOwner(IllusionerEntity.this);
                        clone.setTarget(IllusionerEntity.this.getTarget());
                        IllusionerEntity.this.getWorld().spawnEntity(clone);

                        ServerWorld serverWorld = (ServerWorld) clone.getWorld();
                        for (ServerPlayerEntity player : serverWorld.getPlayers()) {
                            ServerPlayNetworking.send(player, new IllusionerParticlePacketPayload(
                                    x, bestY + 1 + ((Math.random() - 0.5) * 2), z
                            ));
                        }

                        break;
                    }
                }
            }
        }

        @Override
        public void tick() {
            super.tick();
            IllusionerEntity.this.getDataTracker().set(SPELL_COLOR, new Vector3f(103 / 255.0F, 68 / 255.0F, 99 / 255.0F));
        }

        @Nullable
        protected SoundEvent getSoundPrepare() {
            return SoundEvents.ENTITY_ILLUSIONER_PREPARE_MIRROR;
        }

        protected SpellcastingIllagerEntity.Spell getSpell() {
            return Spell.BLINDNESS;
        }
    }

    private class SmokeBombGoal extends SpellcastingIllagerEntity.CastSpellGoal {
        private int targetId;

        SmokeBombGoal() {
            super();
        }

        public boolean canStart() {
            if (!super.canStart()) {
                return false;
            } else if (IllusionerEntity.this.getTarget() == null) {
                return false;
            } else if (IllusionerEntity.this.getTarget().getId() == this.targetId) {
                return false;
            } else {
                return IllusionerEntity.this.getWorld().getLocalDifficulty(IllusionerEntity.this.getBlockPos()).isHarderThan((float)Difficulty.NORMAL.ordinal());
            }
        }

        public void start() {
            super.start();
            LivingEntity livingEntity = IllusionerEntity.this.getTarget();
            if (livingEntity != null) {
                this.targetId = livingEntity.getId();
            }

        }

        protected int getSpellTicks() {
            return 60;
        }

        protected int startTimeDelay() {
            return 300;
        }

        protected void castSpell() {
            for (int i = 0; i < 3; i++) {
                TickDelayScheduler.schedule(random.nextBetween(0, 10), () -> {
                    SmokeBombEntity smokeBomb = new SmokeBombEntity(AntiqueEntities.SMOKE_BOMB, IllusionerEntity.this.getWorld());
                    smokeBomb.setPosition(IllusionerEntity.this.getPos().add((Math.random() - 0.5) * 2, 3, (Math.random() - 0.5) * 2));
                    smokeBomb.setVelocity((Math.random() - 0.5) * 0.8, 0.25, (Math.random() - 0.5) * 0.8);
                    smokeBomb.setFirework(true);
                    IllusionerEntity.this.getWorld().spawnEntity(smokeBomb);
                    IllusionerEntity.this.getWorld().playSound(null, IllusionerEntity.this.getX(), IllusionerEntity.this.getY(), IllusionerEntity.this.getZ(), SoundEvents.ENTITY_WITCH_THROW, SoundCategory.BLOCKS, 1F, 1F);
                });
            }
        }

        @Override
        public void tick() {
            super.tick();
            IllusionerEntity.this.getDataTracker().set(SPELL_COLOR, new Vector3f(1, 55 / 255.0F, 130 / 255.0F));
        }

        protected SoundEvent getSoundPrepare() {
            return SoundEvents.ENTITY_ILLUSIONER_PREPARE_BLINDNESS;
        }

        protected SpellcastingIllagerEntity.Spell getSpell() {
            return Spell.BLINDNESS;
        }
    }
}
