package net.hollowed.antique.mixin.screens;

import net.hollowed.antique.index.AntiqueDataComponentTypes;
import net.hollowed.antique.index.AntiqueItems;
import net.hollowed.antique.items.BagOfTricksItem;
import net.hollowed.antique.items.SatchelItem;
import net.hollowed.antique.mixin.accessors.GetSlotAtAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.input.Scroller;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {
    @Shadow @Nullable protected Slot focusedSlot;
    @Unique
    private final Scroller scroller = new Scroller();
    @Unique
    private ItemStack lastSatchel = null;
    @Unique
    private ItemStack lastBag = null;

    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    public void onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount, CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen != null) {
            boolean bl = client.options.getDiscreteMouseScroll().getValue();
            double d = client.options.getMouseWheelSensitivity().getValue();
            double e = (bl ? Math.signum(horizontalAmount) : horizontalAmount) * d;
            double f = (bl ? Math.signum(verticalAmount) : verticalAmount) * d;
            Vector2i vector2i = this.scroller.update(e, f);
            if (vector2i.x == 0 && vector2i.y == 0) {
                return;
            }

            int i = vector2i.y == 0 ? -vector2i.x : vector2i.y;
            Slot hoveredSlot = ((GetSlotAtAccessor) client.currentScreen).invokeGetSlotAt(mouseX, mouseY);

            if (hoveredSlot != null && !hoveredSlot.getStack().isEmpty() && hoveredSlot.getStack().getItem() instanceof SatchelItem) {
                performScrollAction(hoveredSlot.getStack(), i);
                cir.setReturnValue(true);
            }
            if (hoveredSlot != null && !hoveredSlot.getStack().isEmpty() && hoveredSlot.getStack().getItem() instanceof BagOfTricksItem) {
                performBagScrollAction(hoveredSlot.getStack(), i);
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "drawMouseoverTooltip", at = @At("HEAD"))
    private void drawMouseoverTooltip(DrawContext context, int x, int y, CallbackInfo ci) {
        if (this.focusedSlot == null && lastSatchel != null || lastSatchel != null && !this.focusedSlot.hasStack() || lastSatchel != null && this.focusedSlot.getStack() != lastSatchel) SatchelItem.setInternalIndex(lastSatchel, -1);
        if (this.focusedSlot != null && this.focusedSlot.hasStack() && this.focusedSlot.getStack().isOf(AntiqueItems.SATCHEL)) lastSatchel = this.focusedSlot.getStack();

        if (this.focusedSlot == null && lastBag != null || lastBag != null && !this.focusedSlot.hasStack() || lastBag != null && this.focusedSlot.getStack() != lastBag) BagOfTricksItem.setInternalIndex(lastBag, -1);
        if (this.focusedSlot != null && this.focusedSlot.hasStack() && this.focusedSlot.getStack().isOf(AntiqueItems.BAG_OF_TRICKS)) lastBag = this.focusedSlot.getStack();
    }

    @Inject(method = "isItemTooltipSticky", at = @At("HEAD"), cancellable = true)
    private void stickyTooltips(ItemStack item, CallbackInfoReturnable<Boolean> cir) {
        if (item.getOrDefault(AntiqueDataComponentTypes.STICKY_TOOLTIP, false)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void close(CallbackInfo ci) {
        if (lastSatchel != null) SatchelItem.setInternalIndex(lastSatchel, -1);
        if (lastBag != null) BagOfTricksItem.setInternalIndex(lastBag, -1);
    }

    @Unique
    private void performScrollAction(ItemStack stack, int i) {
        if (stack.getItem() instanceof SatchelItem && !Objects.requireNonNull(stack.get(AntiqueDataComponentTypes.SATCHEL_STACK)).isEmpty()) {
            SatchelItem.setInternalIndex(stack, Scroller.scrollCycling(i, SatchelItem.getInternalIndex(stack), Objects.requireNonNull(stack.get(AntiqueDataComponentTypes.SATCHEL_STACK)).size()));
        }
    }
    @Unique
    private void performBagScrollAction(ItemStack stack, int i) {
        if (stack.getItem() instanceof BagOfTricksItem && !Objects.requireNonNull(stack.get(AntiqueDataComponentTypes.SATCHEL_STACK)).isEmpty()) {
            BagOfTricksItem.setInternalIndex(stack, Scroller.scrollCycling(i, BagOfTricksItem.getInternalIndex(stack), Objects.requireNonNull(stack.get(AntiqueDataComponentTypes.SATCHEL_STACK)).size()));
        }
    }
}
