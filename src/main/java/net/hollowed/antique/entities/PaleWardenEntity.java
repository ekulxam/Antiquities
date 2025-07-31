package net.hollowed.antique.entities;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.hollowed.antique.index.AntiqueItems;
import net.hollowed.antique.networking.PaleWardenTickPacketPayload;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("all")
public class PaleWardenEntity extends PathAwareEntity {

    private boolean awakened = false;
    private int mode = 0;

    public final AnimationState awakenAnimationState = new AnimationState();
    public int awakenAnimationTimeout = 0;

    public final AnimationState idleAnimationState = new AnimationState();
    public int idleAnimationTimeout = 0;

    public PaleWardenEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        if (MinecraftClient.getInstance().player != null) {
            ClientPlayNetworking.send(new PaleWardenTickPacketPayload(this.getId(), AntiqueItems.PALE_WARDENS_GREATSWORD.getDefaultStack(), ItemStack.EMPTY));
        }
    }

    @Override
    public boolean isLeftHanded() {
        return false;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 24.0F));
        this.goalSelector.add(2, new LookAroundGoal(this));
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    public void swapStacks(ItemStack mainhand, ItemStack offhand) {
        if (this.getWorld() instanceof ServerWorld) {
            // Set the stacks on the server
            this.setStackInHand(Hand.MAIN_HAND, mainhand);
            this.setStackInHand(Hand.OFF_HAND, offhand);

            // Swap the stacks
            ItemStack tempStack = this.getStackInHand(Hand.MAIN_HAND).copy();
            this.setStackInHand(Hand.MAIN_HAND, this.getStackInHand(Hand.OFF_HAND));
            this.setStackInHand(Hand.OFF_HAND, tempStack);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (MinecraftClient.getInstance().player != null && this.awakenAnimationTimeout == 40) {
            ItemStack mainhand = this.getStackInHand(Hand.MAIN_HAND);
            ItemStack offhand = this.getStackInHand(Hand.OFF_HAND);

            ClientPlayNetworking.send(new PaleWardenTickPacketPayload(this.getId(), mainhand, offhand));
        }

        if (this.getWorld().isClient) {
            if (this.awakenAnimationTimeout == 80) {
                this.idleAnimationTimeout = 0;
                this.idleAnimationState.stop();
                this.awakenAnimationState.start(this.age);
            }
            if (this.awakenAnimationTimeout >= 0) {
                --this.awakenAnimationTimeout;
            } else {
                this.awakenAnimationState.stop();
            }
            if (this.idleAnimationTimeout == 60) {
                this.idleAnimationState.start(this.age);
            }
            if (this.idleAnimationTimeout >= 0) {
                --this.idleAnimationTimeout;
            } else if (!(this.awakenAnimationTimeout >= 0)) {
                this.idleAnimationTimeout = 60;
            }
        }
    }

    @Override
    public boolean clientDamage(DamageSource source) {
        if (this.awakenAnimationTimeout <= 0) {
            this.awakenAnimationTimeout = 80;
        }
        return super.clientDamage(source);
    }

    @Override
    public boolean isCollidable(@Nullable Entity entity) {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected float getBaseWaterMovementSpeedMultiplier() {
        return 0;
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }

    @Override
    public boolean canFreeze() {
        return false;
    }

    public boolean isInvulnerableTo(ServerWorld world, DamageSource source) {
        if (source.isIn(DamageTypeTags.IS_PROJECTILE)
                || source.isIn(DamageTypeTags.IS_DROWNING)
                || source.isIn(DamageTypeTags.IS_EXPLOSION)
                || source.isIn(DamageTypeTags.IS_FALL)
                || source.isIn(DamageTypeTags.IS_FIRE)
                || source.isIn(DamageTypeTags.IS_FREEZING)
                || source.isIn(DamageTypeTags.IS_LIGHTNING)
                || source.isOf(DamageTypes.MAGIC)
                || source.isOf(DamageTypes.INDIRECT_MAGIC)) {
            return true;
        }
        return this.isAlwaysInvulnerableTo(source) || EnchantmentHelper.isInvulnerableTo(world, this, source);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(EntityAttributes.FOLLOW_RANGE, 32.0)
                .add(EntityAttributes.STEP_HEIGHT, 1.0)
                .add(EntityAttributes.ARMOR, 20.0)
                .add(EntityAttributes.ARMOR_TOUGHNESS, 8.0)
                .add(EntityAttributes.ATTACK_DAMAGE, 5.0)
                .add(EntityAttributes.ATTACK_KNOCKBACK, 2.0)
                .add(EntityAttributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(EntityAttributes.MAX_HEALTH, 180.0)
                .add(EntityAttributes.FALL_DAMAGE_MULTIPLIER, 0)
                .add(EntityAttributes.MOVEMENT_SPEED, 1);
    }
}
