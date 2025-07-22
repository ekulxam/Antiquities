package net.hollowed.antique.mixin.screens;

import com.llamalad7.mixinextras.sugar.Local;
import net.hollowed.antique.index.AntiqueKeyBindings;
import net.hollowed.antique.index.AntiqueComponents;
import net.hollowed.antique.items.SatchelItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.input.Scroller;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(Mouse.class)
public class MouseMixin {

    @Shadow @Final private MinecraftClient client;

    @SuppressWarnings("all")
    @Inject(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getInventory()Lnet/minecraft/entity/player/PlayerInventory;", shift = At.Shift.BEFORE), cancellable = true)
    public void antique$makeSatchelScrollable(long window, double horizontal, double vertical, CallbackInfo ci, @Local int i) {
        if (AntiqueKeyBindings.showSatchel.isPressed()) {
            assert client.player != null;
            ItemStack satchel = client.player.getEquippedStack(EquipmentSlot.LEGS);
            assert satchel != null;
            if (satchel.getItem() instanceof SatchelItem satchelItem && satchel.get(AntiqueComponents.SATCHEL_STACK) != null && !Objects.requireNonNull(satchel.get(AntiqueComponents.SATCHEL_STACK)).isEmpty() && !client.player.isSneaking()) {
                satchelItem.setIndex(Scroller.scrollCycling(i, satchelItem.getIndex(), 8));
                ci.cancel();
            }
        }
    }
}