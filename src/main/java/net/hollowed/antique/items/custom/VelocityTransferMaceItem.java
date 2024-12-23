package net.hollowed.antique.items.custom;

import net.hollowed.antique.Antiquities;
import net.hollowed.antique.enchantments.EnchantmentListener;
import net.minecraft.block.BlockState;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.consume.UseAction;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

public class VelocityTransferMaceItem extends Item {

    private static final Map<UUID, Queue<Vec3d>> previousPositions = new HashMap<>();
    private static final int POSITION_HISTORY_SIZE = 10;

    public VelocityTransferMaceItem(Settings settings) {
        super(settings);
    }

    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    public static AttributeModifiersComponent createAttributeModifiers() {
        return AttributeModifiersComponent.builder()
                .add(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, 5.0, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
                .add(EntityAttributes.ATTACK_SPEED, new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, -2.2, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
                .add(EntityAttributes.ENTITY_INTERACTION_RANGE, new EntityAttributeModifier(Identifier.ofVanilla("base_attack_range"), 0.75, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
                .build();
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) {
            return false;
        }

        Vec3d multiplier = new Vec3d(0, 0, 0);

        if (EnchantmentListener.hasCustomEnchantment(stack, "antique:kinematic") || EnchantmentListener.hasCustomEnchantment(stack, "antique:impetus")) {
            if (EnchantmentListener.hasCustomEnchantment(stack, "antique:kinematic")) {
                multiplier = new Vec3d(-1, -1, -1);
            } else {
                multiplier = new Vec3d(1, -1, 1);
            }

            int chargeTime = user.getItemUseTime();

            // Check if the player is targeting a block
            HitResult hitResult = player.raycast(5.0D, 0.0F, false);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                // Interact with a block
                float velocity = Math.clamp(chargeTime * 0.015F + 1F, 1.0F, 4.0F);

                if (!world.isClient) {
                    player.setVelocity(player.getRotationVec(0).multiply(multiplier).multiply(velocity));
                    player.velocityModified = true;
                }

                world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_MACE_SMASH_AIR, SoundCategory.NEUTRAL, 1F, 0.9f + (player.getRandom().nextFloat() * .2f));
                player.incrementStat(Stats.USED.getOrCreateStat(this));
                user.swingHand(user.getActiveHand());
                if (!user.getWorld().isClient) {
                    ((PlayerEntity) user).getItemCooldownManager().set(stack, 40);
                    if (EnchantmentListener.hasCustomEnchantment(stack, "antique:kinematic")) {
                        user.addStatusEffect(new StatusEffectInstance(Antiquities.VOLATILE_BOUNCE_EFFECT, (int) velocity * 30, 0, true, true));
                    } else {
                        user.addStatusEffect(new StatusEffectInstance(Antiquities.BOUNCE_EFFECT, (int) velocity * 30, 0, true, true));
                    }
                }
                System.out.println("hit: " + hitResult);
                return false;
            } else {
                // Spawn a hitbox and interact with entities inside it
                Vec3d forwardVec = user.getRotationVec(1.0F); // Get the direction the player is facing
                Vec3d hitboxCenter = user.getPos().add(forwardVec.multiply(2.0)); // Position hitbox 2 blocks ahead
                Box hitbox = new Box(hitboxCenter.subtract(1, 1, 1), hitboxCenter.add(1, 1, 1)); // 2x2x2 box

                // Get all entities in the hitbox, excluding the player
                List<Entity> entities = world.getOtherEntities(user, hitbox, entity -> entity instanceof LivingEntity);

                for (Entity entity : entities) {
                    if (entity instanceof LivingEntity target) {
                        // Apply damage
                        float damage = Math.clamp((float) chargeTime * 0.1F, 0.0F, 10.0F);
                        if (target.getWorld() instanceof ServerWorld serverWorld) {
                            target.damage(serverWorld,
                                    target.getWorld().getDamageSources().playerAttack(player), damage);
                        }

                        // Knockback
                        Vec3d knockback = forwardVec.multiply(1.5); // Apply knockback in player's direction
                        target.addVelocity(knockback.x, knockback.y, knockback.z);
                        target.velocityModified = true;

                        // Apply a custom effect
                        if (EnchantmentListener.hasCustomEnchantment(stack, "antique:kinematic")) {
                            target.addStatusEffect(new StatusEffectInstance(Antiquities.VOLATILE_BOUNCE_EFFECT, 60, 0, true, true));
                        } else {
                            target.addStatusEffect(new StatusEffectInstance(Antiquities.BOUNCE_EFFECT, 30, 0, true, true));
                        }
                    }
                }

                // Play a sound effect and set cooldown
                if (!entities.isEmpty()) {
                    world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_MACE_SMASH_AIR, SoundCategory.NEUTRAL, 1F, 1.0F);
                    ((PlayerEntity) user).getItemCooldownManager().set(stack, 40);
                }
                user.swingHand(user.getActiveHand());
                System.out.println("miss: " + hitResult);
                return false;
            }
        }
        return true;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (EnchantmentListener.hasCustomEnchantment(user.getStackInHand(hand), "antique:kinematic")) {
            user.setCurrentHand(hand);
            return ActionResult.PASS;
        } else if (EnchantmentListener.hasCustomEnchantment(user.getStackInHand(hand), "antique:impetus")) {
            user.setCurrentHand(hand);
            return ActionResult.PASS;
        }
        return ActionResult.FAIL;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        if (EnchantmentListener.hasCustomEnchantment(stack, "antique:impetus")) {
            return UseAction.BOW;
        }
        return UseAction.NONE;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {

        if (!attacker.getWorld().isClient() && attacker instanceof PlayerEntity player) {

            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
            ServerWorld serverWorld = serverPlayerEntity.getServerWorld();

            UUID playerId = player.getUuid();

            // Ensure there's a queue to store previous positions
            Queue<Vec3d> positionHistory = previousPositions.computeIfAbsent(playerId, k -> new LinkedList<>());

            // Get the current position and add it to the history
            Vec3d currentPosition = player.getPos();
            positionHistory.add(currentPosition);

            // Limit the history to the last N positions (POSITION_HISTORY_SIZE)
            if (positionHistory.size() > POSITION_HISTORY_SIZE) {
                positionHistory.poll();
            }

            // Only compute velocity if we have enough data (at least 2 positions)
            if (positionHistory.size() > 1) {
                // Get the oldest position (i.e., the one from a few ticks ago)
                Vec3d previousPosition = positionHistory.peek();

                // Calculate velocity
                Vec3d effectiveVelocity = currentPosition.subtract(previousPosition);

                if (effectiveVelocity.length() > 0.1) { // Adjust the threshold if needed

                    // Apply damage based on the velocity magnitude
                    float damage = Math.clamp((float) (Math.abs(effectiveVelocity.length()) * 3.5), 0, 30);
                    target.damage((ServerWorld) attacker.getWorld(),
                            attacker.getWorld().getDamageSources().playerAttack(player), damage);

                    // Apply velocity to the target
                    Vec3d targetVelocity = effectiveVelocity.multiply(2, 1.5, 2); // Double the velocity

                    if (target instanceof PlayerEntity || target instanceof ServerPlayerEntity) {
                        targetVelocity.multiply(2, 0.2, 2);
                    }

                    targetVelocity.multiply(0.9);

                    // Find and apply reduced velocity to nearby entities
                    double radius = 5.0; // Radius to check for nearby entities
                    List<Entity> nearbyEntities = serverWorld.getOtherEntities(
                            target, target.getBoundingBox().expand(radius));

                    for (Entity nearby : nearbyEntities) {
                        if (nearby instanceof LivingEntity && nearby != target && nearby != attacker) {
                            double distance = target.getPos().distanceTo(nearby.getPos());
                            if (distance <= radius) {
                                double scalingFactor = 1 - (distance / radius); // Closer entities get more velocity
                                Vec3d reducedVelocity = effectiveVelocity.multiply(scalingFactor); // Reduce strength
                                nearby.setVelocity(reducedVelocity);
                                nearby.velocityModified = true;
                            }
                        }
                    }

                    target.setVelocity(targetVelocity);
                    target.velocityModified = true;

                    // Reset attacker's velocity
                    player.setVelocity(0, 0.01, 0);
                    player.velocityModified = true;

                    if (EnchantmentListener.hasCustomEnchantment(stack, "antique:kinematic")) {
                        Vec3d playerVelocity = effectiveVelocity.multiply(-1, -0.75, -1); // Double the velocity
                        player.setVelocity(playerVelocity);
                        player.velocityModified = true;

                        player.addStatusEffect(new StatusEffectInstance(Antiquities.BOUNCE_EFFECT, 30, 0, true, true));
                    } else if (EnchantmentListener.hasCustomEnchantment(stack, "antique:impetus")) {
                        Vec3d playerVelocity = effectiveVelocity.multiply(1, 0.75, 1); // Double the velocity
                        player.setVelocity(playerVelocity);
                        player.velocityModified = true;

                        player.addStatusEffect(new StatusEffectInstance(Antiquities.VOLATILE_BOUNCE_EFFECT, 30, 0, true, true));
                    }

                    if (target instanceof PlayerEntity && !EnchantmentListener.hasCustomEnchantment(stack, "antique:kinematic")) {
                        target.addStatusEffect(new StatusEffectInstance(Antiquities.VOLATILE_BOUNCE_EFFECT, 30, 0, true, true));
                    } else {
                        target.addStatusEffect(new StatusEffectInstance(Antiquities.BOUNCE_EFFECT, 30, 0, true, true));
                    }
                }
            }
        }
        return super.postHit(stack, target, attacker);
    }

    private static final Map<UUID, Long> lastResetTime = new HashMap<>();
    private static final int RESET_INTERVAL_TICKS = 6000; // Reset every 5 minutes (6000 ticks)

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient() && entity instanceof PlayerEntity) {
            UUID entityId = entity.getUuid();
            long currentTime = world.getTime();

            // Check if the history should be reset
            lastResetTime.putIfAbsent(entityId, currentTime);
            long lastReset = lastResetTime.get(entityId);

            if (currentTime - lastReset > RESET_INTERVAL_TICKS) {
                // Reset position history
                previousPositions.remove(entityId);
                lastResetTime.put(entityId, currentTime);
            }

            // Update position history
            Queue<Vec3d> positionHistory = previousPositions.computeIfAbsent(entityId, k -> new LinkedList<>());
            Vec3d currentPosition = entity.getPos();
            positionHistory.add(currentPosition);

            // Limit the history to the last N positions (POSITION_HISTORY_SIZE)
            if (positionHistory.size() > POSITION_HISTORY_SIZE) {
                positionHistory.poll();
            }
        }
    }
}
