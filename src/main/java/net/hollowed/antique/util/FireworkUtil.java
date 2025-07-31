package net.hollowed.antique.util;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;

import java.util.List;
import java.util.Random;

public class FireworkUtil {
    public static FireworksComponent randomFireworkBall() {
        Random random = new Random();

        // Generate 1–3 random colors
        IntList colors = IntArrayList.wrap(random.ints(1 + random.nextInt(3), 0, 0xFFFFFF + 1).toArray());

        // 50% chance of having fade colors
        IntList fadeColors = random.nextBoolean()
                ? IntArrayList.wrap(random.ints(1 + random.nextInt(2), 0, 0xFFFFFF + 1).toArray())
                : IntArrayList.of();

        FireworkExplosionComponent explosion = new FireworkExplosionComponent(
                FireworkExplosionComponent.Type.SMALL_BALL,
                colors,
                fadeColors,
                random.nextBoolean(), // hasTrail
                true  // hasTwinkle
        );

        return new FireworksComponent(-3, List.of(explosion));
    }

    public static FireworksComponent randomFirework() {
        Random random = new Random();

        // Generate 1–3 random colors
        IntList colors = IntArrayList.wrap(random.ints(1 + random.nextInt(3), 0, 0xFFFFFF + 1).toArray());

        // 50% chance of having fade colors
        IntList fadeColors = random.nextBoolean()
                ? IntArrayList.wrap(random.ints(1 + random.nextInt(2), 0, 0xFFFFFF + 1).toArray())
                : IntArrayList.of();

        FireworkExplosionComponent explosion = new FireworkExplosionComponent(
                randomType(),
                colors,
                fadeColors,
                random.nextBoolean(), // hasTrail
                true  // hasTwinkle
        );

        return new FireworksComponent(1, List.of(explosion));
    }

    private static FireworkExplosionComponent.Type randomType() {
        return FireworkExplosionComponent.Type.BURST;
    }
}
