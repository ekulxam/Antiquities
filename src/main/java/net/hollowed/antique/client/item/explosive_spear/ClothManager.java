package net.hollowed.antique.client.item.explosive_spear;

import net.hollowed.antique.Antiquities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.gen.noise.NoiseHelper;
import org.joml.Matrix4f;
import org.joml.Vector3d;

import java.util.ArrayList;

public class ClothManager {

    public static double CLOTH_LENGTH = 2;
    public static double CLOTH_WIDTH = 0.1;

    public Vector3d pos = new Vector3d();
    public ArrayList<ClothBody> bodies = new ArrayList<>();

    public static long windNoiseSeed = 4L;
    public Vector3d windPos = new Vector3d();

    private int bodyCount;

    public ClothManager(Vector3d pos, int BodyCount) {
        this.bodyCount = BodyCount;
        reset(pos, BodyCount);
    }

    public void reset(Vector3d pos, int BodyCount) {
        bodies.clear();
        for (int i = 0; i < Math.abs(BodyCount+1); i++) {
            ClothBody body = new ClothBody(pos);
            bodies.add(body);
        }
        this.bodyCount = BodyCount;
    }

    public void tick(double delta) {

        // Update parent position
        var root = bodies.getFirst();
        root.pos = new Vector3d(pos);

        // Check if cloth is too far from the root position
        double maxDistance = 5.0;
        if (root.pos.distance(root.posCache) > maxDistance) {
            resetCloth(); // Call reset method
            return; // Exit tick early after resetting
        }

        ClientWorld world = MinecraftClient.getInstance().world;

        if (world != null) {

            // Update pass
            for (int i = 0; i < bodies.size(); i++) {
                ClothBody body = bodies.get(i);
                Vec3d startPos = new Vec3d(body.pos.x, body.pos.y, body.pos.z);
                BlockPos blockPos = BlockPos.ofFloored(startPos);
                BlockState state = world.getBlockState(blockPos);
                Vector3d vel = body.pos.sub(body.posCache, new Vector3d());
                body.accel.add(vel.mul(-0.15));

                // Gravity and wind
                if (state.getBlock() == Blocks.WATER) {
                    body.accel.add(0.0, 0.00049, 0.0);
                    ClothWindHelper.applyWindToBody(body, (i*i*0.5), 0.75, 0.25);
                } else {
                    body.accel.add(0.0, -0.00245, 0.0);
                    ClothWindHelper.applyWindToBody(body, (i*i*0.5), 1.0, 1.0);
                }

                body.update(delta);
            }
        }

        // Constraint pass
        for (int k = 0; k < bodies.size(); k++) {
            for (int i = 0; i < bodies.size() - 1; i++) {
                var body = bodies.get(i);
                var nextBody = bodies.get(i + 1);
                body.containDistance(nextBody, (1.0 / bodies.size()) * CLOTH_LENGTH);
            }
        }

        // Collision pass
        if (world != null) {
            for (ClothBody body : bodies) {
                body.slideOutOfBlocks(world);
            }
        }
    }

    // Reset all body segments to be near the root position
    private void resetCloth() {
        Vector3d offset = new Vector3d(0, -0.2, 0); // Slight offset for natural repositioning
        for (int i = 0; i < bodies.size(); i++) {
            bodies.get(i).pos.set(pos.add(offset.mul(i)));
            bodies.get(i).posCache.set(bodies.get(i).pos);
        }
    }

    public void renderCloth(Vec3d position, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(Antiquities.id("textures/item/explosive_spear_cloth.png")));
        Vector3d danglePos = new Vector3d(position.x, position.y, position.z);

        pos = new Vector3d(danglePos);

        int count = bodies.size() - 1;
        for (int i = 0; i < count; i++) {

            ClothBody body = bodies.get(i);
            ClothBody nextBody = bodies.get(i + 1);

            var pos = body.getPos();
            var nextPos = nextBody.getPos();

            float uvTop = (1f / count) * i;
            float uvBot = uvTop + (1f / count);

            var rot = -MinecraftClient.getInstance().gameRenderer.getCamera().getYaw();
            var thicknessVec = new Vector3d(CLOTH_WIDTH, 0.0, 0.0).rotateY(Math.toRadians(rot));
            var a = pos.sub(thicknessVec, new Vector3d());
            var b = pos.add(thicknessVec, new Vector3d());
            var c = nextPos.add(thicknessVec, new Vector3d());
            var d = nextPos.sub(thicknessVec, new Vector3d());

            drawQuad(
                    new Matrix4f(),
                    vertexConsumer,
                    a, b, c, d,
                    new Vec2f(0f,uvTop),
                    new Vec2f(1f,uvTop),
                    new Vec2f(1f,uvBot),
                    new Vec2f(0f,uvBot),
                    light
            );
            drawQuad(
                    new Matrix4f(),
                    vertexConsumer,
                    d, c, b, a,
                    new Vec2f(0f,uvBot),
                    new Vec2f(1f,uvBot),
                    new Vec2f(1f,uvTop),
                    new Vec2f(0f,uvTop),
                    light
            );

        }

        matrices.pop();
    }

    public void drawQuad(Matrix4f matrix, VertexConsumer vertexConsumer, Vector3d posA, Vector3d posB, Vector3d posC, Vector3d posD, Vec2f uvA, Vec2f uvB, Vec2f uvC, Vec2f uvD, int light) {
        // Draw a line from pos1 to pos2
        var cam = MinecraftClient.getInstance().gameRenderer.getCamera().getPos().multiply(-1); //.getClientCameraPosVec(MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true)).multiply(-1);
        vertexConsumer.vertex(matrix, (float) ((float) posA.x + cam.x), (float) ((float) posA.y + cam.y), (float) ((float) posA.z + cam.z)).color(255, 255, 255, 255).overlay(0).normal(0f,0f,1f).light(light).texture(uvA.x, uvA.y);
        vertexConsumer.vertex(matrix, (float) ((float) posB.x + cam.x), (float) ((float) posB.y + cam.y), (float) ((float) posB.z + cam.z)).color(255, 255, 255, 255).overlay(0).normal(0f,0f,1f).light(light).texture(uvB.x, uvB.y);
        vertexConsumer.vertex(matrix, (float) ((float) posC.x + cam.x), (float) ((float) posC.y + cam.y), (float) ((float) posC.z + cam.z)).color(255, 255, 255, 255).overlay(0).normal(0f,0f,1f).light(light).texture(uvC.x, uvC.y);
        vertexConsumer.vertex(matrix, (float) ((float) posD.x + cam.x), (float) ((float) posD.y + cam.y), (float) ((float) posD.z + cam.z)).color(255, 255, 255, 255).overlay(0).normal(0f,0f,1f).light(light).texture(uvD.x, uvD.y);
    }
}
