package net.hollowed.antique.mixin.items;

import net.hollowed.antique.index.AntiqueDataComponentTypes;
import net.hollowed.antique.index.AntiqueItems;
import net.hollowed.antique.items.MyriadToolItem;
import net.hollowed.combatamenities.index.CAParticles;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MyriadAxeBlockMixin extends Entity implements Attackable {

    @Shadow public abstract boolean isUsingItem();

    @Shadow protected ItemStack activeItemStack;

    @Shadow protected int itemUseTimeLeft;

    @Unique
    private boolean ran = false;

    public MyriadAxeBlockMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    public void blockWithMyriadAxe(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        Vec3d attackDirection = source.getPosition() != null ? source.getPosition().subtract(self.getPos()).normalize() : Vec3d.ZERO;
        if (attackDirection.equals(Vec3d.ZERO)) {
            attackDirection = new Vec3d(0, -1, 0);
        }

        Vec3d lookDirection = self.getRotationVec(1.0F).normalize();
        double angle = attackDirection.dotProduct(lookDirection);

        if (this.getAxeBlockingItem() != null && (angle > 0.0F) && !ran
            && !(source.isIn(DamageTypeTags.IS_PROJECTILE)) && (!source.isIn(DamageTypeTags.BYPASSES_SHIELD) || source.isOf(DamageTypes.FALL)) && !source.isIn(DamageTypeTags.IS_FIRE)) {

            float reducedDamage = amount * 0.5F;
            if (self instanceof PlayerEntity player) {
                player.getWorld().playSound(null, self.getBlockPos(), SoundEvents.BLOCK_HEAVY_CORE_PLACE, SoundCategory.PLAYERS, 1.0F, 1.2F);
                player.getWorld().playSound(null, self.getBlockPos(), SoundEvents.ITEM_SHIELD_BLOCK.value(), SoundCategory.PLAYERS, 0.25F, 1.2F);
                if (source.getSource() instanceof LivingEntity attacker) {
                    Vec3d knockbackDirection = attacker.getPos().subtract(player.getPos()).normalize();
                    attacker.takeKnockback(0.25, -knockbackDirection.x, -knockbackDirection.z);
                    attacker.velocityModified = true;
                    attacker.velocityDirty = true;
                }
            }
            this.ran = true;
            self.damage(world, source, reducedDamage);

            if (amount > 20.0F) {
                if (self instanceof ServerPlayerEntity player) {
                    player.getItemCooldownManager().set(player.getActiveItem(), 20);
                    self.stopUsingItem();
                }
            }

            double d = -MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0)) * 1.5;
            double e = MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0)) * 1.5;
            world.spawnParticles(CAParticles.RING, this.getX() + d, this.getBodyY(0.5) + 0.25, this.getZ() + e, 0, d, 0.0, e, 0.0);

            cir.setReturnValue(false);
        } else {
            if (source.isIn(DamageTypeTags.IS_PROJECTILE) && this.getAxeBlockingItem() != null && (angle > 0.0F)) {
                self.getWorld().playSound(null, self.getBlockPos(), SoundEvents.BLOCK_HEAVY_CORE_PLACE, SoundCategory.PLAYERS, 1.0F, 1.2F);
                self.getWorld().playSound(null, self.getBlockPos(), SoundEvents.ITEM_SHIELD_BLOCK.value(), SoundCategory.PLAYERS, 0.25F, 1.2F);
                if (source.getSource() != null) {
                    world.spawnParticles(CAParticles.RING, source.getSource().getX(), source.getSource().getY(), source.getSource().getZ(), 1, 0.0, 0.0, 0.0, 0);
                }
                cir.setReturnValue(false);
            }
            this.ran = false;
        }
    }

    @Unique
    @Nullable
    public ItemStack getAxeBlockingItem() {
        if (this.isUsingItem() && !this.activeItemStack.isEmpty()) {
            Item item = this.activeItemStack.getItem();
            if (!(item instanceof MyriadToolItem && this.activeItemStack.getOrDefault(AntiqueDataComponentTypes.MYRIAD_STACK, ItemStack.EMPTY).isOf(AntiqueItems.MYRIAD_AXE_HEAD))) {
                return null;
            } else {
                return item.getMaxUseTime(this.activeItemStack, (LivingEntity) (Object) this) - this.itemUseTimeLeft < 5 ? null : this.activeItemStack;
            }
        } else {
            return null;
        }
    }
}
