package net.hollowed.antique.mixin;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemRenderer.class)
public abstract class ItemGlowMixin {

    @Shadow
    private static void renderBakedItemQuads(MatrixStack matrices, VertexConsumer vertexConsumer, List<BakedQuad> quads, int[] tints, int light, int overlay) {
    }

    @Inject(method = "renderBakedItemModel", at = @At("HEAD"), cancellable = true)
    private static void renderBakedItemModel(BakedModel model, int[] tints, int light, int overlay, MatrixStack matrices, VertexConsumer vertexConsumer, CallbackInfo ci) {
        if (model.getParticleSprite().getContents().getId().toString().equals("antique:item/reverence") || model.getParticleSprite().getContents().getId().toString().equals("antique:item/reverence_item")) {
            light = 0xf000f0;

            Random random = Random.create();
            Direction[] var9 = Direction.values();

            for (Direction direction : var9) {
                random.setSeed(42L);
                renderBakedItemQuads(matrices, vertexConsumer, model.getQuads(null, direction, random), tints, light, overlay);
            }

            random.setSeed(42L);
            renderBakedItemQuads(matrices, vertexConsumer, model.getQuads(null, null, random), tints, light, overlay);
            ci.cancel();
        }
    }
}
