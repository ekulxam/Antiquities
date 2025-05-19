package net.hollowed.antique.client.armor.renderers;

import net.hollowed.antique.Antiquities;
import net.hollowed.antique.client.armor.models.AdventureArmor;
import net.hollowed.antique.items.ModItems;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Unique;

public class FirstPersonRenderer {

    @Unique
    private static final Identifier TEXTURE = Identifier.of(Antiquities.MOD_ID, "textures/entity/adventure_armor.png");
    @Unique
    private static final Identifier THICK_TEXTURE = Identifier.of(Antiquities.MOD_ID, "textures/entity/adventure_armor_thick.png");

    public static void renderLeftArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, boolean slim, AdventureArmor armorModel) {
        if (player.getEquippedStack(EquipmentSlot.CHEST).getItem() == ModItems.NETHERITE_PAULDRONS) {
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-5));
            matrices.translate(0.075, 0, 0);
            if (slim) {
                VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(TEXTURE),
                        player.getEquippedStack(EquipmentSlot.CHEST).hasGlint());
                armorModel.leftArm.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
            } else {
                matrices.translate(-0.075, 0, 0);
                VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(THICK_TEXTURE),
                        player.getEquippedStack(EquipmentSlot.CHEST).hasGlint());
                armorModel.leftArmThick.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
            }
        }
    }

    public static void renderRightArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, boolean slim, AdventureArmor armorModel) {
        if (player.getEquippedStack(EquipmentSlot.CHEST).getItem() == ModItems.NETHERITE_PAULDRONS) {
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(5));
            matrices.translate(-0.075, 0, 0);
            if (slim) {
                VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(TEXTURE),
                        player.getEquippedStack(EquipmentSlot.CHEST).hasGlint());
                armorModel.rightArm.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
            } else {
                matrices.translate(0.05, 0, 0);
                VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(THICK_TEXTURE),
                        player.getEquippedStack(EquipmentSlot.CHEST).hasGlint());
                armorModel.rightArmThick.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
            }
        }
    }
}
