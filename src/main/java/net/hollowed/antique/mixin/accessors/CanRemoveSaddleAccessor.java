package net.hollowed.antique.mixin.accessors;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MobEntity.class)
public interface CanRemoveSaddleAccessor {
    @Invoker("canRemoveSaddle")
    boolean canRemoveSaddle(PlayerEntity player);
}