package net.hollowed.antique.items;

import net.hollowed.antique.enchantments.EnchantmentListener;
import net.hollowed.antique.index.AntiqueEffects;
import net.minecraft.block.BlockState;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ScepterItem extends Item {

    private static Vec3d startTickPosition;
    public static Vec3d playerVelocity = new Vec3d(0, 0, 0);
    public static Vec3d lastHitVelocity = new Vec3d(0, 0, 0);

    public ScepterItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canMine(ItemStack stack, BlockState state, World world, BlockPos pos, LivingEntity user) {
        return !user.isInCreativeMode();
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

        Vec3d multiplier;

        if (EnchantmentListener.hasEnchantment(stack, "antique:kinematic") || EnchantmentListener.hasEnchantment(stack, "antique:impetus")) {
            if (EnchantmentListener.hasEnchantment(stack, "antique:kinematic")) {
                multiplier = new Vec3d(-3, -1.5, -3);
            } else {
                multiplier = new Vec3d(3, -1, 3);
            }

            int chargeTime = user.getItemUseTime();

            // Check if the player is targeting a block
            HitResult hitResult = player.raycast(5.0D, 0.0F, false);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                // Interact with a block
                float velocity = Math.clamp(chargeTime * 0.04F + 0.25F, EnchantmentListener.hasEnchantment(stack, "antique:impetus") ? 0.6F : 1.0F, 2.0F);

                player.addVelocity(player.getRotationVec(0).multiply(multiplier).multiply(velocity));
                player.velocityModified = true;
                if (user.getEntityWorld() instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(ParticleTypes.GUST, user.getX(), user.getY() + 0.25F, user.getZ(), 1, 0.1, 0.0, 0.1, 0);
                }

                world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_MACE_SMASH_AIR, SoundCategory.NEUTRAL, 1F, 0.9f + (player.getRandom().nextFloat() * .2f));
                player.incrementStat(Stats.USED.getOrCreateStat(this));
                user.swingHand(user.getActiveHand());
                if (!user.getEntityWorld().isClient()) {
                    player.getItemCooldownManager().set(stack, 130);
                    if (EnchantmentListener.hasEnchantment(stack, "antique:kinematic")) {
                        user.addStatusEffect(new StatusEffectInstance(AntiqueEffects.VOLATILE_BOUNCE_EFFECT, (int) Math.clamp((velocity - 0.625) * 40, 10, 10000), 0, true, true));
                    } else {
                        user.addStatusEffect(new StatusEffectInstance(AntiqueEffects.BOUNCE_EFFECT, (int) Math.clamp((velocity - 0.625) * 50, 10, 10000), 0, true, true));
                    }
                }
            } else {
                // Spawn a hitbox and interact with entities inside it
                Vec3d forwardVec = user.getRotationVec(1.0F); // Get the direction the player is facing
                Vec3d hitboxCenter = user.getEntityPos().add(forwardVec.multiply(2.0)); // Position hitbox 2 blocks ahead
                Box hitbox = new Box(hitboxCenter.subtract(1, 1, 1), hitboxCenter.add(1, 1, 1)); // 2x2x2 box

                // Get all entities in the hitbox, excluding the player
                List<Entity> entities = world.getOtherEntities(user, hitbox);

                for (Entity entity : entities) {
                    if (entity instanceof LivingEntity target) {
                        // Apply damage
                        float damage = Math.clamp((float) chargeTime * 0.1F, 0.0F, 10.0F);
                        if (target.getEntityWorld() instanceof ServerWorld serverWorld) {
                            target.damage(serverWorld,
                                    target.getEntityWorld().getDamageSources().playerAttack(player), damage);
                        }

                        // Knockback
                        Vec3d knockback = forwardVec.multiply(1.5); // Apply knockback in player's direction
                        target.addVelocity(knockback.x, knockback.y, knockback.z);
                        target.velocityModified = true;

                        // Apply a custom effect
                        if (EnchantmentListener.hasEnchantment(stack, "antique:kinematic")) {
                            target.addStatusEffect(new StatusEffectInstance(AntiqueEffects.VOLATILE_BOUNCE_EFFECT, 60, 0, true, true));
                        } else {
                            target.addStatusEffect(new StatusEffectInstance(AntiqueEffects.BOUNCE_EFFECT, 30, 0, true, true));
                        }
                    }
                }

                if (!entities.isEmpty()) world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_MACE_SMASH_AIR, SoundCategory.NEUTRAL, 1F, 1.0F);
                player.getItemCooldownManager().set(stack, 60);
                user.swingHand(user.getActiveHand());
            }
            return false;
        }
        return true;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return stack.hasEnchantments() ? 72000 : 0;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (EnchantmentListener.hasEnchantment(user.getStackInHand(hand), "antique:kinematic")) {
            user.setCurrentHand(hand);
            return ActionResult.PASS;
        } else if (EnchantmentListener.hasEnchantment(user.getStackInHand(hand), "antique:impetus")) {
            user.setCurrentHand(hand);
            return ActionResult.PASS;
        }

        user.swingHand(hand);
        HitResult hitResult = user.raycast(5.0D, 0.0F, false);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            user.getItemCooldownManager().set(user.getStackInHand(hand), 40);
            user.addVelocity(user.getRotationVec(0).multiply(new Vec3d(-1.5, -0.5, -1.5)));
            user.velocityModified = true;
            if (user.getEntityWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.GUST, user.getX(), user.getY() + 0.25F, user.getZ(), 1, 0.1, 0.0, 0.1, 0);
                user.addStatusEffect(new StatusEffectInstance(AntiqueEffects.VOLATILE_BOUNCE_EFFECT, 30, 0, true, true));
            }

            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_MACE_SMASH_AIR, SoundCategory.NEUTRAL, 1F, 0.9f + (user.getRandom().nextFloat() * .2f));
            user.incrementStat(Stats.USED.getOrCreateStat(this));
        } else {
            user.getItemCooldownManager().set(user.getStackInHand(hand), 20);
        }

        return ActionResult.PASS;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        if (EnchantmentListener.hasEnchantment(stack, "antique:impetus")) {
            return UseAction.SPEAR;
        }
        return UseAction.NONE;
    }

    @Override
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        ScepterItem.lastHitVelocity = playerVelocity;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        if (!world.isClient() && entity instanceof PlayerEntity player) {

            // Track the start position of the tick
            if (startTickPosition == null) {
                startTickPosition = player.getEntityPos();
            }

            // Calculate velocity at the end of the tick (this will be after position has changed)
            Vec3d endTickPosition = player.getEntityPos();

            // Check if there has been a position change
            if (!startTickPosition.equals(endTickPosition)) {
                // Calculate velocity as the difference between start and end position
                playerVelocity = endTickPosition.subtract(startTickPosition);
            }

            // Optionally, store the velocity for later use or apply any logic
            // For example, updating player velocity based on the calculated velocity
            player.setVelocity(playerVelocity);

            // Update start position for the next tick
            startTickPosition = endTickPosition;
        }
    }
}
