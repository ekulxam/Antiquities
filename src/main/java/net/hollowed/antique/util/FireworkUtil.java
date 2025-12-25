package net.hollowed.antique.util;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import java.util.Random;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;

public class FireworkUtil {
    public static Fireworks randomFireworkBall() {
        Random random = new Random();

        // Generate 1–3 random colors
        IntList colors = IntArrayList.wrap(random.ints(1 + random.nextInt(3), 0, 0xFFFFFF + 1).toArray());

        // 50% chance of having fade colors
        IntList fadeColors = random.nextBoolean()
                ? IntArrayList.wrap(random.ints(1 + random.nextInt(2), 0, 0xFFFFFF + 1).toArray())
                : IntArrayList.of();

        FireworkExplosion explosion = new FireworkExplosion(
                FireworkExplosion.Shape.SMALL_BALL,
                colors,
                fadeColors,
                random.nextBoolean(), // hasTrail
                true  // hasTwinkle
        );

        return new Fireworks(-3, List.of(explosion));
    }

    public static Fireworks randomFirework() {
        Random random = new Random();

        // Generate 1–3 random colors
        IntList colors = IntArrayList.wrap(random.ints(1 + random.nextInt(3), 0, 0xFFFFFF + 1).toArray());

        // 50% chance of having fade colors
        IntList fadeColors = random.nextBoolean()
                ? IntArrayList.wrap(random.ints(1 + random.nextInt(2), 0, 0xFFFFFF + 1).toArray())
                : IntArrayList.of();

        FireworkExplosion explosion = new FireworkExplosion(
                randomType(),
                colors,
                fadeColors,
                random.nextBoolean(), // hasTrail
                true  // hasTwinkle
        );

        return new Fireworks(1, List.of(explosion));
    }

    private static FireworkExplosion.Shape randomType() {
        return FireworkExplosion.Shape.BURST;
    }
}
