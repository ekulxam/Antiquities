package net.hollowed.antique.mixin.accessors;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Mob.class)
public interface CanRemoveSaddleAccessor {
    @Invoker("canShearEquipment")
    boolean canRemoveSaddle(Player player);
}