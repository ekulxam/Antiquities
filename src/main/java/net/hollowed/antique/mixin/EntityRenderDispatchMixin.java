package net.hollowed.antique.mixin;

import net.hollowed.antique.entities.custom.MyriadShovelEntity;
import net.hollowed.antique.entities.parts.MyriadShovelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatchMixin {

    @Inject(method = "renderHitbox", at = @At("HEAD"))
    private static void renderBox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, float red, float green, float blue, CallbackInfo ci) {
//        if (entity instanceof MyriadShovelEntity) {
//            for (MyriadShovelPart enderDragonPart : ((MyriadShovelEntity)entity).getBodyParts()) {
//                matrices.push();
//                matrices.translate(-entity.getX(), -entity.getY(), -entity.getZ());
//                VertexRendering.drawBox(
//                        matrices,
//                        vertices,
//                        enderDragonPart.getBoundingBox(),
//                        0.25F,
//                        1.0F,
//                        0.0F,
//                        1.0F
//                );
//                matrices.pop();
//            }
//        }
    }
}
