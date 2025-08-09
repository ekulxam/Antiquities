package net.hollowed.antique.mixin.entities.features;

import net.hollowed.antique.index.AntiqueComponents;
import net.hollowed.antique.index.AntiqueItems;
import net.hollowed.antique.util.resources.MyriadStaffTransformData;
import net.hollowed.antique.util.resources.MyriadStaffTransformResourceReloadListener;
import net.hollowed.combatamenities.config.CAConfig;
import net.hollowed.combatamenities.renderer.BackSlotFeatureRenderer;
import net.hollowed.combatamenities.util.interfaces.PlayerEntityRenderStateAccess;
import net.hollowed.combatamenities.util.json.BackTransformData;
import net.hollowed.combatamenities.util.json.BackTransformResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BackSlotFeatureRenderer.class)
public abstract class BackSlotRendererMixin extends HeldItemFeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {

    @Shadow protected abstract void applyDynamicMovement(MatrixStack matrixStack, PlayerEntity playerEntity, Item item);

    public BackSlotRendererMixin(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> featureRendererContext) {
        super(featureRendererContext);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/PlayerEntityRenderState;FF)V", at = @At("HEAD"))
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, PlayerEntityRenderState armedEntityRenderState, float limbSwing, float limbSwingAmount, CallbackInfo ci) {
        if (armedEntityRenderState instanceof PlayerEntityRenderStateAccess access) {
            PlayerEntity playerEntity = access.combat_Amenities$getPlayerEntity();
            if (playerEntity != null) {
                ItemStack backSlotStack = playerEntity.getInventory().getStack(41);
                if (!backSlotStack.isEmpty()) {
                    Item item = backSlotStack.getItem();
                    Identifier itemId = Registries.ITEM.getId(item);
                    BackTransformData transformData = BackTransformResourceReloadListener.getTransform(itemId, backSlotStack.getOrDefault(net.hollowed.combatamenities.util.items.ModComponents.INTEGER_PROPERTY, -1).toString());

                    Arm arm = armedEntityRenderState.mainArm;
                    boolean right = arm == Arm.RIGHT && !CAConfig.flipBackslotDisplay || arm == Arm.LEFT && CAConfig.flipBackslotDisplay;
                    boolean flip = !right && !(item instanceof BlockItem) && !transformData.noFlip();

                    List<Float> translation;
                    List<Float> rotation;

                    matrixStack.push();
                    this.getContextModel().body.applyTransform(matrixStack);
                    if (playerEntity.getEquippedStack(EquipmentSlot.CHEST) != ItemStack.EMPTY) {
                        matrixStack.translate(0.0F, 0.0F, 0.1F);
                    }

                    if (armedEntityRenderState.capeVisible) {
                        matrixStack.translate(0.0F, 0.0F, 0.05F);
                    }

                    matrixStack.translate(0.0, 0, 0.125);
                    matrixStack.multiply((new Quaternionf()).rotateY(-3.1415927F).rotateX(transformData.sway() * -(6.0F + armedEntityRenderState.field_53537 / 2.0F + armedEntityRenderState.field_53536) * 0.017453292F).rotateZ(-(armedEntityRenderState.field_53538 / 2.0F * 0.017453292F)).rotateY((180.0F - armedEntityRenderState.field_53538 / 2.0F) * 0.017453292F));
                    matrixStack.translate(0.0, 0.35, 0.0);
                    if (flip) {
                        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
                    }

                    if (playerEntity instanceof OtherClientPlayerEntity) {
                        this.applyDynamicMovement(matrixStack, playerEntity, item);
                    } else if (playerEntity instanceof ClientPlayerEntity) {
                        this.applyDynamicMovement(matrixStack, playerEntity, item);
                    }

                    translation = transformData.translation();
                    matrixStack.translate(translation.get(0), translation.get(1), flip ? translation.get(2) : -(Float) translation.get(2));
                    matrixStack.translate(0.125, 0.125, 0.025);
                    if (right && (item instanceof BlockItem || transformData.noFlip())) {
                        matrixStack.translate(translation.getFirst() * -2.0F, 0.0F, 0.0F);
                    }

                    rotation = transformData.rotation();
                    matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotation.get(0)));
                    if (flip) {
                        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotation.getFirst() * -2.0F));
                    }

                    matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation.get(1)));
                    matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation.get(2)));
                    if (right && (item instanceof BlockItem || transformData.noFlip())) {
                        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation.get(2) * -2.0F));
                    }

                    if (backSlotStack.isOf(AntiqueItems.MYRIAD_STAFF)) {

                        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
                        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(45));
                        matrixStack.translate(0, 1.1, 0);
                        matrixStack.scale(0.55F, 0.55F, 0.55F);

                        MinecraftClient client = MinecraftClient.getInstance();
                        ItemRenderer itemRenderer = client.getItemRenderer();

                        ItemStack stackToRender = backSlotStack.getOrDefault(AntiqueComponents.MYRIAD_STACK, ItemStack.EMPTY);

                        Object name = backSlotStack.getOrDefault(DataComponentTypes.CUSTOM_NAME, "");
                        if (!(name.equals(Text.literal("Perfected Staff")) || name.equals(Text.literal("Orb Staff")) || name.equals(Text.literal("Lapis Staff")))) {
                            matrixStack.translate(0, 0.9, 0);
                        }

                        matrixStack.scale(0.875F, 0.875F, 0.875F);
                        matrixStack.translate(0.0, -0.035, 0.05);

                        MyriadStaffTransformData data = MyriadStaffTransformResourceReloadListener.getTransform(Registries.ITEM.getId(stackToRender.getItem()));
                        matrixStack.scale(data.scale().get(0), data.scale().get(1), data.scale().get(2));

                        matrixStack.translate(0, -0.05, 0);
                        matrixStack.translate(data.translation().get(0), data.translation().get(1), data.translation().get(2));
                        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(data.rotation().getFirst()));
                        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(data.rotation().get(1)));
                        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(data.rotation().get(2)));

                        Identifier customModel = stackToRender.getOrDefault(DataComponentTypes.ITEM_MODEL, Registries.ITEM.getId(stackToRender.getItem()));
                        if (!data.model().equals(Identifier.of("default"))) {
                            stackToRender.set(DataComponentTypes.ITEM_MODEL, data.model());
                        }

                        itemRenderer.renderItem(
                                stackToRender,
                                ItemDisplayContext.NONE,
                                light,
                                OverlayTexture.DEFAULT_UV,
                                matrixStack,
                                vertexConsumerProvider,
                                client.world,
                                1
                        );

                        stackToRender.set(DataComponentTypes.ITEM_MODEL, customModel);
                    }

                    matrixStack.pop();

                    matrixStack.push();
                    if (playerEntity.getEquippedStack(EquipmentSlot.CHEST) != ItemStack.EMPTY) {
                        matrixStack.translate(0.0F, 0.0F, 0.05F);
                    }
                }
            }
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/PlayerEntityRenderState;FF)V", at = @At("TAIL"))
    public void renderTail(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, PlayerEntityRenderState armedEntityRenderState, float limbSwing, float limbSwingAmount, CallbackInfo ci) {
        if (armedEntityRenderState instanceof PlayerEntityRenderStateAccess access) {
            PlayerEntity playerEntity = access.combat_Amenities$getPlayerEntity();
            if (playerEntity != null) {
                ItemStack backSlotStack = playerEntity.getInventory().getStack(41);
                if (!backSlotStack.isEmpty()) {
                    matrixStack.pop();
                }
            }
        }
    }
}
