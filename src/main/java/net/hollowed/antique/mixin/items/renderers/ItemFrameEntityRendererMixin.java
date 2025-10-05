package net.hollowed.antique.mixin.items.renderers;

import net.hollowed.antique.index.AntiqueDataComponentTypes;
import net.hollowed.antique.index.AntiqueItems;
import net.hollowed.antique.util.resources.MyriadStaffTransformData;
import net.hollowed.antique.util.resources.MyriadStaffTransformResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.ItemFrameEntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ItemFrameEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFrameEntityRenderer.class)
public abstract class ItemFrameEntityRendererMixin {

    @Shadow public abstract Vec3d getPositionOffset(EntityRenderState par1);

    @Unique
    private ItemStack stack = ItemStack.EMPTY;

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/decoration/ItemFrameEntity;Lnet/minecraft/client/render/entity/state/ItemFrameEntityRenderState;F)V", at = @At("TAIL"))
    public void getStack(ItemFrameEntity itemFrameEntity, ItemFrameEntityRenderState itemFrameEntityRenderState, float f, CallbackInfo ci) {
        stack = itemFrameEntity.getHeldItemStack();
    }

    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/ItemFrameEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V", at = @At("TAIL"))
    public void render(ItemFrameEntityRenderState itemFrameEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState, CallbackInfo ci) {
        matrixStack.push();
        Direction direction = itemFrameEntityRenderState.facing;
        Vec3d vec3d = this.getPositionOffset(itemFrameEntityRenderState);
        matrixStack.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
        matrixStack.translate(direction.getOffsetX() * 0.46875, direction.getOffsetY() * 0.46875, direction.getOffsetZ() * 0.46875);
        float f;
        float g;
        if (direction.getAxis().isHorizontal()) {
            f = 0.0F;
            g = 180.0F - direction.getPositiveHorizontalDegrees();
        } else {
            f = -90 * direction.getDirection().offset();
            g = 180.0F;
        }

        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(g));

        int j = itemFrameEntityRenderState.rotation % 8 * 2;
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(j * 360.0F / 16.0F));

        matrixStack.translate(-0.075, 0.075, 0.45);
        matrixStack.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(-45));
        matrixStack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(90));
        matrixStack.scale(0.15F, 0.15F, 0.15F);

        if (stack.isOf(AntiqueItems.MYRIAD_STAFF)) {
            ItemStack stackToRender = stack.getOrDefault(AntiqueDataComponentTypes.MYRIAD_STACK, ItemStack.EMPTY);

            matrixStack.scale(0.875F, 0.875F, 0.875F);
            matrixStack.translate(0.0, -0.035, 0.05);

            MyriadStaffTransformData data = MyriadStaffTransformResourceReloadListener.getTransform(Registries.ITEM.getId(stackToRender.getItem()));
            matrixStack.scale(data.scale().get(0), data.scale().get(1), data.scale().get(2));
            matrixStack.translate(data.translation().get(0), data.translation().get(1), data.translation().get(2));
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(data.rotation().getFirst()));
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(data.rotation().get(1)));
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(data.rotation().get(2)));

            Identifier customModel = stackToRender.getOrDefault(DataComponentTypes.ITEM_MODEL, Registries.ITEM.getId(stackToRender.getItem()));
            if (!data.model().equals(Identifier.of("default"))) {
                stackToRender.set(DataComponentTypes.ITEM_MODEL, data.model());
            }

            ItemRenderState stackRenderState = new ItemRenderState();
            MinecraftClient.getInstance().getItemModelManager().update(stackRenderState, stackToRender, ItemDisplayContext.NONE, MinecraftClient.getInstance().world, null, 1);
            stackRenderState.render(matrixStack, orderedRenderCommandQueue, itemFrameEntityRenderState.light, OverlayTexture.DEFAULT_UV, 0);

            stackToRender.set(DataComponentTypes.ITEM_MODEL, customModel);
        }

        matrixStack.pop();
    }
}
