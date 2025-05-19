package net.hollowed.antique.mixin;

import net.hollowed.antique.Antiquities;
import net.hollowed.antique.ModSounds;
import net.hollowed.antique.enchantments.EnchantmentListener;
import net.hollowed.antique.items.ModItems;
import net.hollowed.antique.items.custom.NetheritePauldronsItem;
import net.hollowed.antique.items.custom.VelocityTransferMaceItem;
import net.hollowed.antique.util.EntityFreezer;
import net.hollowed.antique.util.FreezeFrameManager;
import net.hollowed.antique.util.TickDelayScheduler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Mixin(PlayerEntity.class)
public abstract class AttackNonlivingEntityHandler extends LivingEntity {

    @Shadow public abstract void setFireTicks(int fireTicks);

    @Shadow protected abstract float getDamageAgainst(Entity target, float baseDamage, DamageSource damageSource);

    @Shadow public abstract float getAttackCooldownProgress(float baseTime);

    @Shadow public abstract void resetLastAttackedTicks();

    @Shadow public abstract void spawnSweepAttackParticles();

    @Shadow public abstract void addCritParticles(Entity target);

    @Shadow public abstract void addEnchantedHitParticles(Entity target);

    @Shadow public abstract void increaseStat(Identifier stat, int amount);

    @Shadow public abstract void addExhaustion(float exhaustion);

    @Shadow private ItemStack selectedItem;
    @Unique
    private int lastAttackTicks;

