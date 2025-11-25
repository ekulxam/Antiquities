package net.hollowed.antique.client.renderer.experimental_cloth;

import net.dustley.lemon.modules.citrus_physics.PhysicsWorld;
import net.dustley.lemon.modules.citrus_physics.component.ActorComponent;
import net.hollowed.antique.util.interfaces.duck.ClothAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector4f;

import java.awt.*;
import java.util.Objects;

public class ClothManager {

    private final PhysicsWorld physicsWorld;
    private Cloth cloth;
    public Entity entity;

    private int swapCooldown = 0;

    public ClothManager(World world) {
        this.physicsWorld = PhysicsWorld.getFromWorld(world);
        this.cloth = new Cloth(null, this.physicsWorld);
    }

    public static ClothManager getOrCreate(Entity entity, Identifier id) {
        if (entity instanceof ClothAccess clothAccess) {
            clothAccess.antique$getExperimentalManagers().computeIfAbsent(id, k -> {
                ClothManager manager = new ClothManager(entity.getEntityWorld());
                manager.entity = entity;
                return manager;
            });
            return clothAccess.antique$getExperimentalManagers().get(id);
        }
        return null;
    }

    public static Vec3d matrixToVec(MatrixStack matrixStack) {
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        Vector4f localPos = new Vector4f(0, 0, 0, 1);
        matrix.transform(localPos);
        Vec3d cameraPos = camera.getPos();
        return new Vec3d(cameraPos.x + localPos.x(), cameraPos.y + localPos.y(), cameraPos.z + localPos.z());
    }

    public void renderCloth(
            MatrixStack matrices,
            OrderedRenderCommandQueue queue,
            int light,
            boolean glow,
            Color color,
            Color overlayColor,
            boolean ignoreFreeze,
            Identifier cloth,
            Identifier overlay,
            double length,
            double width,
            int segmentCount) {

        Vec3d position = matrixToVec(matrices);

        if (segmentCount != 0 && this.swapCooldown <= 0 && segmentCount != this.cloth.getSegmentCount() && length != this.cloth.getLength() * segmentCount && this.cloth.getWidth() != width) {
            this.cloth = new Cloth(this.entity, this.physicsWorld, segmentCount, length, width);
            this.swapCooldown = 3;
        }

        if (this.swapCooldown > 0) {
            this.swapCooldown--;
        }

        this.cloth.update(new Vector3d(position.x, position.y, position.z), MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(ignoreFreeze));

        Vector3d lastA = null;
        Vector3d lastB = null;
        Vector3d lastThicknessVec = null;

        matrices.push();
        int count = this.cloth.getSegmentCount() - 1;
        for (int i = 0; i < count; i++) {

            var actor = this.cloth.getSegment(i).get(ActorComponent.class);
            var otherActor = this.cloth.getSegment(i + 1).get(ActorComponent.class);

            if (actor == null || otherActor == null) return;

            var pos = actor.position;
            var nextPos = otherActor.position;

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

            drawClothQuad(
                    matrices,
                    new Matrix4f(),
                    clothLayer,
                    !overlay.equals(Identifier.of("")) ? overlayLayer : null,
                    queue,
                    a, b, c, d,
                    new Vec2f(0f,uvTop),
                    new Vec2f(1f,uvTop),
                    new Vec2f(1f,uvBot),
                    new Vec2f(0f,uvBot),
                    light,
                    glow,
                    color,
                    overlayColor
            );
        }

        matrices.pop();
    }

