package net.hollowed.antique.mixin.entities.poses;

import net.hollowed.antique.util.interfaces.duck.BipedEntityRenderStateAccess;
import net.hollowed.antique.client.poses.MyriadAxePosing;
import net.hollowed.antique.index.AntiqueDataComponentTypes;
import net.hollowed.antique.index.AntiqueItems;
import net.hollowed.antique.items.MyriadToolItem;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public class BipedEntityModelMixin {

    @Shadow @Final public ModelPart rightArm;

    @Shadow @Final public ModelPart leftArm;

    @Shadow @Final public ModelPart head;

    @Inject(method = "positionRightArm", at = @At("HEAD"), cancellable = true)
    private void positionCustomRightArm(BipedEntityRenderState state, BipedEntityModel.ArmPose armPose, CallbackInfo ci) {
        if (state instanceof BipedEntityRenderStateAccess access && access.antique$getEntity().getActiveItem().getItem() instanceof MyriadToolItem) {
            if (access.antique$getEntity().getActiveItem().getOrDefault(AntiqueDataComponentTypes.MYRIAD_STACK, ItemStack.EMPTY).isOf(AntiqueItems.MYRIAD_AXE_HEAD)) {
                MyriadAxePosing.hold(this.rightArm, this.leftArm, this.head, true);
                ci.cancel();
            }
        }
    }

    @Inject(method = "positionLeftArm", at = @At("HEAD"), cancellable = true)
    private void positionCustomLeftArm(BipedEntityRenderState state, BipedEntityModel.ArmPose armPose, CallbackInfo ci) {
        if (state instanceof BipedEntityRenderStateAccess access && access.antique$getEntity().getActiveItem().getItem() instanceof MyriadToolItem) {
            if (access.antique$getEntity().getActiveItem().getOrDefault(AntiqueDataComponentTypes.MYRIAD_STACK, ItemStack.EMPTY).isOf(AntiqueItems.MYRIAD_AXE_HEAD)) {
                MyriadAxePosing.hold(this.rightArm, this.leftArm, this.head, false);
                ci.cancel();
            }
        }
    }
}
