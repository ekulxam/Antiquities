package net.hollowed.antique.mixin;

import net.hollowed.antique.Antiquities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class NoAttackMixin {

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    public void swingCancel(CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && !player.getStackInHand(Hand.MAIN_HAND).streamTags().toList().contains(TagKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "bypass_attack_cooldown")))) {
            if (player.getItemCooldownManager().isCoolingDown(player.getStackInHand(Hand.MAIN_HAND))) {
                cir.setReturnValue(false);
            }
        }
    }
}
