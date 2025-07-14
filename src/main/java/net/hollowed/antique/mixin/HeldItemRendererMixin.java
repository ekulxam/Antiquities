package net.hollowed.antique.mixin;

import net.hollowed.antique.client.item.explosive_spear.ClothManager;
import net.hollowed.antique.component.ModComponents;
import net.hollowed.antique.items.ModItems;
import net.hollowed.antique.util.ArmedRenderStateAccess;
import net.hollowed.antique.util.SpearClothAccess;
import net.hollowed.antique.util.Temp;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.Objects;

@Mixin(HeldItemFeatureRenderer.class)
public abstract class HeldItemRendererMixin<S extends ArmedEntityRenderState, M extends EntityModel<S> & ModelWithArms> extends FeatureRenderer<S, M> {

    public HeldItemRendererMixin(FeatureRendererContext<S, M> context) {
        super(context);
    }

    @Inject(method = "renderItem", at = @At("HEAD"))
    public void renderItem(S entityState, ItemRenderState itemState, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (entityState instanceof ArmedRenderStateAccess access) {
            matrices.push();
            this.getContextModel().setArmAngle(arm, matrices);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
            boolean bl = arm == Arm.LEFT;
            matrices.translate((float)(bl ? -1 : 1) / 16.0F, 0.125F, -0.625F);
            matrices.translate(0, 0.6, 0);

            MinecraftClient client = MinecraftClient.getInstance();
            ItemRenderer itemRenderer = client.getItemRenderer();

            Entity entity = access.antique$getEntity();

            if (entity instanceof LivingEntity living && living.getActiveItem().isOf(ModItems.EXPLOSIVE_SPEAR)) {
                matrices.translate(0, -1.4, 0.2);
            }
            if (entity instanceof LivingEntity living && living.getActiveItem().isOf(ModItems.MYRIAD_TOOL)
                    && living.getStackInArm(arm).getOrDefault(ModComponents.MYRIAD_STACK, ItemStack.EMPTY).isOf(ModItems.MYRIAD_SHOVEL_HEAD)) {
                matrices.translate(0, -1.2, 0.2);
            }
            if (entity instanceof LivingEntity living && living.getStackInArm(arm).isOf(ModItems.MYRIAD_TOOL)
                    && living.getStackInArm(arm).getOrDefault(ModComponents.MYRIAD_STACK, ItemStack.EMPTY).isOf(ModItems.MYRIAD_AXE_HEAD)) {
                matrices.translate(0, -0.3, 0);
                if (living.isUsingItem()) {
                    matrices.translate(arm == Arm.RIGHT ? -0.45 : 0.45, -0.5, 0);
                }
            }
            if (entity instanceof LivingEntity living && living.getStackInArm(arm).isOf(ModItems.MYRIAD_STAFF)) {
                matrices.translate(0, -0.5, 0);
            }

            ClothManager manager;
            Vec3d itemWorldPos = ClothManager.matrixToVec(matrices);

            if (entity instanceof SpearClothAccess clothAccess) {
                if (entity instanceof LivingEntity living) {
                    if (living.getStackInArm(arm).isOf(ModItems.EXPLOSIVE_SPEAR)) {
                        manager = arm == Arm.RIGHT ? clothAccess.antique$getRightArmCloth() : clothAccess.antique$getLeftArmCloth();
                        if (manager != null) {
                            manager.renderCloth(itemWorldPos, matrices, vertexConsumers, light, false, new Color(255, 0, 0, 255), false, ClothManager.BLANK_CLOTH_STRIP, 2, 0.1);
                        }
                    }
                    Object name = living.getStackInArm(arm).getOrDefault(DataComponentTypes.CUSTOM_NAME, "");
                    if (living.getStackInArm(arm).isOf(ModItems.MYRIAD_TOOL) || (living.getStackInArm(arm).isOf(ModItems.MYRIAD_STAFF) && !(name.equals(Text.literal("Perfected Staff")) || name.equals(Text.literal("Orb Staff")) || name.equals(Text.literal("Lapis Staff"))))) {
                        manager = arm == Arm.RIGHT ? clothAccess.antique$getRightArmCloth() : clothAccess.antique$getLeftArmCloth();
                        if (manager != null && living.getStackInArm(arm).get(DataComponentTypes.DYED_COLOR) != null) {
                            manager.renderCloth(itemWorldPos, matrices, vertexConsumers, light, false, new Color(Objects.requireNonNull(living.getStackInArm(arm).get(DataComponentTypes.DYED_COLOR)).rgb()), false, ClothManager.TATTERED_CLOTH_STRIP, 2, 0.1);
                        }
                    }
                }
            }

            if (entity instanceof LivingEntity living && living.getStackInArm(arm).isOf(ModItems.MYRIAD_STAFF)) {
                Temp.method1(itemRenderer, living, arm, light, matrices, vertexConsumers, client);
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
				-0.02F, -0.02F, -0.02F, // Box min (relative to pivot)
				0.02F,  0.02F,  0.02F, // Box max (relative to pivot)
				1.0F, 0.0F, 0.0F, 1.0F  // Red color
		);
    }
}
