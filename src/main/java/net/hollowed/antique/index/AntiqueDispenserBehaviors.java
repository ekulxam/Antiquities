package net.hollowed.antique.index;

import net.minecraft.block.DispenserBlock;

public interface AntiqueDispenserBehaviors {

    static void initialize() {
        DispenserBlock.registerProjectileBehavior(AntiqueItems.SMOKE_BOMB);
    }
}
