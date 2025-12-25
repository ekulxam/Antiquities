package net.hollowed.antique.mixin.accessors;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Mob.class)
public interface MobEntitySoundAccessor {
    @Invoker("getAmbientSound")
    SoundEvent invokeGetAmbientSound();
}
