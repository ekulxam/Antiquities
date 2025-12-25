package net.hollowed.antique.mixin.entities.features;

import com.mojang.blaze3d.vertex.PoseStack;
import net.hollowed.antique.AntiquitiesClient;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class ArmorRendererMixin<S extends HumanoidRenderState, M extends HumanoidModel<S>> extends RenderLayer<S, M> {

    public ArmorRendererMixin(RenderLayerParent<S, M> context) {
        super(context);
    }

    @Inject(method = "renderArmorPiece", at = @At("HEAD"), cancellable = true)
    private void renderArmor(PoseStack matrices, SubmitNodeCollector orderedRenderCommandQueue, ItemStack stack, EquipmentSlot slot, int light, S bipedEntityRenderState, CallbackInfo ci) {
        if (AntiquitiesClient.BETTER_ARMOR_LIST.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()))) {
            ci.cancel();
        }
    }
}
