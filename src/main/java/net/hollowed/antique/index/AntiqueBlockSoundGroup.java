package net.hollowed.antique.index;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;

public class AntiqueBlockSoundGroup extends SoundType {
    public AntiqueBlockSoundGroup(float volume, float pitch, SoundEvent breakSound, SoundEvent stepSound, SoundEvent placeSound, SoundEvent hitSound, SoundEvent fallSound) {
        super(volume, pitch, breakSound, stepSound, placeSound, hitSound, fallSound);
    }

    public static final SoundType HOLLOW_CORE = new SoundType(1.0F, 1.7F, SoundEvents.HEAVY_CORE_BREAK, SoundEvents.HEAVY_CORE_STEP, SoundEvents.HEAVY_CORE_PLACE, SoundEvents.HEAVY_CORE_HIT, SoundEvents.HEAVY_CORE_FALL);
}
