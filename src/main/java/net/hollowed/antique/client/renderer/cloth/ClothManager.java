package net.hollowed.antique.client.renderer.cloth;

import net.hollowed.antique.config.AntiquitiesConfig;
import net.hollowed.antique.util.interfaces.duck.ClothAccess;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClothManager {

    public static RenderLayer getClothRenderLayer(Identifier cloth) {
        return RenderLayer.getItemEntityTranslucentCull(Identifier.of(cloth.getNamespace() + ":textures/cloth/" + cloth.getPath() + ".png"));
    }

    public static RenderLayer getOverlayRenderLayer(String cloth, Identifier overlay) {
        return RenderLayer.getItemEntityTranslucentCull(Identifier.of(overlay.getNamespace() + ":textures/overlay/" + overlay.getPath() + cloth + ".png"));
    }

    public Vector3d pos = new Vector3d();
    public ArrayList<ClothBody> bodies = new ArrayList<>();
    private int bodyCountCooldown = 0;
    public Entity entity;

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

    public void tick(double length) {
        double delta = MinecraftClient.getInstance().getRenderTickCounter().getDynamicDeltaTicks();
        ClientWorld world = MinecraftClient.getInstance().world;

        if (delta == 0) {
            for (ClothBody body : bodies) {
                body.posCache.set(body.pos);
            }
        }

        if (world != null) {

            double previousDrag = 0.0;

            bodies.getFirst().isPinned = true;

            // Update pass
            for (ClothBody body : bodies) {
                Vec3d startPos = new Vec3d(body.pos.x, body.pos.y, body.pos.z);
                BlockPos blockPos = BlockPos.ofFloored(startPos);
                BlockState state = world.getBlockState(blockPos);
                Vector3d vel = new Vector3d(body.pos).sub(body.posCache);
                double maxVel = 0.05;
                if (vel.length() > maxVel) {
                    vel.normalize().mul(maxVel);
                }
                double velLength = vel.length();
                double dynamicDrag = MathHelper.clamp(1.0 - velLength * 0.05, 0.85, 0.98);
                vel.mul(dynamicDrag);
                body.posCache.set(new Vector3d(body.pos).sub(vel));

                // Compute new drag value smoothly
                double newDrag = Math.random() * (state.getBlock() == Blocks.WATER ? 0.25 : 1.25);
                double smoothDrag = MathHelper.lerp(delta * 0.1, previousDrag, newDrag);

                // Apply gravity
                var gravity = AntiquitiesConfig.clothGravity;
                if(state.getBlock() == Blocks.WATER) {
                    gravity *= -0.5;
                }
                gravity /= 1;

                body.accel.add(0, -gravity, 0);

                previousDrag = smoothDrag; // Store for next iteration
                body.update(delta);
            }
        }

        for (int k = 0; k < 32; k++) {
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
            List<Vector3d> accels = new ArrayList<>();

            for (ClothBody body : bodies) {
                body.slideOutOfBlocks(world);
                accels.add(body.entityCollisionPerchance(world, entity));
                body.pos.x = MathHelper.lerp(0.125, body.pos.x, body.posCache.x);
                body.pos.y = MathHelper.lerp(0.125, body.pos.y, body.posCache.y);
                body.pos.z = MathHelper.lerp(0.125, body.pos.z, body.posCache.z);
            }

            Vector3d average = new Vector3d();
            for (Vector3d accel : accels) {
                average.add(accel);
            }

            average.div(accels.size());
            for (ClothBody body : bodies) {
                body.accel.add(average);
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
        Vector3d offset = new Vector3d(0, -0.2, 0);
        for (int i = 0; i < bodies.size(); i++) {
            bodies.get(i).pos.set(pos.add(offset.mul(i)));
            bodies.get(i).posCache.set(bodies.get(i).pos);
        }
    }

    public static Vec3d matrixToVec(MatrixStack matrixStack) {
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        Vector4f localPos = new Vector4f(0, 0, 0, 1);
        matrix.transform(localPos);
        Vec3d cameraPos = camera.getPos();
        return new Vec3d(cameraPos.x + localPos.x(), cameraPos.y + localPos.y(), cameraPos.z + localPos.z());
    }

    @SuppressWarnings("unused")
    public int rgbToDecimal(int red, int green, int blue) {
        return (red << 16) | (green << 8) | blue;
    }

    public static ClothManager getOrCreate(Entity entity, Identifier id) {
        if (entity instanceof ClothAccess clothAccess) {
            clothAccess.antique$getManagers().computeIfAbsent(id, k -> {
                ClothManager manager = new ClothManager(new Vector3d(entity.getX(), entity.getY(), entity.getZ()), 8);
                manager.entity = entity;
                return manager;
            });
            return clothAccess.antique$getManagers().get(id);
        }
        return null;
    }

    public void renderCloth(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, boolean glow, Color color, Color overlayColor, @Nullable Identifier cloth, Identifier overlay, double length, double width, int bodyCount) {
        if (cloth == null) return;

        Vec3d position = matrixToVec(matrices);

        if (bodyCount != 0 && this.bodyCountCooldown <= 0 && bodyCount != (bodies.size() - 1)) {
            setBodyCount(bodyCount);
            this.bodyCountCooldown = 3;
        }

        if (this.bodyCountCooldown > 0) {
            this.bodyCountCooldown--;
        }

        Vector3d lastA = null;
        Vector3d lastB = null;
        Vector3d lastThicknessVec = null;

        Vector3d danglePos = new Vector3d(position.x, position.y, position.z);
        pos = new Vector3d(danglePos);
        this.tick(length);

        matrices.push();
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

            Vector3d a = lastA != null ? lastA : pos.sub(thicknessVec, new Vector3d());
            Vector3d b = lastB != null ? lastB : pos.add(thicknessVec, new Vector3d());

            // Compute end vertices for this segment
            Vector3d c = nextPos.add(thicknessVec, new Vector3d());
            Vector3d d = nextPos.sub(thicknessVec, new Vector3d());

            // Cache for next loop
            lastA = d;
            lastB = c;
            lastThicknessVec = thicknessVec;

            RenderLayer clothLayer = getClothRenderLayer(cloth);
            String clothType = !Objects.equals(cloth.getPath(), "cloth") ? cloth.getPath().substring(0, cloth.getPath().indexOf("_")) : "default";
            RenderLayer overlayLayer = getOverlayRenderLayer("_" + clothType, overlay);

            drawQuad(
                    matrices,
                    new Matrix4f(),
                    clothLayer,
                    !overlay.equals(Identifier.of("")) ? overlayLayer : null,
                    queue,
                    a, b, c, d,
                    new Vec2f(0f, uvTop),
                    new Vec2f(1f, uvTop),
                    new Vec2f(1f, uvBot),
                    new Vec2f(0f, uvBot),
                    light,
                    glow,
                    color,
                    overlayColor
            );
        }

        matrices.pop();
    }

    public void drawQuad(MatrixStack matrices, Matrix4f matrix, RenderLayer layer, @Nullable RenderLayer overlay, OrderedRenderCommandQueue queue, Vector3d posA, Vector3d posB, Vector3d posC, Vector3d posD, Vec2f uvA, Vec2f uvB, Vec2f uvC, Vec2f uvD, int light, boolean glow, Color color, Color overlayColor) {
        var cam = MinecraftClient.getInstance().gameRenderer.getCamera().getPos().multiply(-1);

        queue.getBatchingQueue(1).submitCustom(matrices, layer, ((matricesEntry, vertexConsumer) -> {
            vertexConsumer.vertex(matrix, (float) ((float) posD.x + cam.x), (float) ((float) posD.y + cam.y), (float) ((float) posD.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0, 1, 0).light(light).texture(uvD.x, uvD.y).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            vertexConsumer.vertex(matrix, (float) ((float) posC.x + cam.x), (float) ((float) posC.y + cam.y), (float) ((float) posC.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0, 1, 0).light(light).texture(uvC.x, uvC.y).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            vertexConsumer.vertex(matrix, (float) ((float) posB.x + cam.x), (float) ((float) posB.y + cam.y), (float) ((float) posB.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0, 1, 0).light(light).texture(uvB.x, uvB.y).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            vertexConsumer.vertex(matrix, (float) ((float) posA.x + cam.x), (float) ((float) posA.y + cam.y), (float) ((float) posA.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0, 1, 0).light(light).texture(uvA.x, uvA.y).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

            vertexConsumer.vertex(matrix, (float) ((float) posA.x + cam.x), (float) ((float) posA.y + cam.y), (float) ((float) posA.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0, 1, 0).light(light).texture(uvA.x, uvA.y).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            vertexConsumer.vertex(matrix, (float) ((float) posB.x + cam.x), (float) ((float) posB.y + cam.y), (float) ((float) posB.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0, 1, 0).light(light).texture(uvB.x, uvB.y).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            vertexConsumer.vertex(matrix, (float) ((float) posC.x + cam.x), (float) ((float) posC.y + cam.y), (float) ((float) posC.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0, 1, 0).light(light).texture(uvC.x, uvC.y).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            vertexConsumer.vertex(matrix, (float) ((float) posD.x + cam.x), (float) ((float) posD.y + cam.y), (float) ((float) posD.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0, 1, 0).light(light).texture(uvD.x, uvD.y).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        }));

        if (overlay != null) {
            queue.getBatchingQueue(2).submitCustom(matrices, overlay, ((matricesEntry, vertexConsumer) -> {
                vertexConsumer.vertex(matrix, (float) ((float) posD.x + cam.x), (float) ((float) posD.y + cam.y), (float) ((float) posD.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0, 1, 0).light(glow ? 255 : light).texture(uvD.x, uvD.y).color(overlayColor.getRed(), overlayColor.getGreen(), overlayColor.getBlue(), overlayColor.getAlpha());
                vertexConsumer.vertex(matrix, (float) ((float) posC.x + cam.x), (float) ((float) posC.y + cam.y), (float) ((float) posC.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0, 1, 0).light(glow ? 255 : light).texture(uvC.x, uvC.y).color(overlayColor.getRed(), overlayColor.getGreen(), overlayColor.getBlue(), overlayColor.getAlpha());
                vertexConsumer.vertex(matrix, (float) ((float) posB.x + cam.x), (float) ((float) posB.y + cam.y), (float) ((float) posB.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0, 1, 0).light(glow ? 255 : light).texture(uvB.x, uvB.y).color(overlayColor.getRed(), overlayColor.getGreen(), overlayColor.getBlue(), overlayColor.getAlpha());
                vertexConsumer.vertex(matrix, (float) ((float) posA.x + cam.x), (float) ((float) posA.y + cam.y), (float) ((float) posA.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0, 1, 0).light(glow ? 255 : light).texture(uvA.x, uvA.y).color(overlayColor.getRed(), overlayColor.getGreen(), overlayColor.getBlue(), overlayColor.getAlpha());

                vertexConsumer.vertex(matrix, (float) ((float) posA.x + cam.x), (float) ((float) posA.y + cam.y), (float) ((float) posA.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0, 1, 0).light(glow ? 255 : light).texture(uvA.x, uvA.y).color(overlayColor.getRed(), overlayColor.getGreen(), overlayColor.getBlue(), overlayColor.getAlpha());
                vertexConsumer.vertex(matrix, (float) ((float) posB.x + cam.x), (float) ((float) posB.y + cam.y), (float) ((float) posB.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0, 1, 0).light(glow ? 255 : light).texture(uvB.x, uvB.y).color(overlayColor.getRed(), overlayColor.getGreen(), overlayColor.getBlue(), overlayColor.getAlpha());
                vertexConsumer.vertex(matrix, (float) ((float) posC.x + cam.x), (float) ((float) posC.y + cam.y), (float) ((float) posC.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0, 1, 0).light(glow ? 255 : light).texture(uvC.x, uvC.y).color(overlayColor.getRed(), overlayColor.getGreen(), overlayColor.getBlue(), overlayColor.getAlpha());
                vertexConsumer.vertex(matrix, (float) ((float) posD.x + cam.x), (float) ((float) posD.y + cam.y), (float) ((float) posD.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0, 1, 0).light(glow ? 255 : light).texture(uvD.x, uvD.y).color(overlayColor.getRed(), overlayColor.getGreen(), overlayColor.getBlue(), overlayColor.getAlpha());
            }));
        }
    }
}
