package net.hollowed.antique.mixin.accessors;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContainerScreen.class)
public interface GetSlotAtAccessor {
    @Invoker("getHoveredSlot")
    Slot invokeGetSlotAt(double mouseX, double mouseY);
}