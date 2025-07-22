package net.hollowed.antique.mixin.entities.living;

import net.hollowed.antique.util.interfaces.duck.ShieldPiercer;
import net.hollowed.combatamenities.util.entities.ModDamageTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class ShieldPierceMixin {
    @Shadow public abstract boolean damage(ServerWorld world, DamageSource source, float amount);

    @Shadow public abstract float getDamageBlockedAmount(ServerWorld world, DamageSource source, float amount);

    @Unique
    private boolean ran = false;

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    public void pierceShield(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Entity entity = source.getAttacker();
        if (!this.ran) {
            this.ran = true;
            if (entity instanceof LivingEntity attacker && this.getDamageBlockedAmount(world, source, amount) > 0) {
                Item stack = attacker.getMainHandStack().getItem();
                if (stack instanceof ShieldPiercer access) {
                    float percent = access.shieldPierce();
                    this.damage(world, ModDamageTypes.of(world, ModDamageTypes.CLEAVED, attacker), amount * percent);
                    attacker.getWorld().playSound(null, attacker.getBlockPos(), SoundEvents.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, SoundCategory.PLAYERS);
                    cir.setReturnValue(false);
                }
            }
        } else {
            this.ran = false;
        }
    }
}
