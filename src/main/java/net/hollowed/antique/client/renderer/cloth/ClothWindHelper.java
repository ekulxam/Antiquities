package net.hollowed.antique.client.renderer.cloth;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.math.random.Random;
import org.joml.Vector3d;

@SuppressWarnings("all")
public class ClothWindHelper {

    public static long WIND_NOISE_SEED = 4L;
    public static double WIND_NOISE_FREQ = 0.000005;   // Frequency for baseline wind variation
    public static double WIND_NOISE_SCALE = 0.1;       // Scale factor for directional variation
    public static double WIND_NOISE_HERTZ = 0.00001;   // Adjusted to control wind response time
    public static Vector3d windPos = new Vector3d();

    public static void applyWindToBody(ClothBody body, int bodyIndex, double eff, double flow, double drag) {
        WIND_NOISE_HERTZ = 0.00001;
        double frequency = WIND_NOISE_FREQ * flow;
        double multiplier = WIND_NOISE_HERTZ * drag;
        if (MinecraftClient.getInstance().world == null) return;
        double time = MinecraftClient.getInstance().world.getTime() * 0.02; // Smooth time-based noise variation

        // Perlin Noise Instances for wind dynamics
        var directionXNoise = new PerlinNoiseSampler(Random.create(WIND_NOISE_SEED + bodyIndex));
        var directionZNoise = new PerlinNoiseSampler(Random.create(WIND_NOISE_SEED + bodyIndex + 1));
        var strengthNoise = new PerlinNoiseSampler(Random.create(WIND_NOISE_SEED + 2));
        var gustNoise = new PerlinNoiseSampler(Random.create(WIND_NOISE_SEED + 5)); // New gust factor

        // Offset wind position per cloth piece to add variation
        double offsetX = (bodyIndex * 0.12) % 5;
        double offsetZ = (bodyIndex * 0.18) % 5;

        windPos.add(frequency, 0, frequency);

        double gustFactor = (Math.sin(time * 0.8) * 0.5 + 0.5)
                * MathHelper.map(gustNoise.sample(time * 0.1, bodyIndex * 0.2, 0), -1.0, 1.0, 0.3, 1.2);

        double strengthSample = strengthNoise.sample(
                windPos.x + body.pos.x * WIND_NOISE_SCALE + offsetX,
                windPos.y + body.pos.y * WIND_NOISE_SCALE,
                windPos.z - body.pos.z * WIND_NOISE_SCALE - offsetZ
        );

        double windStrength = MathHelper.map(strengthSample, -1.0, 1.0, 0.5, 2.2)
                * 20.0 * (multiplier * eff)
                * gustFactor;

        // Generate smooth X and Z wind components using noise
        double windX = directionXNoise.sample(
                windPos.x + offsetX + time * 0.03,
                windPos.y,
                windPos.z + offsetZ
        );
        double windZ = directionZNoise.sample(
                windPos.x - offsetX,
                windPos.y,
                windPos.z - offsetZ - time * 0.03
        );

        // Map wind direction from -1 to 1 range
        windX = MathHelper.map(windX, -1.0, 1.0, -1.0, 1.0);
        windZ = MathHelper.map(windZ, -1.0, 1.0, -1.0, 1.0);

        Vector3d windDirectionVector = new Vector3d(windX, 0, windZ).normalize(); // Ensure smooth omnidirectional motion

        // Apply wind force with gusting effect
        body.accel.add(windDirectionVector.mul(windStrength, new Vector3d()));
    }
}
