package net.hollowed.antique.mixin.entities.features;

import net.hollowed.antique.Antiquities;
import net.hollowed.antique.client.armor.renderers.VanillaArmorFeatureRenderer;
import net.hollowed.antique.index.AntiqueEntityLayers;
import net.hollowed.antique.client.armor.models.AdventureArmor;
import net.hollowed.antique.index.AntiqueItems;
import net.hollowed.antique.items.MyriadToolItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerFeatureAdder extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityRenderState, PlayerEntityModel> {

    @Unique
    private static final Identifier TEXTURE = Identifier.of(Antiquities.MOD_ID, "textures/entity/adventure_armor.png");
    @Unique
    private static final Identifier THICK_TEXTURE = Identifier.of(Antiquities.MOD_ID, "textures/entity/adventure_armor_thick.png");
    @Unique
    private AdventureArmor<PlayerEntityRenderState> armorModel;
    @Unique
    private boolean slim = false;

    public PlayerFeatureAdder(EntityRendererFactory.Context ctx, PlayerEntityModel model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addCustomFeature(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
        this.slim = slim;
        this.armorModel = new AdventureArmor<>(ctx.getEntityModels().getModelPart(AntiqueEntityLayers.ADVENTURE_ARMOR));
        this.addFeature(new VanillaArmorFeatureRenderer<>(this, 0, ctx.getEntityModels()));
    }

    @Inject(method = "getArmPose(Lnet/minecraft/entity/PlayerLikeEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Hand;)Lnet/minecraft/client/render/entity/model/BipedEntityModel$ArmPose;", at = @At("HEAD"), cancellable = true)
    private static void getArmPose(PlayerLikeEntity player, ItemStack stack, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        if (stack.streamTags().toList().contains(TagKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "two_handed")))) {
            if (!player.isUsingItem() && !player.handSwinging && !player.isSneaking()) {
                cir.setReturnValue(BipedEntityModel.ArmPose.CROSSBOW_CHARGE);
            } else if (player.isSneaking() || player.handSwinging) {
                cir.setReturnValue(BipedEntityModel.ArmPose.CROSSBOW_HOLD);
            }
        }
        if (stack.getItem() instanceof MyriadToolItem) {
            if (player.isSneaking() && !player.isUsingItem()) {
                cir.setReturnValue(BipedEntityModel.ArmPose.BLOCK);
            } else if (!player.isUsingItem()) {
                cir.setReturnValue(BipedEntityModel.ArmPose.BRUSH);
            }
        }
    }


    @Inject(method = "renderLeftArm", at = @At("TAIL"))
    public void renderArmoredLeftArm(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, Identifier skinTexture, boolean sleeveVisible, CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        if (player.getEquippedStack(EquipmentSlot.CHEST).getItem() == AntiqueItems.NETHERITE_PAULDRONS) {
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-5));
            matrices.translate(0.075, 0, 0);
            if (slim) {
//                VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(TEXTURE),
//                        player.getEquippedStack(EquipmentSlot.CHEST).hasGlint());
//                armorModel.leftArm.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
            } else {
                matrices.translate(-0.075, 0, 0);
//                VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(THICK_TEXTURE),
//                        player.getEquippedStack(EquipmentSlot.CHEST).hasGlint());
//                armorModel.leftArmThick.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
            }
        }
    }

    @Inject(method = "renderRightArm", at = @At("TAIL"))
    public void renderArmoredRightArm(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, Identifier skinTexture, boolean sleeveVisible, CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        if (player.getEquippedStack(EquipmentSlot.CHEST).getItem() == AntiqueItems.NETHERITE_PAULDRONS) {
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(5));
            matrices.translate(-0.075, 0, 0);
            if (slim) {
//                VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(TEXTURE),
//                        player.getEquippedStack(EquipmentSlot.CHEST).hasGlint());
//                armorModel.rightArm.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
            } else {
                matrices.translate(0.05, 0, 0);
//                VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(THICK_TEXTURE),
//                        player.getEquippedStack(EquipmentSlot.CHEST).hasGlint());
//                armorModel.rightArmThick.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
            }
        }
    }
}
