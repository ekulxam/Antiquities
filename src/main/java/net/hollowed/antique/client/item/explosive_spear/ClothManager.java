package net.hollowed.antique.client.item.explosive_spear;

import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;

public class ClothManager {

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
        for (int i = 0; i < bodies.size() - 1; i++) {
            // Update vel
            var body = bodies.get(i);
            body.update(delta);

            // Update constraint
            var nextBody = bodies.get(i + 1);
            body.containDistance(nextBody);

            if(body.pos.distance(nextBody.pos) > 5.0) nextBody.pos = body.pos.sub(0.0, 0.1, 0.0, new Vector3d());

            // Update Gravity
            body.accel.add(0.0, -0.98, 0.0);
        }

        // Update parent position
        var root = bodies.getFirst();
        root.pos = new Vector3d(pos);
//        Vector3d diff = root.pos.sub(pos, new Vector3d());
//        root.vel.add(diff);
//        root.pos.add(diff);
    }


    public void renderCloth(Vec3d position, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getCutoutMipped());
        Vector3d danglePos = new Vector3d(position.x, position.y, position.z);

        pos = new Vector3d(danglePos);
        double thickness = 0.5;

        int count = bodies.size();
        for (int i = 0; i < count - 1; i++) {

            ClothBody body = bodies.get(i);
            ClothBody nextBody = bodies.get(i + 1);

            var pos = body.getPos();
            var nextPos = nextBody.getPos();

            float uvTop = (1f / count) * i;
            float uvBot = uvTop + (1f / count);

            var a = pos.add(-thickness, 0.0, 0.0, new Vector3d());
            var b = pos.add(thickness, 0.0, 0.0, new Vector3d());
            var c = nextPos.add(thickness, 0.0, 0.0, new Vector3d());
            var d = nextPos.add(-thickness, 0.0, 0.0, new Vector3d());

//            var a = pos;
//            var b = new Vector3d(1.0, 1.0, 0.0).add(new Vector3d(0.0, 0.0, 1.0));
//            var c = nextPos;
//            var d = new Vector3d(0.0, 0.0, 0.0).add(new Vector3d(0.0, 0.0, 1.0));

//            var a = new Vector3d(0.0, 1.0, 0.0);
//            var b = new Vector3d(1.0, 1.0, 0.0);
//            var c = new Vector3d(1.0, 0.0, 0.0);
//            var d = new Vector3d(0.0, 0.0, 0.0);
            drawQuad(
                    new Matrix4f(),
                    vertexConsumer,
                    a, b, c, d,
                    new Vec2f(0f,uvTop),
                    new Vec2f(1f,uvTop),
                    new Vec2f(1f,uvBot),
                    new Vec2f(0f,uvBot),
                    light);

        }

        matrices.pop();
    }

    public void drawQuad(Matrix4f matrix, VertexConsumer vertexConsumer, Vector3d posA, Vector3d posB, Vector3d posC, Vector3d posD, Vec2f uvA, Vec2f uvB, Vec2f uvC, Vec2f uvD, int light) {
        // Draw a line from pos1 to pos2
        var cam = MinecraftClient.getInstance().gameRenderer.getCamera().getPos().multiply(-1); //.getClientCameraPosVec(MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true)).multiply(-1);
        vertexConsumer.vertex(matrix, (float) ((float) posA.x + cam.x), (float) ((float) posA.y + cam.y), (float) ((float) posA.z + cam.z)).color(255, 255, 255, 255).normal(0f,0f,1f).light(light).texture(uvA.x, uvA.y);
        vertexConsumer.vertex(matrix, (float) ((float) posB.x + cam.x), (float) ((float) posB.y + cam.y), (float) ((float) posB.z + cam.z)).color(255, 255, 255, 255).normal(0f,0f,1f).light(light).texture(uvB.x, uvB.y);
        vertexConsumer.vertex(matrix, (float) ((float) posC.x + cam.x), (float) ((float) posC.y + cam.y), (float) ((float) posC.z + cam.z)).color(255, 255, 255, 255).normal(0f,0f,1f).light(light).texture(uvC.x, uvC.y);
        vertexConsumer.vertex(matrix, (float) ((float) posD.x + cam.x), (float) ((float) posD.y + cam.y), (float) ((float) posD.z + cam.z)).color(255, 255, 255, 255).normal(0f,0f,1f).light(light).texture(uvD.x, uvD.y);
    }
}
