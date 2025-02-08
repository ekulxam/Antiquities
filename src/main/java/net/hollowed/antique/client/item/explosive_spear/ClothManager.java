package net.hollowed.antique.client.item.explosive_spear;

import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.mixin.HeldItemRendererMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.GuardianEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;

public class ClothManager {

    public static double CLOTH_LENGTH = 0.25;
    public static double CLOTH_WIDTH = 0.5;

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

    public void tick(double delta) {
        CLOTH_LENGTH = 1.5;
        CLOTH_WIDTH = 0.05;

        // Update parent position
        var root = bodies.getFirst();
        root.pos = new Vector3d(pos);

        for (int i = 0; i < bodies.size(); i++) {
            var body = bodies.get(i);

            Vector3d vel = body.pos.sub(body.posCache, new Vector3d());
            body.accel.add(vel.mul(-0.15));
            body.accel.add(0.0, -0.000098, 0.0);

            body.update(delta);
        }

        for (int i = 0; i < bodies.size() - 1; i++) {
            var body = bodies.get(i);
            var nextBody = bodies.get(i + 1);

            for (int j = 0; j < bodies.size() - 1; j++) { body.containDistance(nextBody, (1.0/bodies.size()) * CLOTH_LENGTH ); }
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
