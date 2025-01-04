package net.hollowed.antique.mixin;

import net.hollowed.antique.items.custom.VelocityTransferMaceItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("HEAD"), method = "attack")
    private void attackWithScepter(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        float attackPower = player.getAttackCooldownProgress(0.0f);
        ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
        if (stack.getItem() instanceof VelocityTransferMaceItem item) {
            if (!(target instanceof LivingEntity)) {
                item.postEntityHit(target, this);
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
            } else {
                player.getEntityWorld().playSound(player, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BLOCK_HEAVY_CORE_PLACE, SoundCategory.PLAYERS, 1.0F, 1.3F);
            }
        }
    }
}