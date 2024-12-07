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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

public class VelocityTransferMaceItem extends Item {

    private static final Map<UUID, Queue<Vec3d>> previousPositions = new HashMap<>();
    private static final int POSITION_HISTORY_SIZE = 5;

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
                    float damage = (float) (Math.abs(effectiveVelocity.length()) * 3.5);
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
                    }

                    if (target instanceof PlayerEntity && !EnchantmentListener.hasCustomEnchantment(stack, "antique:kinematic")) {
                        target.addStatusEffect(new StatusEffectInstance(Antiquities.VOLATILE_BOUNCE_EFFECT, 30, 0, true, true));
                    } else {
                        target.addStatusEffect(new StatusEffectInstance(Antiquities.BOUNCE_EFFECT, 30, 0, true, true));
                    }
                }
            }

            // Store the current position for the next tick
            previousPositions.put(playerId, positionHistory);
        }

        previousPositions.clear();
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
