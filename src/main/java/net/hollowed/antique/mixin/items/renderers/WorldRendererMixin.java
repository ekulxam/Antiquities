package net.hollowed.antique.mixin.items.renderers;

import com.llamalad7.mixinextras.sugar.Local;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.client.renderer.cloth.ClothManager;
import net.hollowed.antique.entities.MyriadShovelEntity;
import net.hollowed.antique.entities.renderer.MyriadShovelRenderState;
import net.hollowed.antique.index.AntiqueDataComponentTypes;
import net.hollowed.antique.util.resources.ClothSkinData;
import net.hollowed.antique.util.resources.ClothSkinListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.FramePass;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.state.WorldRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.Color;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    // https://github.com/FabricMC/fabric/blob/060c7037d9131d03ac076029e2d225ecb2c5635a/fabric-rendering-v1/src/client/java/net/fabricmc/fabric/mixin/client/rendering/WorldRendererMixin.java#L156

    @Shadow @Final private DefaultFramebufferSet framebufferSet;

    @Shadow @Final private WorldRenderState worldRenderState;

    @Shadow @Final private OrderedRenderCommandQueueImpl entityRenderCommandQueue;

    @Shadow @Final private EntityRenderManager entityRenderManager;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getCloudRenderModeValue()Lnet/minecraft/client/option/CloudRenderMode;"))
    private void beforeClouds(CallbackInfo ci, @Local FrameGraphBuilder frameGraphBuilder) {
        FramePass afterTranslucentPass = frameGraphBuilder.createPass("afterTranslucent");
        framebufferSet.mainFramebuffer = afterTranslucentPass.transfer(framebufferSet.mainFramebuffer);

        afterTranslucentPass.setRenderer(() -> {
            MatrixStack matrixStack = new MatrixStack();
            matrixStack.push();
            Vec3d vec3d = this.worldRenderState.cameraRenderState.pos;
            matrixStack.translate(-vec3d.x, -vec3d.y, -vec3d.z);
            this.worldRenderState.entityRenderStates.forEach(entityRenderState -> {
                if (!(entityRenderState instanceof MyriadShovelRenderState myriadShovelRenderState)) {
                    return;
                }
                if (!(myriadShovelRenderState.entity instanceof MyriadShovelEntity entity)) {
                    return;
                }
                matrixStack.push();
                Vec3d entityPos = this.entityRenderManager.getRenderer(myriadShovelRenderState).getPositionOffset(myriadShovelRenderState).add(entityRenderState.x, entityRenderState.y, entityRenderState.z);
                matrixStack.translate(entityPos.x, entityPos.y, entityPos.z);
                {
                    matrixStack.push();

                    float multiplier = 1.25F;

                    matrixStack.translate(myriadShovelRenderState.entity.getRotationVec(0).multiply(multiplier, multiplier, -multiplier));

                    matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(myriadShovelRenderState.entity.getYaw() - 180.0F));
                    matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(myriadShovelRenderState.entity.getPitch() - 105.0F));

                    matrixStack.scale(1.5F, 1.5F, 1.5F);
                    matrixStack.translate(0, 0, 0.125);

                    matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
                    matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(15.0F));
                    matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-20.0F));

                    ItemStack shovel = entity.getItemStack();

                    ClothSkinData.ClothSubData data = ClothSkinListener.getTransform(shovel.getOrDefault(AntiqueDataComponentTypes.CLOTH_TYPE, "cloth"));

                    ClothManager manager = ClothManager.getOrCreate(entity, Antiquities.id(entity.getId() + "_spade"));
                    if(manager != null && shovel.get(DataComponentTypes.DYED_COLOR) != null) {
                        matrixStack.translate(0.05, 0.3, 0.1);
                        Vec3d itemWorldPos = ClothManager.matrixToVec(matrixStack);
                        manager.renderCloth(
                                itemWorldPos,
                                matrixStack,
                                this.entityRenderCommandQueue,
                                data.light() != 0 ? data.light() : myriadShovelRenderState.light,
                                myriadShovelRenderState.glow,
                                data.dyeable() ? new Color(myriadShovelRenderState.color) : Color.WHITE,
                                new Color(myriadShovelRenderState.overlayColor),
                                true,
                                data.model(),
                                Identifier.of(myriadShovelRenderState.pattern),
                                data.length() != 0 ? data.length() : 1.4,
                                data.width() != 0 ? data.width() : 0.1,
                                data.bodyAmount() != 0 ? data.bodyAmount() : 8
                        );
                    }
                    matrixStack.pop();
                }
                matrixStack.pop();
            });
            matrixStack.pop();
        });
    }
}
