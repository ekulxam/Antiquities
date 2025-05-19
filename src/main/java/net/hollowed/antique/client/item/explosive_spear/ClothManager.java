package net.hollowed.antique.client.item.explosive_spear;

import net.hollowed.antique.Antiquities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector4f;

import java.awt.*;
import java.util.ArrayList;

public class ClothManager {

    public static RenderLayer BLANK_CLOTH_STRIP = RenderLayer.getEntityTranslucent(Antiquities.id("textures/item/cloth_strip.png"));
    public static RenderLayer TATTERED_CLOTH_STRIP = RenderLayer.getEntityTranslucent(Antiquities.id("textures/item/tattered_cloth_strip.png"));

    public Vector3d pos = new Vector3d();
    public ArrayList<ClothBody> bodies = new ArrayList<>();

    public ClothManager(Vector3d pos, int BodyCount) {
        reset(pos, BodyCount);
    }

    public void reset(Vector3d pos, int BodyCount) {
        bodies.clear();
        for (int i = 0; i < Math.abs(BodyCount+1); i++) {
            ClothBody body = new ClothBody(pos);
            bodies.add(body);
        }
    }

    public void setBodyCount(int count) {
        if (count != bodies.size()) {
            reset(this.pos, count);
        }
    }

    public void tick(boolean ignoreFreeze, double length) {
        double delta = MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(ignoreFreeze);
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

            double previousDrag = 0.0; // Store last frame's drag to lerp smoothly

            // Update pass
            for (int i = 0; i < bodies.size(); i++) {
                ClothBody body = bodies.get(i);
                Vec3d startPos = new Vec3d(body.pos.x, body.pos.y, body.pos.z);
                BlockPos blockPos = BlockPos.ofFloored(startPos);
                BlockState state = world.getBlockState(blockPos);
                Vector3d vel = body.pos.sub(body.posCache, new Vector3d());
                body.accel.add(vel.mul(-0.15));

                // Compute new drag value smoothly
                double newDrag = Math.random() * (state.getBlock() == Blocks.WATER ? 0.25 : 1.25);
                double smoothDrag = MathHelper.lerp(delta * 0.1, previousDrag, newDrag); // Lerp for smooth transition

                // Apply gravity and wind
                if (state.getBlock() == Blocks.WATER) {
                    body.accel.add(0.0, 0.00049, 0.0);
                    ClothWindHelper.applyWindToBody(body, i, (i * i * 0.5), 0.75, smoothDrag);
                } else {
                    body.accel.add(0.0, -0.00245, 0.0);
                    ClothWindHelper.applyWindToBody(body, i, (i * i * 0.5), 1.0, smoothDrag);
                }

                previousDrag = smoothDrag; // Store for next iteration
                body.update(delta);
            }
        }

        // Constraint pass
        for (int k = 0; k < bodies.size(); k++) {
            for (int i = 0; i < bodies.size() - 1; i++) {
                var body = bodies.get(i);
                var nextBody = bodies.get(i + 1);
                body.containDistance(nextBody, (1.0 / bodies.size()) * length);
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

    public static Vec3d matrixToVec(MatrixStack matrixStack) {
        // Extract transformation matrix
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();

        // Convert local position to world space
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        return transformToWorld(matrix, camera);
    }

    // **Helper Method: Converts Rendered Item Position to World Space**
    private static Vec3d transformToWorld(Matrix4f matrix, Camera camera) {
        // Convert (0,0,0) in local item space to transformed coordinates
        Vector4f localPos = new Vector4f(0, 0, 0, 1);
        matrix.transform(localPos);

        // Convert view space to world space by adding the camera position
        Vec3d cameraPos = camera.getPos();
        return new Vec3d(cameraPos.x + localPos.x(), cameraPos.y + localPos.y(), cameraPos.z + localPos.z());
    }

    public void renderCloth(Vec3d position, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, boolean firstPerson, Color color, boolean ignoreFreeze, RenderLayer layer, double length, double width) {
        this.tick(ignoreFreeze, length);

        matrices.push();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(layer);
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
            var thicknessVec = new Vector3d(width, 0.0, 0.0);
            if (!firstPerson) thicknessVec.rotateY(Math.toRadians(rot));
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
                    light,
                    color
            );
            drawQuad(
                    new Matrix4f(),
                    vertexConsumer,
                    d, c, b, a,
                    new Vec2f(0f,uvBot),
                    new Vec2f(1f,uvBot),
                    new Vec2f(1f,uvTop),
                    new Vec2f(0f,uvTop),
                    light,
                    color
            );

        }

        matrices.pop();
    }

    public void drawQuad(Matrix4f matrix, VertexConsumer vertexConsumer, Vector3d posA, Vector3d posB, Vector3d posC, Vector3d posD, Vec2f uvA, Vec2f uvB, Vec2f uvC, Vec2f uvD, int light, Color color) {
        // Draw a line from pos1 to pos2
        var cam = MinecraftClient.getInstance().gameRenderer.getCamera().getPos().multiply(-1); //.getClientCameraPosVec(MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true)).multiply(-1);
        vertexConsumer.vertex(matrix, (float) ((float) posA.x + cam.x), (float) ((float) posA.y + cam.y), (float) ((float) posA.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0,1,0).light(light).texture(uvA.x, uvA.y).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        vertexConsumer.vertex(matrix, (float) ((float) posB.x + cam.x), (float) ((float) posB.y + cam.y), (float) ((float) posB.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0,1,0).light(light).texture(uvB.x, uvB.y).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        vertexConsumer.vertex(matrix, (float) ((float) posC.x + cam.x), (float) ((float) posC.y + cam.y), (float) ((float) posC.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0,1,0).light(light).texture(uvC.x, uvC.y).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        vertexConsumer.vertex(matrix, (float) ((float) posD.x + cam.x), (float) ((float) posD.y + cam.y), (float) ((float) posD.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0,1,0).light(light).texture(uvD.x, uvD.y).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }
}
