package net.hollowed.antique.mixin.entities.living;

import net.hollowed.antique.entities.IllusionerEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import net.minecraft.world.entity.monster.illager.SpellcasterIllager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpellcasterIllager.class)
public abstract class SpellCastingIllagerMixin extends AbstractIllager {

    protected SpellCastingIllagerMixin(EntityType<? extends AbstractIllager> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/illager/SpellcasterIllager;isCastingSpell()Z"), cancellable = true)
    private void tick(CallbackInfo ci) {
        if ((SpellcasterIllager) (Object) this instanceof IllusionerEntity) {
            ci.cancel();
        }
    }
}
