package net.hollowed.antique;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static SoundEvent PARRY_ULTRAKILL = register("parry_ultrakill");
    public static SoundEvent FIRECRACKER = register("firecracker");
    @SuppressWarnings("unused")
    public static SoundEvent WARHORN = register("warhorn");

    private static SoundEvent register(String id) {
        return register(Identifier.of(Antiquities.MOD_ID, id));
    }

    private static SoundEvent register(Identifier id) {
        return register(id, id);
    }

    private static SoundEvent register(Identifier id, Identifier soundId) {
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(soundId));
    }

    public static void initialize() {

    }
}
