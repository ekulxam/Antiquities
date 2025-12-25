package net.hollowed.antique.mixin.entities.living;

import net.hollowed.antique.util.interfaces.duck.ShieldPiercer;
import net.hollowed.combatamenities.util.entities.ModDamageTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class ShieldPierceMixin {
    @Shadow public abstract boolean hurtServer(ServerLevel world, DamageSource source, float amount);

    @Shadow public abstract float applyItemBlocking(ServerLevel world, DamageSource source, float amount);

    @Unique
    private boolean ran = false;

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    public void pierceShield(ServerLevel world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Entity entity = source.getEntity();
        if (!this.ran) {
            this.ran = true;
            if (entity instanceof LivingEntity attacker && this.applyItemBlocking(world, source, amount) > 0) {
                Item stack = attacker.getMainHandItem().getItem();
                if (stack instanceof ShieldPiercer access) {
                    float percent = access.shieldPierce();
                    this.hurtServer(world, ModDamageTypes.of(world, ModDamageTypes.CLEAVED, attacker), amount * percent);
                    attacker.level().playSound(null, attacker.blockPosition(), SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, SoundSource.PLAYERS);
                    cir.setReturnValue(false);
                }
            }
        } else {
            this.ran = false;
        }
    }
}