    protected AttackNonlivingEntityHandler(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (this instanceof EntityFreezer access) {
            if (this.lastAttackTicks <= 0 && access.antiquities$getFrozen()) {
                access.antiquities$setFrozen(false);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "attack", cancellable = true)
    private void attackWithScepter(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        float attackPower = player.getAttackCooldownProgress(0.0f);
        ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);

        if (stack.getItem() instanceof VelocityTransferMaceItem) {
            this.lastAttackTicks = player.getVelocity().length() > 0.6 ? (int) (player.getVelocity().length() * 5F) : 0;
            player.getEntityWorld().playSound(player, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BLOCK_HEAVY_CORE_PLACE, SoundCategory.PLAYERS, 1.0F, 1.3F);

            Vec3d effectiveVelocity = VelocityTransferMaceItem.playerVelocity;

            if (player.getVelocity().length() > 0.1) {
                if (target instanceof EntityFreezer access) {
                    access.antiquities$setFrozen(true);
                }
                if (player instanceof EntityFreezer access) {
                    access.antiquities$setFrozen(true);
                }
                int delay = player.getVelocity().length() > 0.6 ? (int) (player.getVelocity().length() * 5F) : 0;
                TickDelayScheduler.schedule(delay, () -> {
                    if (target instanceof EntityFreezer access) {
                        access.antiquities$setFrozen(false);
                    }
                    if (player instanceof EntityFreezer access) {
                        access.antiquities$setFrozen(false);
                    }

                    if (attackPower > 0.9f) {
                        float pitch = 1.1f + (player.getRandom().nextFloat() * .2f);

                        if (!target.isOnGround()) {
                            player.getEntityWorld().playSound(player, player.getX(), player.getY(), player.getZ(),
                                    SoundEvents.ITEM_MACE_SMASH_AIR, SoundCategory.PLAYERS, 1.0F, 1.3F);
                        } else {
                            player.getEntityWorld().playSound(player, player.getX(), player.getY(), player.getZ(),
                                    SoundEvents.ITEM_MACE_SMASH_GROUND, SoundCategory.PLAYERS, 1.0F, pitch);
                        }
                    }

                    // Scepter attack code

                    // Calculate velocity
                    if (effectiveVelocity.length() > 0.1) { // Adjust the threshold if needed

                        // Apply damage based on the velocity magnitude
                        float damage = Math.min((float) (effectiveVelocity.length() * 5 + 5), 30); // Clamp to a max of 30
                        target.serverDamage(
                                player.getWorld().getDamageSources().flyIntoWall(), damage);

                        // Apply velocity to the target
                        Vec3d targetVelocity = effectiveVelocity.multiply(4, 2.5, 4);

                        if (target instanceof PlayerEntity || target instanceof ServerPlayerEntity) {
                            targetVelocity = targetVelocity.multiply(2, 0.2, 2);
                        }

                        target.setVelocity(targetVelocity.multiply(0.9));
                        target.velocityModified = true;

                        // Find and apply reduced velocity to nearby entities
                        double radius = 5.0; // Radius to check for nearby entities
                        List<Entity> nearbyEntities = player.getWorld().getOtherEntities(
                                target, target.getBoundingBox().expand(radius));

                        for (Entity nearby : nearbyEntities) {
                            if (nearby instanceof LivingEntity && nearby != target && nearby != player) {
                                double distance = target.getPos().distanceTo(nearby.getPos());
                                if (distance <= radius) {
                                    double scalingFactor = 1.5 - (distance / radius); // Closer entities get more velocity
                                    Vec3d reducedVelocity = effectiveVelocity.multiply(scalingFactor); // Reduce strength
                                    nearby.setVelocity(reducedVelocity);
                                    nearby.velocityModified = true;
                                }
                            }
                        }

                        // Handle enchantments
                        if (EnchantmentListener.hasEnchantment(stack, "antique:kinematic")) {
                            Vec3d playerVelocity = effectiveVelocity.multiply(-1, -1, -1); // Reverse the velocity
                            player.setVelocity(playerVelocity);
                            player.velocityModified = true;

                            player.addStatusEffect(new StatusEffectInstance(Antiquities.BOUNCE_EFFECT, 30, 0, true, true));
                        } else if (EnchantmentListener.hasEnchantment(stack, "antique:impetus")) {
                            Vec3d playerVelocity = effectiveVelocity.multiply(1.5, 1.25, 1.5); // Enhance the velocity
                            player.setVelocity(playerVelocity);
                            player.velocityModified = true;

                            player.addStatusEffect(new StatusEffectInstance(Antiquities.VOLATILE_BOUNCE_EFFECT, 30, 0, true, true));
                        }

                        if (target instanceof PlayerEntity playerTarget && !EnchantmentListener.hasEnchantment(stack, "antique:kinematic")) {
                            playerTarget.addStatusEffect(new StatusEffectInstance(Antiquities.VOLATILE_BOUNCE_EFFECT, 30, 0, true, true));
                        } else if (target instanceof LivingEntity livingTarget) {
                            livingTarget.addStatusEffect(new StatusEffectInstance(Antiquities.BOUNCE_EFFECT, 30, 0, true, true));
                        }
                    }



                    // Manually call the attack logic after the delay
                    if (target.isAttackable()) {
                        if (!target.handleAttack(this)) {
                            float f = this.isUsingRiptide() ? this.riptideAttackDamage : (float)this.getAttributeValue(EntityAttributes.ATTACK_DAMAGE);
                            ItemStack itemStack = this.getWeaponStack();
                            DamageSource damageSource = (DamageSource) Optional.ofNullable(itemStack.getItem().getDamageSource(this)).orElse(this.getDamageSources().playerAttack(player));
                            float g = this.getDamageAgainst(target, f, damageSource) - f;
                            float h = this.getAttackCooldownProgress(0.5F);
                            f *= 0.2F + h * h * 0.8F;
                            g *= h;
                            this.resetLastAttackedTicks();
                            if (target.getType().isIn(EntityTypeTags.REDIRECTABLE_PROJECTILE)
                                    && target instanceof ProjectileEntity projectileEntity
                                    && projectileEntity.deflect(ProjectileDeflection.REDIRECTED, this, this, true)) {
                                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, this.getSoundCategory());
                                return;
                            }

                            if (f > 0.0F || g > 0.0F) {
                                boolean bl = h > 0.9F;
                                boolean bl2;
                                if (this.isSprinting() && bl) {
                                    this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, this.getSoundCategory(), 1.0F, 1.0F);
                                    bl2 = true;
                                } else {
                                    bl2 = false;
                                }

                                f += itemStack.getItem().getBonusAttackDamage(target, f, damageSource);
                                boolean bl3 = bl
                                        && this.fallDistance > 0.0F
                                        && !this.isOnGround()
                                        && !this.isClimbing()
                                        && !this.isTouchingWater()
                                        && !this.hasStatusEffect(StatusEffects.BLINDNESS)
                                        && !this.hasVehicle()
                                        && target instanceof LivingEntity
                                        && !this.isSprinting();
                                if (bl3) {
                                    f *= 1.5F;
                                }

                                float i = f + g;
                                boolean bl4 = false;
                                if (bl && !bl3 && !bl2 && this.isOnGround()) {
                                    double d = this.getMovement().horizontalLengthSquared();
                                    double e = (double)this.getMovementSpeed() * 2.5;
                                    if (d < MathHelper.square(e) && this.getStackInHand(Hand.MAIN_HAND).isIn(ItemTags.SWORDS)) {
                                        bl4 = true;
                                    }
                                }

                                float j = 0.0F;
                                if (target instanceof LivingEntity livingEntity) {
                                    j = livingEntity.getHealth();
                                }

                                Vec3d vec3d = target.getVelocity();
                                boolean bl5 = target.sidedDamage(damageSource, i);
                                if (bl5) {
                                    float k = this.getAttackKnockbackAgainst(target, damageSource) + (bl2 ? 1.0F : 0.0F);
                                    if (k > 0.0F) {
                                        if (target instanceof LivingEntity livingEntity2) {
                                            livingEntity2.takeKnockback(
                                                    (double)(k * 0.5F),
                                                    (double)MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0)),
                                                    (double)(-MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0)))
                                            );
                                        } else {
                                            target.addVelocity(
                                                    (double)(-MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0)) * k * 0.5F),
                                                    0.1,
                                                    (double)(MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0)) * k * 0.5F)
                                            );
                                        }

                                        this.setVelocity(this.getVelocity().multiply(0.6, 1.0, 0.6));
                                        this.setSprinting(false);
                                    }

