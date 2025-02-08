package net.hollowed.antique.mixin;

import net.hollowed.antique.ModSounds;
import net.hollowed.antique.items.custom.NetheritePauldronsItem;
import net.hollowed.antique.items.custom.VelocityTransferMaceItem;
import net.hollowed.antique.util.FreezeFrameManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(PlayerEntity.class)
public abstract class AttackNonlivingEntityHandler extends LivingEntity {

    @Shadow public abstract void setFireTicks(int fireTicks);

    protected AttackNonlivingEntityHandler(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("HEAD"), method = "attack", cancellable = true)
    private void attackWithScepter(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        float attackPower = player.getAttackCooldownProgress(0.0f);
        ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
        if (stack.getItem() instanceof VelocityTransferMaceItem item) {
            if (attackPower > 0.9f) {
                float pitch = 1.1f + (player.getRandom().nextFloat() * .2f);

                if (!target.isOnGround()) {
                    player.getEntityWorld().playSound(player, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ITEM_MACE_SMASH_AIR, SoundCategory.PLAYERS, 1.0F, 1.3F);
                } else {
                    player.getEntityWorld().playSound(player, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ITEM_MACE_SMASH_GROUND, SoundCategory.PLAYERS, 1.0F, pitch);
                }
            } else {
                player.getEntityWorld().playSound(player, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BLOCK_HEAVY_CORE_PLACE, SoundCategory.PLAYERS, 1.0F, 1.3F);
            }
            if (!(target instanceof LivingEntity)) {
                item.postEntityHit(target, this);
                ci.cancel();
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
            FreezeFrameManager.triggerFreeze(6, true);
        }
    }
}