package net.hollowed.antique.client.renderer.cloth;

import net.hollowed.antique.Antiquities;
import net.hollowed.antique.util.resources.ClothSkinData;
import net.hollowed.antique.util.resources.ClothSkinListener;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector4f;

import java.awt.*;
import java.util.ArrayList;

public class ClothManager {

    public static RenderLayer TATTERED_CLOTH_STRIP = RenderLayer.getEntityTranslucent(Antiquities.id("textures/item/tattered_cloth_strip.png"));

    public Vector3d pos = new Vector3d();
    public ArrayList<ClothBody> bodies = new ArrayList<>();
    private int bodyCountCooldown = 0;

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
        ClientWorld world = MinecraftClient.getInstance().world;

        if (world != null) {

            double previousDrag = 0.0; // Store last frame's drag to lerp smoothly

            bodies.getFirst().isPinned = true;

            // Update pass
            for (ClothBody body : bodies) {
                Vec3d startPos = new Vec3d(body.pos.x, body.pos.y, body.pos.z);
                BlockPos blockPos = BlockPos.ofFloored(startPos);
                BlockState state = world.getBlockState(blockPos);
                Vector3d vel = new Vector3d(body.pos).sub(body.posCache);
                double maxVel = 0.05; // or tune this
                if (vel.length() > maxVel) {
                    vel.normalize().mul(maxVel);
                }
                double velLength = vel.length();
                double dynamicDrag = MathHelper.clamp(1.0 - velLength * 0.05, 0.85, 0.98);
                vel.mul(dynamicDrag);
                body.posCache.set(new Vector3d(body.pos).sub(vel));

                // Compute new drag value smoothly
                double newDrag = Math.random() * (state.getBlock() == Blocks.WATER ? 0.25 : 1.25);
                double smoothDrag = MathHelper.lerp(delta * 0.1, previousDrag, newDrag); // Lerp for smooth transition

                // Apply gravity and wind
                if (state.getBlock() == Blocks.WATER) {
                    body.accel.add(0.0, 0.0025, 0.0);

                    //ClothWindHelper.applyWindToBody(body, i, (i * i * 0.5), 0.75, smoothDrag);
                } else {
                    body.accel.add(0, -0.005, 0);

                    //ClothWindHelper.applyWindToBody(body, i, (i * i * 0.5), 1.00, smoothDrag);
                }

                previousDrag = smoothDrag; // Store for next iteration
                body.update(delta);
            }
        }

        for (int k = 0; k < 10; k++) {
            if (k % 2 == 0) {
                for (int i = 0; i < bodies.size() - 1; i++) {
                    bodies.get(i).containDistance(bodies.get(i + 1), length / bodies.size());
                }
            } else {
                for (int i = bodies.size() - 2; i >= 0; i--) {
                    bodies.get(i).containDistance(bodies.get(i + 1), length / bodies.size());
                }
            }
        }


        // Collision pass
        if (world != null) {
            for (ClothBody body : bodies) {
                body.slideOutOfBlocks(world);
                body.pos.x = MathHelper.lerp(0.125, body.pos.x, body.posCache.x);
                body.pos.y = MathHelper.lerp(0.125, body.pos.y, body.posCache.y);
                body.pos.z = MathHelper.lerp(0.125, body.pos.z, body.posCache.z);
            }
        }

        // Update parent position
        var root = bodies.getFirst();
        root.pos = new Vector3d(pos);

        // Check if cloth is too far from the root position
        double maxDistance = 5.0;
        if (root.pos.distance(root.posCache) > maxDistance) {
            resetCloth(); // Call reset method
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

    public int rgbToDecimal(int red, int green, int blue) {
        return (red << 16) | (green << 8) | blue;
    }

    public void renderCloth(Vec3d position, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Color color, boolean ignoreFreeze, RenderLayer layer, double length, double width) {
        ClothSkinData.ClothSubData data = ClothSkinListener.getTransform(rgbToDecimal(color.getRed(), color.getGreen(), color.getBlue()));
        String texture = data.texture();
        if (!texture.isBlank()) {
            layer = RenderLayer.getEntityTranslucent(Identifier.of(texture));
            color = Color.WHITE;
            length = data.length() != 0 ? data.length() : length;
            width = data.width() != 0 ? data.width() : width;
            if (data.bodyAmount() != 0 && this.bodyCountCooldown <= 0 && data.bodyAmount() != (bodies.size() - 1)) {
                setBodyCount(data.bodyAmount());
                this.bodyCountCooldown = 3;
            }
            light = data.light() != 0 ? data.light() : light;
        }

        if (this.bodyCountCooldown > 0) {
            this.bodyCountCooldown--;
        }

        Vector3d lastA = null;
        Vector3d lastB = null;
        Vector3d lastThicknessVec = null;

        Vector3d danglePos = new Vector3d(position.x, position.y, position.z);
        pos = new Vector3d(danglePos);
        this.tick(ignoreFreeze, length);

        matrices.push();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(layer);
        int count = bodies.size() - 1;
        for (int i = 0; i < count; i++) {

            ClothBody body = bodies.get(i);
            ClothBody nextBody = bodies.get(i + 1);

            var pos = body.getPos();
            var nextPos = nextBody.getPos();

            float uvTop = (1f / count) * i;
            float uvBot = uvTop + (1f / count);

            // Get camera position
            Vec3d cameraPosVec3d = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
            Vector3d cameraPos = new Vector3d(cameraPosVec3d.x, cameraPosVec3d.y, cameraPosVec3d.z);
            // Compute thickness vector from segment midpoint
            Vector3d mid = new Vector3d((pos.x + nextPos.x) / 2.0, (pos.y + nextPos.y) / 2.0, (pos.z + nextPos.z) / 2.0);
            Vector3d toCam = cameraPos.sub(mid, new Vector3d()).normalize();
            Vector3d up = new Vector3d(0, 1, 0);
            Vector3d thicknessVec = up.cross(toCam, new Vector3d()).normalize().mul(width);

            if (lastThicknessVec != null) {
                thicknessVec = thicknessVec.lerp(lastThicknessVec, 1).normalize().mul(width);
            }


            // Use last segment’s end vertices as this segment’s start
            Vector3d a = lastA != null ? lastA : pos.sub(thicknessVec, new Vector3d());
            Vector3d b = lastB != null ? lastB : pos.add(thicknessVec, new Vector3d());

            // Compute end vertices for this segment
            Vector3d c = nextPos.add(thicknessVec, new Vector3d());
            Vector3d d = nextPos.sub(thicknessVec, new Vector3d());

            // Cache for next loop
            lastA = d;
            lastB = c;
            lastThicknessVec = thicknessVec;

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