    public void drawClothQuad(MatrixStack matrices, Matrix4f matrix, RenderLayer layer, @Nullable RenderLayer overlay, OrderedRenderCommandQueue queue, Vector3d posA, Vector3d posB, Vector3d posC, Vector3d posD, Vec2f uvA, Vec2f uvB, Vec2f uvC, Vec2f uvD, int light, boolean glow, Color color, Color overlayColor) {
        var cam = MinecraftClient.getInstance().gameRenderer.getCamera().getPos().multiply(-1);

        queue.getBatchingQueue(1).submitCustom(matrices, layer, ((matricesEntry, vertexConsumer) -> {
            vertexConsumer.vertex(matrix, (float) ((float) posD.x + cam.x), (float) ((float) posD.y + cam.y), (float) ((float) posD.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0,1,0).light(light).texture(uvD.x, uvD.y).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            vertexConsumer.vertex(matrix, (float) ((float) posC.x + cam.x), (float) ((float) posC.y + cam.y), (float) ((float) posC.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0,1,0).light(light).texture(uvC.x, uvC.y).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            vertexConsumer.vertex(matrix, (float) ((float) posB.x + cam.x), (float) ((float) posB.y + cam.y), (float) ((float) posB.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0,1,0).light(light).texture(uvB.x, uvB.y).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            vertexConsumer.vertex(matrix, (float) ((float) posA.x + cam.x), (float) ((float) posA.y + cam.y), (float) ((float) posA.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0,1,0).light(light).texture(uvA.x, uvA.y).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

            vertexConsumer.vertex(matrix, (float) ((float) posA.x + cam.x), (float) ((float) posA.y + cam.y), (float) ((float) posA.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0,1,0).light(light).texture(uvA.x, uvA.y).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            vertexConsumer.vertex(matrix, (float) ((float) posB.x + cam.x), (float) ((float) posB.y + cam.y), (float) ((float) posB.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0,1,0).light(light).texture(uvB.x, uvB.y).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            vertexConsumer.vertex(matrix, (float) ((float) posC.x + cam.x), (float) ((float) posC.y + cam.y), (float) ((float) posC.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0,1,0).light(light).texture(uvC.x, uvC.y).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            vertexConsumer.vertex(matrix, (float) ((float) posD.x + cam.x), (float) ((float) posD.y + cam.y), (float) ((float) posD.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0,1,0).light(light).texture(uvD.x, uvD.y).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        }));

        if (overlay == null) return;
        queue.getBatchingQueue(2).submitCustom(matrices, overlay, ((matricesEntry, vertexConsumer) -> {
            vertexConsumer.vertex(matrix, (float) ((float) posD.x + cam.x), (float) ((float) posD.y + cam.y), (float) ((float) posD.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0,1,0).light(glow ? 255 : light).texture(uvD.x, uvD.y).color(overlayColor.getRed(), overlayColor.getGreen(), overlayColor.getBlue(), overlayColor.getAlpha());
            vertexConsumer.vertex(matrix, (float) ((float) posC.x + cam.x), (float) ((float) posC.y + cam.y), (float) ((float) posC.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0,1,0).light(glow ? 255 : light).texture(uvC.x, uvC.y).color(overlayColor.getRed(), overlayColor.getGreen(), overlayColor.getBlue(), overlayColor.getAlpha());
            vertexConsumer.vertex(matrix, (float) ((float) posB.x + cam.x), (float) ((float) posB.y + cam.y), (float) ((float) posB.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0,1,0).light(glow ? 255 : light).texture(uvB.x, uvB.y).color(overlayColor.getRed(), overlayColor.getGreen(), overlayColor.getBlue(), overlayColor.getAlpha());
            vertexConsumer.vertex(matrix, (float) ((float) posA.x + cam.x), (float) ((float) posA.y + cam.y), (float) ((float) posA.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0,1,0).light(glow ? 255 : light).texture(uvA.x, uvA.y).color(overlayColor.getRed(), overlayColor.getGreen(), overlayColor.getBlue(), overlayColor.getAlpha());

            vertexConsumer.vertex(matrix, (float) ((float) posA.x + cam.x), (float) ((float) posA.y + cam.y), (float) ((float) posA.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0,1,0).light(glow ? 255 : light).texture(uvA.x, uvA.y).color(overlayColor.getRed(), overlayColor.getGreen(), overlayColor.getBlue(), overlayColor.getAlpha());
            vertexConsumer.vertex(matrix, (float) ((float) posB.x + cam.x), (float) ((float) posB.y + cam.y), (float) ((float) posB.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0,1,0).light(glow ? 255 : light).texture(uvB.x, uvB.y).color(overlayColor.getRed(), overlayColor.getGreen(), overlayColor.getBlue(), overlayColor.getAlpha());
            vertexConsumer.vertex(matrix, (float) ((float) posC.x + cam.x), (float) ((float) posC.y + cam.y), (float) ((float) posC.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0,1,0).light(glow ? 255 : light).texture(uvC.x, uvC.y).color(overlayColor.getRed(), overlayColor.getGreen(), overlayColor.getBlue(), overlayColor.getAlpha());
            vertexConsumer.vertex(matrix, (float) ((float) posD.x + cam.x), (float) ((float) posD.y + cam.y), (float) ((float) posD.z + cam.z)).overlay(OverlayTexture.DEFAULT_UV).normal(0,1,0).light(glow ? 255 : light).texture(uvD.x, uvD.y).color(overlayColor.getRed(), overlayColor.getGreen(), overlayColor.getBlue(), overlayColor.getAlpha());
        }));
    }

    public static RenderLayer getClothRenderLayer(Identifier cloth) {
        return RenderLayer.getItemEntityTranslucentCull(Identifier.of(cloth.getNamespace() + ":textures/cloth/" + cloth.getPath() + ".png"));
    }

    public static RenderLayer getOverlayRenderLayer(String cloth, Identifier overlay) {
        return RenderLayer.getItemEntityTranslucentCull(Identifier.of(overlay.getNamespace() + ":textures/overlay/" + overlay.getPath() + cloth + ".png"));
    }
}
