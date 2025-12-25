package net.hollowed.antique.index;

import net.hollowed.antique.Antiquities;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class AntiqueSounds {
    public static SoundEvent FIRECRACKER = register("firecracker");
    public static SoundEvent CAKE_SPLAT = register("cake_splat");
    public static SoundEvent STAFF_INSERT = register("staff_insert");
    public static SoundEvent STAFF_REMOVE = register("staff_remove");
    public static SoundEvent STAFF_HIT = register("staff_hit");
    @SuppressWarnings("unused")
    public static SoundEvent WARHORN = register("warhorn");

    private static SoundEvent register(String id) {
        return register(Identifier.fromNamespaceAndPath(Antiquities.MOD_ID, id));
    }

    private static SoundEvent register(Identifier id) {
        return register(id, id);
    }

    private static SoundEvent register(Identifier id, Identifier soundId) {
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(soundId));
    }

    public static void initialize() {}
}
