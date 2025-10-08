package net.hollowed.antique.mixin.entities.living.player;

import net.hollowed.antique.index.AntiqueSounds;
import net.hollowed.antique.enchantments.EnchantmentListener;
import net.hollowed.antique.index.AntiqueEffects;
import net.hollowed.antique.index.AntiqueItems;
import net.hollowed.antique.items.ScepterItem;
import net.hollowed.antique.util.delay.TickDelayScheduler;
import net.hollowed.combatamenities.index.CAParticles;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
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

@Mixin(PlayerEntity.class)
public abstract class AttackNonlivingEntityHandler extends LivingEntity {

    @Shadow public abstract void setFireTicks(int fireTicks);

    @Shadow private ItemStack selectedItem;

    @Shadow public abstract void attack(Entity target);

    @Unique
    private boolean ranScepterAttack = false;

    @Unique
    private int parryCooldown = 0;

    protected AttackNonlivingEntityHandler(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("HEAD"), method = "attack", cancellable = true)
    private void attackWithScepter(Entity target, CallbackInfo ci) {
        if (!ranScepterAttack) {
            PlayerEntity player = (PlayerEntity) (Object) this;

            float attackPower = player.getAttackCooldownProgress(0.0f);
            ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);

            if (stack.getItem() instanceof ScepterItem) {
                player.getEntityWorld().playSound(player, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BLOCK_HEAVY_CORE_PLACE, SoundCategory.PLAYERS, 1.0F, 1.3F);

                if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(CAParticles.RING, target.getX(), target.getBodyY(0.6), target.getZ(), 1, 0.1, 0.2, 0.1, 0);
                }

                Vec3d effectiveVelocity = ScepterItem.playerVelocity;

                if (player.getVelocity().length() > 0.1) {
                    int delay = player.getVelocity().length() > 0.6 ? (int) (player.getVelocity().length() * 5F) : 0;
                    if (target instanceof net.hollowed.combatamenities.util.interfaces.EntityFreezer access) {
                        access.antiquities$setFrozen(true, delay - 1);
                    }
                    if (player instanceof net.hollowed.combatamenities.util.interfaces.EntityFreezer access) {
                        access.antiquities$setFrozen(true, delay - 1);
                    }
                    ranScepterAttack = true;
                    TickDelayScheduler.schedule(delay, () -> {
                        if (attackPower > 0.9f) {
                            float pitch = 1.1f + (player.getRandom().nextFloat() * .2f);
                            if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
                                serverWorld.spawnParticles(ParticleTypes.GUST_EMITTER_SMALL, target.getX(), target.getBodyY(0.5), target.getZ(), 1, 0.1, 0.0, 0.1, 0);
                            }

                            if (!target.isOnGround()) {
                                player.getEntityWorld().playSound(player, player.getX(), player.getY(), player.getZ(),
                                        SoundEvents.ITEM_MACE_SMASH_AIR, SoundCategory.PLAYERS, 1.0F, 1.3F);
                            } else {
                                player.getEntityWorld().playSound(player, player.getX(), player.getY(), player.getZ(),
                                        SoundEvents.ITEM_MACE_SMASH_GROUND, SoundCategory.PLAYERS, 1.0F, pitch);
                            }
                        }

                        // Scepter attack code
                        if (effectiveVelocity.length() > 0.1) {

                            float damage = Math.min((float) (effectiveVelocity.length() * 7.5 + 6), 30);
                            if (player.getEntityWorld() instanceof ServerWorld serverWorld) {
                                target.damage(serverWorld, player.getEntityWorld().getDamageSources().flyIntoWall(), damage);
                            }

                            Vec3d targetVelocity = effectiveVelocity.multiply(4, 2.5, 4);

                            if (target instanceof PlayerEntity || target instanceof ServerPlayerEntity) {
                                targetVelocity = targetVelocity.multiply(2, 0.2, 2);
                            }

                            target.setVelocity(targetVelocity.multiply(0.6 / targetVelocity.length() * targetVelocity.length()));
                            target.velocityModified = true;

                            double radius = 5.0;
                            List<Entity> nearbyEntities = player.getEntityWorld().getOtherEntities(
                                    target, target.getBoundingBox().expand(radius));

                            for (Entity nearby : nearbyEntities) {
                                if (nearby instanceof LivingEntity && nearby != target && nearby != player) {
                                    double distance = target.getEntityPos().distanceTo(nearby.getEntityPos());
                                    if (distance <= radius) {
                                        double scalingFactor = 2.5 - (distance / radius);
                                        Vec3d reducedVelocity = effectiveVelocity.multiply(scalingFactor);
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

                                player.addStatusEffect(new StatusEffectInstance(AntiqueEffects.BOUNCE_EFFECT, 30, 0, true, true));
                            } else if (EnchantmentListener.hasEnchantment(stack, "antique:impetus")) {
                                Vec3d playerVelocity = effectiveVelocity.multiply(1.5, 1.25, 1.5); // Enhance the velocity
                                player.setVelocity(playerVelocity.multiply(0.6 / targetVelocity.length() * targetVelocity.length()));
                                player.velocityModified = true;

                                player.addStatusEffect(new StatusEffectInstance(AntiqueEffects.VOLATILE_BOUNCE_EFFECT, 30, 0, true, true));
                            }

                            if (target instanceof PlayerEntity playerTarget && !EnchantmentListener.hasEnchantment(stack, "antique:kinematic")) {
                                playerTarget.addStatusEffect(new StatusEffectInstance(AntiqueEffects.VOLATILE_BOUNCE_EFFECT, 30, 0, true, true));
                            } else if (target instanceof LivingEntity livingTarget) {
                                livingTarget.addStatusEffect(new StatusEffectInstance(AntiqueEffects.BOUNCE_EFFECT, 30, 0, true, true));
                            }
                        }

                        this.attack(target);
                    });

                    ci.cancel();
                }
            }
        } else {
            ranScepterAttack = false;
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (this.parryCooldown > 0) {
            this.parryCooldown--;
        }
        if (this.getActiveItem().isOf(AntiqueItems.SCEPTER)) {
            float time = this.getItemUseTime() * 0.04F;
            if (time == 2.0F) {
                this.getEntityWorld().playSoundClient(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 1, 1);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "attack")
    private void attackWithPauldrons(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack stack = player.getEquippedStack(EquipmentSlot.CHEST);
        if (stack.isOf(AntiqueItems.NETHERITE_PAULDRONS) && target instanceof ProjectileEntity entity && this.parryCooldown <= 0) {
            this.parryCooldown = 5;
            if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.GUST, target.getX(), target.getBodyY(0.5), target.getZ(), 1, 0.1, 0.0, 0.1, 0);
            }

            player.getEntityWorld().playSound(player, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BLOCK_HEAVY_CORE_PLACE, SoundCategory.PLAYERS, 1.0F, 1.3F);
            player.getEntityWorld().playSound(player, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST, SoundCategory.PLAYERS, 1.0F, 0.7F);
            Vec3d velocity = player.getRotationVec(0).normalize();
            if (Objects.equals(entity.getOwner(), player)) {
                target.setVelocity(velocity.x * 1.5F * target.getVelocity().length(), velocity.y * 1.5F * target.getVelocity().length(), velocity.z * 1.5F * target.getVelocity().length());
            } else {
                target.setVelocity(velocity.x * 2.0F, velocity.y * 2.0F, velocity.z * 2.0F);
            }
            target.velocityModified = true;
            target.velocityDirty = true;
        }
    }

    @Inject(at = @At("HEAD"), method = "attack")
    private void attackWithStaff(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getStackInHand(Hand.MAIN_HAND).isOf(AntiqueItems.MYRIAD_STAFF)) {
            float attackPower = player.getAttackCooldownProgress(0.0f);
            player.getEntityWorld().playSound(player, player.getX(), player.getY(), player.getZ(),
                    attackPower > 0.9f ? AntiqueSounds.STAFF_HIT : SoundEvents.BLOCK_HEAVY_CORE_PLACE, SoundCategory.PLAYERS, 2.0F, attackPower > 0.9f ? (float) (1.0 + (Math.random() * 0.2) - 0.1) : 1.3F);
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
        if (player.getStackInHand(Hand.MAIN_HAND).isOf(AntiqueItems.MYRIAD_STAFF)) {
            player.getEntityWorld().playSound(player, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.0F, 0.6F);
        }
    }
}