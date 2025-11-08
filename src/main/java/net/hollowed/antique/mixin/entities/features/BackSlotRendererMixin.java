package net.hollowed.antique.mixin.entities.features;

import net.hollowed.combatamenities.renderer.BackSlotFeatureRenderer;
import net.hollowed.combatamenities.util.interfaces.PlayerEntityRenderStateAccess;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackSlotFeatureRenderer.class)
public abstract class BackSlotRendererMixin {

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/PlayerEntityRenderState;FF)V", at = @At("HEAD"))
    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, PlayerEntityRenderState armedEntityRenderState, float f, float g, CallbackInfo ci) {
        if (armedEntityRenderState instanceof PlayerEntityRenderStateAccess access) {
            PlayerEntity playerEntity = access.combat_Amenities$getPlayerEntity();
            if (playerEntity != null) {
                ItemStack backSlotStack = playerEntity.getInventory().getStack(41);
                if (!backSlotStack.isEmpty()) {
                    matrixStack.push();
                    if (playerEntity.getEquippedStack(EquipmentSlot.CHEST) != ItemStack.EMPTY && !(armedEntityRenderState.capeVisible && armedEntityRenderState.skinTextures.cape() != null)) {
                        matrixStack.translate(0.0F, 0.0F, 0.05F);
                    }
                }
            }
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/PlayerEntityRenderState;FF)V", at = @At("TAIL"))
    public void renderTail(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, PlayerEntityRenderState armedEntityRenderState, float f, float g, CallbackInfo ci) {
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