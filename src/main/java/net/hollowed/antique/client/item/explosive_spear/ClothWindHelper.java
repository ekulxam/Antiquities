package net.hollowed.antique.client.item.explosive_spear;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.math.random.Random;
import org.joml.Vector3d;

public class ClothWindHelper {

    public static long WIND_NOISE_SEED = 4L;
    public static double WIND_NOISE_FREQ = 0.00005;
    public static double WIND_NOISE_SCALE = 3.0;
    public static double WIND_NOISE_HERTZ = 0.0005;
    public static Vector3d windPos = new Vector3d();

    public static void applyWindToBody(ClothBody body, double eff, double flow, double drag) {
        WIND_NOISE_FREQ = 0.00001;
        WIND_NOISE_SCALE = 15.0;
        WIND_NOISE_HERTZ = 0.0005;

        double frequency = WIND_NOISE_FREQ * flow;
        double multiplier = WIND_NOISE_HERTZ * drag;

        //Wind
        var directionNoise = new PerlinNoiseSampler(Random.create(WIND_NOISE_SEED));
        var strengthNoise = new PerlinNoiseSampler(Random.create(WIND_NOISE_SEED + 1L));
        windPos.add(frequency, 0, frequency);

        var windStrengthSample = strengthNoise.sample((windPos.x) + (body.pos.x * WIND_NOISE_SCALE), (windPos.y) + (body.pos.y * WIND_NOISE_SCALE), (windPos.z) + (body.pos.z * WIND_NOISE_SCALE));
        var windStrength = MathHelper.map(windStrengthSample, -1.0, 1.0, 0.0, 1.0) * 15.0 * (multiplier * eff);// eff = (i * i * 0.5)

        var windDirectionSample = directionNoise.sample((windPos.x), (windPos.y), (windPos.z));
        var windDirectionDegrees = MathHelper.map(windDirectionSample, 0.0, 1.0, 0.0, 360.0);
        var windDirectionVector = new Vector3d(1.0, 0.0, 0.0).rotateY(Math.toRadians(windDirectionDegrees));

        body.accel.add(windDirectionVector.mul(windStrength, new Vector3d()));
    }
}
