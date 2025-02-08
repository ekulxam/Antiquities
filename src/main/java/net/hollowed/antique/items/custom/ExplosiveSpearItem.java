package net.hollowed.antique.items.custom;

import net.hollowed.antique.entities.custom.ExplosiveSpearEntity;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ExplosiveSpearItem extends Item {

    public ExplosiveSpearItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity playerEntity) {
            RegistryEntry<SoundEvent> registryEntry = EnchantmentHelper.getEffect(stack, EnchantmentEffectComponentTypes.TRIDENT_SOUND)
                    .orElse(SoundEvents.ITEM_TRIDENT_THROW);
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
            if (world instanceof ServerWorld serverWorld) {
                stack.damage(1, playerEntity);
                float multiplier = 1.0F;
                Vec3d look = playerEntity.getRotationVec(0).multiply(multiplier, multiplier, multiplier);

                // Calculate a position slightly in front of the player
                Vec3d spawnPos = playerEntity.getPos().add(0, 1.6, 0).add(look);

                // Create and spawn the MyriadShovelEntity at the calculated position
                ExplosiveSpearEntity tridentEntity = ProjectileEntity.spawnWithVelocity(ExplosiveSpearEntity::new, serverWorld, stack, playerEntity, 0.0F, 2.5F, 1.0F);
                tridentEntity.setPosition(spawnPos);

                if (playerEntity.isInCreativeMode()) {
                    tridentEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                } else {
                    playerEntity.getInventory().removeOne(stack);
                }

                world.playSoundFromEntity(null, tridentEntity, registryEntry.value(), SoundCategory.PLAYERS, 1.0F, 1.0F);
                return true;
            }
        }
        return false;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return ActionResult.PASS;
    }
}
