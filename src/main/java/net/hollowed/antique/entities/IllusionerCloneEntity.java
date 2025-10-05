//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.hollowed.antique.entities;

import net.hollowed.antique.util.interfaces.duck.SetSpellTicks;
import net.hollowed.antique.util.delay.TickDelayScheduler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Objects;
import java.util.UUID;

public class IllusionerCloneEntity extends SpellcastingIllagerEntity implements RangedAttackMob, Ownable {

    @Nullable
    private UUID ownerUuid;
    @Nullable
    private LivingEntity owner;

    public IllusionerCloneEntity(EntityType<? extends IllusionerCloneEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 5;
    }

    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new SpellcastingIllagerEntity.LookAtTargetGoal());
        this.goalSelector.add(3, new FleeEntityGoal<>(this, CreakingEntity.class, 8.0F, 1.0, 1.2));
        this.goalSelector.add(3, new TeleportGoal(this));
        this.goalSelector.add(6, new BowAttackGoal<>(this, 0.5, 20, 15.0F));
        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
        this.targetSelector.add(1, (new RevengeGoal(this, RaiderEntity.class)).setGroupRevenge());
        this.targetSelector.add(2, (new ActiveTargetGoal<>(this, PlayerEntity.class, true)).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, (new ActiveTargetGoal<>(this, MerchantEntity.class, false)).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, (new ActiveTargetGoal<>(this, IronGolemEntity.class, false)).setMaxTimeWithoutVisibility(300));
    }

    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        view.putNullable("Owner", Uuids.INT_STREAM_CODEC, this.ownerUuid);
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        this.setOwner(view.read("Owner", Uuids.INT_STREAM_CODEC).orElse(null));
    }

    public static DefaultAttributeContainer.Builder createIllusionerAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.MOVEMENT_SPEED, 0.5).add(EntityAttributes.FOLLOW_RANGE, 18.0).add(EntityAttributes.MAX_HEALTH, 1.0);
    }

    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    public SoundEvent getCelebratingSound() {
        return SoundEvents.ENTITY_ILLUSIONER_AMBIENT;
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

    @Override
    public void onDeath(DamageSource damageSource) {
        for (int j = 0; j < 24; ++j) {
            this.getEntityWorld().addParticleClient(ParticleTypes.CLOUD, this.getX() + Math.random() - 0.5, this.getRandomBodyY(), this.getZ() + Math.random() - 0.5, 0.0, 0.1, 0.0);
        }
        this.getEntityWorld().playSoundClient(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE, this.getSoundCategory(), 1.0F, 1.0F, false);
        TickDelayScheduler.schedule(1, this::discard);
        super.onDeath(damageSource);
    }

    public void setOwner(@Nullable LivingEntity entity) {
        if (entity != null) {
            this.ownerUuid = entity.getUuid();
            this.owner = entity;
        }
    }

    protected void setOwner(@Nullable UUID ownerUuid) {
        if (!Objects.equals(this.ownerUuid, ownerUuid)) {
            this.ownerUuid = ownerUuid;
            this.owner = ownerUuid != null ? this.getEntity(ownerUuid) : null;
        }
    }

    @Nullable
    public Entity getOwner() {
        if (this.owner != null && !this.owner.isRemoved()) {
            return this.owner;
        } else if (this.ownerUuid != null) {
            this.owner = this.getEntity(this.ownerUuid);
            return this.owner;
        } else {
            return null;
        }
    }

    @Nullable
    protected LivingEntity getEntity(UUID uuid) {
        World var3 = this.getEntityWorld();
        if (var3 instanceof ServerWorld serverWorld && serverWorld.getEntity(uuid) instanceof LivingEntity) {
            return (LivingEntity) serverWorld.getEntity(uuid);
        } else {
            return null;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getOwner() == null || !this.getOwner().isAlive()) {
            if (this.getEntityWorld() instanceof ServerWorld world) {
                this.kill(world);
            }
        }
    }

    public void shootAt(LivingEntity target, float pullProgress) {
        ItemStack itemStack = this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW));
        ItemStack itemStack2 = this.getProjectileType(itemStack);
        PersistentProjectileEntity persistentProjectileEntity = ProjectileUtil.createArrowProjectile(this, itemStack2, pullProgress, itemStack);
        double d = target.getX() - this.getX();
        double e = target.getBodyY(0.3333333333333333) - persistentProjectileEntity.getY();
        double f = target.getZ() - this.getZ();
        double g = Math.sqrt(d * d + f * f);
        World var15 = this.getEntityWorld();
        if (var15 instanceof ServerWorld serverWorld) {
            ProjectileEntity.spawnWithVelocity(persistentProjectileEntity, serverWorld, itemStack2, d, e + g * 0.20000000298023224, f, 1.6F, (float)(14 - serverWorld.getDifficulty().getId() * 4));
        }

        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
            this.kill(serverWorld);
        }
    }

    public IllusionerCloneEntity.State getState() {
        if (this.isSpellcasting()) {
            return State.SPELLCASTING;
        } else {
            return this.isAttacking() ? State.BOW_AND_ARROW : State.CROSSED;
        }
    }

    public static boolean teleportRandomly(LivingEntity entity) {
        if (entity.isAlive()) {
            for (int i = 0; i < 10; i++) {
                double d = entity.getX() + (entity.getRandom().nextDouble() - 0.5) * 16.0;
                double e = entity.getY() + (double) (entity.getRandom().nextInt(16) - 8);
                double f = entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * 16.0;
                if (teleportTo(d, e, f, entity)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    private static boolean teleportTo(double x, double y, double z, LivingEntity entity) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(x, y, z);

        while(mutable.getY() > entity.getEntityWorld().getBottomY() && !entity.getEntityWorld().getBlockState(mutable).blocksMovement()) {
            mutable.move(Direction.DOWN);
        }

        BlockState blockState = entity.getEntityWorld().getBlockState(mutable);
        boolean bl = blockState.blocksMovement();
        boolean bl2 = blockState.getFluidState().isIn(FluidTags.WATER);
        if (bl && !bl2) {
            Vec3d vec3d = entity.getEntityPos();
            boolean bl3 = entity.teleport(x, y, z, true);
            if (bl3) {
                entity.getEntityWorld().emitGameEvent(GameEvent.TELEPORT, vec3d, GameEvent.Emitter.of(entity));
                if (!entity.isSilent()) {
                    entity.getEntityWorld().playSound(null, entity.lastX, entity.lastY, entity.lastZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, entity.getSoundCategory(), 1.0F, 1.0F);
                    entity.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
                }
            }

            return bl3;
        } else {
            return false;
        }
    }

    public static class TeleportGoal extends Goal {
        protected int spellCooldown;
        protected int startTime;
        protected SpellcastingIllagerEntity entity;

        public TeleportGoal(SpellcastingIllagerEntity entity) {
            this.entity = entity;
        }

        public boolean canStart() {
            LivingEntity livingEntity = entity.getTarget();
            if (livingEntity != null && livingEntity.isAlive()) {
                if (entity.isSpellcasting()) {
                    return false;
                } else {
                    return entity.age >= this.startTime;
                }
            } else {
                return false;
            }
        }

        public boolean shouldContinue() {
            LivingEntity livingEntity = entity.getTarget();
            return livingEntity != null && livingEntity.isAlive() && this.spellCooldown > 0;
        }

        public void start() {
            this.spellCooldown = this.getTickCount(this.getInitialCooldown());
            if (entity instanceof SetSpellTicks access) {
                access.antiquities$setSpellTicks(this.getSpellTicks());
            }
            this.startTime = entity.age + this.startTimeDelay();
            SoundEvent soundEvent = this.getSoundPrepare();
            if (soundEvent != null) {
                entity.playSound(soundEvent, 1.0F, 1.0F);
            }

            entity.setSpell(this.getSpell());
        }

        public void tick() {
            if (entity instanceof IllusionerEntity illusionerEntity) {
                illusionerEntity.getDataTracker().set(IllusionerEntity.SPELL_COLOR, new Vector3f(0.3F, 0.3F, 0.8F));
            }
            --this.spellCooldown;
            if (this.spellCooldown == 0) {
                this.castSpell();
                entity.playSound(SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.0F, 1.0F);
            }

        }

        protected void castSpell() {
            teleportRandomly(entity);
        }

        protected int getInitialCooldown() {
            return 20;
        }

        protected int getSpellTicks() {
            return 20;
        }

        protected int startTimeDelay() {
            return 200;
        }

        @Nullable
        protected SoundEvent getSoundPrepare() {
            return null;
        }

        protected Spell getSpell() {
            return Spell.DISAPPEAR;
        }
    }
}
