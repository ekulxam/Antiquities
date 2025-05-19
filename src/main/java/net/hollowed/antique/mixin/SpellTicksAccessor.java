package net.hollowed.antique.mixin;

import net.hollowed.antique.util.SetSpellTicks;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SpellcastingIllagerEntity.class)
public class SpellTicksAccessor implements SetSpellTicks {

    @Shadow protected int spellTicks;

    @Override
    public void antiquities$setSpellTicks(int ticks) {
        this.spellTicks = ticks;
    }
}
