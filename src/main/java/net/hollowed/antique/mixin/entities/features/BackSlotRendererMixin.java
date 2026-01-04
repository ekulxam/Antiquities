package net.hollowed.antique.mixin.entities.features;

import com.mojang.blaze3d.vertex.PoseStack;
import net.hollowed.combatamenities.renderer.BackSlotFeatureRenderer;
import net.hollowed.combatamenities.util.interfaces.PlayerEntityRenderStateAccess;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackSlotFeatureRenderer.class)
public abstract class BackSlotRendererMixin {

    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V", at = @At("HEAD"))
    public void render(PoseStack matrixStack, SubmitNodeCollector orderedRenderCommandQueue, int i, AvatarRenderState armedEntityRenderState, float f, float g, CallbackInfo ci) {
        if (armedEntityRenderState instanceof PlayerEntityRenderStateAccess access) {
            Player playerEntity = access.combat_Amenities$getPlayerEntity();
            if (playerEntity != null) {
                ItemStack backSlotStack = playerEntity.getInventory().getItem(41);
                if (!backSlotStack.isEmpty()) {
                    matrixStack.pushPose();
                    if (playerEntity.getItemBySlot(EquipmentSlot.CHEST) != ItemStack.EMPTY && !armedEntityRenderState.showCape) {
                        matrixStack.translate(0.0F, 0.0F, 0.05F);
                    }
                }
            }
        }
    }

    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V", at = @At("TAIL"))
    public void renderTail(PoseStack matrixStack, SubmitNodeCollector orderedRenderCommandQueue, int i, AvatarRenderState armedEntityRenderState, float f, float g, CallbackInfo ci) {
        if (armedEntityRenderState instanceof PlayerEntityRenderStateAccess access) {
            Player playerEntity = access.combat_Amenities$getPlayerEntity();
            if (playerEntity != null) {
                ItemStack backSlotStack = playerEntity.getInventory().getItem(41);
                if (!backSlotStack.isEmpty()) {
                    matrixStack.popPose();
                }
            }
        }
    }
}