                                    if (bl4) {
                                        float l = 1.0F + (float)this.getAttributeValue(EntityAttributes.SWEEPING_DAMAGE_RATIO) * f;

                                        for (LivingEntity livingEntity3 : this.getWorld().getNonSpectatingEntities(LivingEntity.class, target.getBoundingBox().expand(1.0, 0.25, 1.0))) {
                                            if (livingEntity3 != this
                                                    && livingEntity3 != target
                                                    && !this.isTeammate(livingEntity3)
                                                    && (!(livingEntity3 instanceof ArmorStandEntity) || !((ArmorStandEntity)livingEntity3).isMarker())
                                                    && this.squaredDistanceTo(livingEntity3) < 9.0) {
                                                float m = this.getDamageAgainst(livingEntity3, l, damageSource) * h;
                                                livingEntity3.takeKnockback(
                                                        0.4F, (double)MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0)), (double)(-MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0)))
                                                );
                                                livingEntity3.serverDamage(damageSource, m);
                                                if (this.getWorld() instanceof ServerWorld serverWorld1) {
                                                    EnchantmentHelper.onTargetDamaged(serverWorld1, livingEntity3, damageSource);
                                                }
                                            }
                                        }

                                        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, this.getSoundCategory(), 1.0F, 1.0F);
                                        this.spawnSweepAttackParticles();
                                    }

                                    if (target instanceof ServerPlayerEntity && target.velocityModified) {
                                        ((ServerPlayerEntity)target).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(target));
                                        target.velocityModified = false;
                                        target.setVelocity(vec3d);
                                    }

                                    if (bl3) {
                                        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, this.getSoundCategory(), 1.0F, 1.0F);
                                        this.addCritParticles(target);
                                    }

                                    if (!bl3 && !bl4) {
                                        if (bl) {
                                            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, this.getSoundCategory(), 1.0F, 1.0F);
                                        } else {
                                            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, this.getSoundCategory(), 1.0F, 1.0F);
                                        }
                                    }

                                    if (g > 0.0F) {
                                        this.addEnchantedHitParticles(target);
                                    }

                                    this.onAttacking(target);
                                    Entity entity = target;
                                    if (target instanceof EnderDragonPart) {
                                        entity = ((EnderDragonPart)target).owner;
                                    }

                                    boolean bl6 = false;
                                    if (this.getWorld() instanceof ServerWorld serverWorld2) {
                                        if (entity instanceof LivingEntity livingEntity3x) {
                                            bl6 = itemStack.postHit(livingEntity3x, this);
                                        }

                                        EnchantmentHelper.onTargetDamaged(serverWorld2, target, damageSource);
                                    }

                                    if (!this.getWorld().isClient && !itemStack.isEmpty() && entity instanceof LivingEntity) {
                                        if (bl6) {
                                            itemStack.postDamageEntity((LivingEntity)entity, this);
                                        }

                                        if (itemStack.isEmpty()) {
                                            if (itemStack == this.getMainHandStack()) {
                                                this.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                                            } else {
                                                this.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
                                            }
                                        }
                                    }

                                    if (target instanceof LivingEntity) {
                                        float n = j - ((LivingEntity)target).getHealth();
                                        this.increaseStat(Stats.DAMAGE_DEALT, Math.round(n * 10.0F));
                                        if (this.getWorld() instanceof ServerWorld && n > 2.0F) {
                                            int o = (int)((double)n * 0.5);
                                            ((ServerWorld)this.getWorld())
                                                    .spawnParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getBodyY(0.5), target.getZ(), o, 0.1, 0.0, 0.1, 0.2);
                                        }
                                    }

                                    this.addExhaustion(0.1F);
                                } else {
                                    this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, this.getSoundCategory(), 1.0F, 1.0F);
                                }
                            }
                        }
                    }
                });

                ci.cancel(); // Prevent the original attack from happening immediately
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "attack")
    private void attackWithPauldrons(Entity target, CallbackInfo ci) {

        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack stack = player.getEquippedStack(EquipmentSlot.CHEST);
        if (stack.getItem() instanceof NetheritePauldronsItem && target instanceof ProjectileEntity entity) {
            player.getEntityWorld().playSound(player, player.getX(), player.getY(), player.getZ(),
                    ModSounds.PARRY_ULTRAKILL, SoundCategory.PLAYERS, 1.0F, 1.0F);
            Vec3d velocity = player.getRotationVec(0).normalize();
            if (Objects.equals(entity.getOwner(), player)) {
                target.setVelocity(velocity.x * 1.5F * target.getVelocity().length(), velocity.y * 1.5F * target.getVelocity().length(), velocity.z * 1.5F * target.getVelocity().length());
            } else {
                target.setVelocity(velocity.x * 2.0F, velocity.y * 2.0F, velocity.z * 2.0F);
            }
            target.velocityModified = true;
            target.velocityDirty = true;
            FreezeFrameManager.triggerFreeze(6);
        }
    }

    @Inject(at = @At("HEAD"), method = "attack")
    private void attackWithStaff(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getStackInHand(Hand.MAIN_HAND).isOf(ModItems.MYRIAD_STAFF)) {
            float attackPower = player.getAttackCooldownProgress(0.0f);
            player.getEntityWorld().playSound(player, player.getX(), player.getY(), player.getZ(),
                    attackPower > 0.9f ? SoundEvents.ITEM_MACE_SMASH_AIR : SoundEvents.BLOCK_HEAVY_CORE_PLACE, SoundCategory.PLAYERS, 1.0F, 1.3F);
        }
    }

    @Inject(at = @At("TAIL"), method = "resetLastAttackedTicks")
    private void swingWithStaff(CallbackInfo ci) {
        ItemStack itemStack = this.getMainHandStack();
        if (!ItemStack.areEqual(this.selectedItem, itemStack)) {
            if (!ItemStack.areItemsEqual(this.selectedItem, itemStack)) {
                return;
            }
        }

        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getStackInHand(Hand.MAIN_HAND).isOf(ModItems.MYRIAD_STAFF)) {
            player.getEntityWorld().playSound(player, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.0F, 0.6F);
        }
    }
}