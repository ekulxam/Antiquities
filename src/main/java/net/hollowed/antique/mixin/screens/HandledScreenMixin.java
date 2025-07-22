package net.hollowed.antique.mixin.screens;

import net.hollowed.antique.index.AntiqueComponents;
import net.hollowed.antique.items.SatchelItem;
import net.hollowed.antique.mixin.accessors.GetSlotAtAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.input.Scroller;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {
    @Unique
    private final Scroller scroller = new Scroller();

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
        }
    }

    @Unique
    private void performScrollAction(ItemStack stack, int i) {
        if (stack.getItem() instanceof SatchelItem item && !Objects.requireNonNull(stack.get(AntiqueComponents.SATCHEL_STACK)).isEmpty()) {
            item.setInternalIndex(Scroller.scrollCycling(i, item.getInternalIndex(), Objects.requireNonNull(stack.get(AntiqueComponents.SATCHEL_STACK)).size()));
        }
    }
}
