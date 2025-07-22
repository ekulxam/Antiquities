package net.hollowed.antique.mixin.accessors;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HandledScreen.class)
public interface GetSlotAtAccessor {
    @Invoker("getSlotAt")
    Slot invokeGetSlotAt(double mouseX, double mouseY);
}