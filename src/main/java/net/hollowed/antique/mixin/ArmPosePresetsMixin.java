package net.hollowed.antique.mixin;

import net.hollowed.antique.Antiquities;
import net.hollowed.antique.items.ModItems;
import net.hollowed.antique.items.custom.MyriadToolItem;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BipedEntityRenderer.class)
public class ArmPosePresetsMixin<T extends MobEntity, S extends BipedEntityRenderState, M extends BipedEntityModel<S>> {

    @Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
    private void getArmPose(T player, Arm arm, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        ItemStack itemStack = player.getStackInArm(arm);
        if (itemStack.streamTags().toList().contains(TagKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "two_handed")))) {
            if (!player.isUsingItem() && !player.handSwinging && !player.isSneaking()) {
                cir.setReturnValue(BipedEntityModel.ArmPose.CROSSBOW_CHARGE);
            } else if (player.isSneaking() || player.handSwinging) {
                cir.setReturnValue(BipedEntityModel.ArmPose.CROSSBOW_HOLD);
            }
        }
        if (itemStack.getItem() instanceof MyriadToolItem || itemStack.isOf(ModItems.MYRIAD_STAFF)) {
            if (player.isSneaking() && !player.isUsingItem()) {
                cir.setReturnValue(BipedEntityModel.ArmPose.BLOCK);
            } else if (!player.isUsingItem()) {
                cir.setReturnValue(BipedEntityModel.ArmPose.BRUSH);
            }
        }
    }
}
