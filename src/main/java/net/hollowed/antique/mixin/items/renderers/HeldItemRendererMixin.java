package net.hollowed.antique.mixin.items.renderers;

import net.hollowed.antique.Antiquities;
import net.hollowed.antique.client.renderer.cloth.ClothManager;
import net.hollowed.antique.index.AntiqueDataComponentTypes;
import net.hollowed.antique.index.AntiqueItems;
import net.hollowed.antique.util.interfaces.duck.ArmedRenderStateAccess;
import net.hollowed.antique.util.resources.ClothSkinData;
import net.hollowed.antique.util.resources.ClothSkinListener;
import net.hollowed.antique.util.resources.MyriadStaffTransformData;
import net.hollowed.antique.util.resources.MyriadStaffTransformResourceReloadListener;
import net.hollowed.combatamenities.util.items.CAComponents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(HeldItemFeatureRenderer.class)
public abstract class HeldItemRendererMixin<S extends ArmedEntityRenderState, M extends EntityModel<S> & ModelWithArms<S>> extends FeatureRenderer<S, M> {

    public HeldItemRendererMixin(FeatureRendererContext<S, M> context) {
        super(context);
    }

    @Inject(method = "renderItem", at = @At("HEAD"))
    public void renderItem(S entityState, ItemRenderState itemRenderState, Arm arm, MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, int light, CallbackInfo ci) {
        if (entityState instanceof ArmedRenderStateAccess access) {
            matrices.push();
            this.getContextModel().setArmAngle(entityState, arm, matrices);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
            boolean bl = arm == Arm.LEFT;
            matrices.translate((float)(bl ? -1 : 1) / 16.0F, 0.125F, -0.625F);
            matrices.translate(0, 0.6, 0);

            Entity entity = access.antique$getEntity();

            if (entity instanceof LivingEntity living && living.getActiveItem().isOf(AntiqueItems.MYRIAD_TOOL)
                    && living.getStackInArm(arm).getOrDefault(AntiqueDataComponentTypes.MYRIAD_STACK, ItemStack.EMPTY).isOf(AntiqueItems.MYRIAD_SHOVEL_HEAD)) {
                matrices.translate(0, -1.2, 0.2);
            }
            if (entity instanceof LivingEntity living && living.getStackInArm(arm).isOf(AntiqueItems.MYRIAD_TOOL)
                    && living.getStackInArm(arm).getOrDefault(AntiqueDataComponentTypes.MYRIAD_STACK, ItemStack.EMPTY).isOf(AntiqueItems.MYRIAD_AXE_HEAD)) {
                matrices.translate(0, -0.3, 0);
                if (living.isUsingItem()) {
                    matrices.translate(arm == Arm.RIGHT ? -0.45 : 0.45, -0.5, 0);
                }
            }
            if (entity instanceof LivingEntity living && living.getStackInArm(arm).isOf(AntiqueItems.MYRIAD_STAFF)) {
                matrices.translate(0, -0.5, 0);
            }

            ClothManager manager;

            if (entity instanceof LivingEntity living) {
                ItemStack stack = living.getStackInArm(arm);

                ClothSkinData.ClothSubData data = ClothSkinListener.getTransform(stack.getOrDefault(AntiqueDataComponentTypes.CLOTH_TYPE, "cloth"));
                Object name = stack.getOrDefault(DataComponentTypes.CUSTOM_NAME, "");
                if (stack.isOf(AntiqueItems.MYRIAD_TOOL) || (stack.isOf(AntiqueItems.MYRIAD_STAFF) && !(name.equals(Text.literal("Perfected Staff")) || name.equals(Text.literal("Orb Staff")) || name.equals(Text.literal("Lapis Staff"))))) {
                    manager = arm == Arm.RIGHT ? ClothManager.getOrCreate(entity, Antiquities.id(entity.getId() + "_right_arm")) : ClothManager.getOrCreate(entity, Antiquities.id(entity.getId() + "_left_arm"));
                    if (manager != null && stack.get(DataComponentTypes.DYED_COLOR) != null) {
                        manager.renderCloth(
                                matrices,
                                orderedRenderCommandQueue,
                                data.light() != 0 ? data.light() : light,
                                stack.getOrDefault(CAComponents.BOOLEAN_PROPERTY, false),
                                data.dyeable() ? new Color(stack.getOrDefault(DataComponentTypes.DYED_COLOR, new DyedColorComponent(0xd13a68)).rgb()) : Color.WHITE,
                                new Color(stack.getOrDefault(AntiqueDataComponentTypes.SECONDARY_DYED_COLOR, new DyedColorComponent(0xFFFFFF)).rgb()),
                                stack.get(AntiqueDataComponentTypes.CLOTH_TYPE) != null ? data.model() : null,
                                Identifier.of(stack.getOrDefault(AntiqueDataComponentTypes.CLOTH_PATTERN, "")),
                                data.length() != 0 ? data.length() : 1.4,
                                data.width() != 0 ? data.width() : 0.1,
                                data.bodyAmount() != 0 ? data.bodyAmount() : 8
                        );
                    }
                }
            }

            if (entity instanceof LivingEntity living && living.getStackInArm(arm).isOf(AntiqueItems.MYRIAD_STAFF)) {
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-10));
                matrices.translate(0, 0.955, 0.035);
                matrices.scale(0.55F, 0.55F, 0.55F);
                ItemStack stackToRender = living.getStackInArm(arm).getOrDefault(AntiqueDataComponentTypes.MYRIAD_STACK, ItemStack.EMPTY);

                matrices.scale(0.875F, 0.875F, 0.875F);
                matrices.translate(0.0, -0.035, 0.05);

                MyriadStaffTransformData data = MyriadStaffTransformResourceReloadListener.getTransform(Registries.ITEM.getId(stackToRender.getItem()));
                matrices.scale(data.scale().get(0), data.scale().get(1), data.scale().get(2));
                matrices.translate(data.translation().get(0), data.translation().get(1), data.translation().get(2));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(data.rotation().getFirst()));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(data.rotation().get(1)));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(data.rotation().get(2)));

                Identifier customModel = stackToRender.getOrDefault(DataComponentTypes.ITEM_MODEL, Registries.ITEM.getId(stackToRender.getItem()));
                if (!data.model().equals(Identifier.of("default"))) {
                    stackToRender.set(DataComponentTypes.ITEM_MODEL, data.model());
                }

                ItemRenderState stackRenderState = new ItemRenderState();
                MinecraftClient.getInstance().getItemModelManager().update(stackRenderState, stackToRender, ItemDisplayContext.NONE, MinecraftClient.getInstance().world, null, 1);
                stackRenderState.render(matrices, orderedRenderCommandQueue, light, OverlayTexture.DEFAULT_UV, 0);

                stackToRender.set(DataComponentTypes.ITEM_MODEL, customModel);
            }

            matrices.pop();
        }
    }

    @SuppressWarnings("unused")
    @Unique
    private void renderDebugPoint(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {
		DebugRenderer.drawBox(
				matrixStack,
				vertexConsumerProvider,
				-0.02F, -0.02F, -0.02F,
				0.02F,  0.02F,  0.02F,
				1.0F, 0.0F, 0.0F, 1.0F
		);
    }
}
