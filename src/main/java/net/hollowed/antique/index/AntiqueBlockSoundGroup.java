package net.hollowed.antique.index;

import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class AntiqueBlockSoundGroup extends BlockSoundGroup {
    public AntiqueBlockSoundGroup(float volume, float pitch, SoundEvent breakSound, SoundEvent stepSound, SoundEvent placeSound, SoundEvent hitSound, SoundEvent fallSound) {
        super(volume, pitch, breakSound, stepSound, placeSound, hitSound, fallSound);
    }

    public static final BlockSoundGroup HOLLOW_CORE = new BlockSoundGroup(1.0F, 1.7F, SoundEvents.BLOCK_HEAVY_CORE_BREAK, SoundEvents.BLOCK_HEAVY_CORE_STEP, SoundEvents.BLOCK_HEAVY_CORE_PLACE, SoundEvents.BLOCK_HEAVY_CORE_HIT, SoundEvents.BLOCK_HEAVY_CORE_FALL);
}
