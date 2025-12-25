package net.hollowed.antique.mixin.accessors;

import net.hollowed.antique.util.interfaces.duck.SetSpellTicks;
import net.minecraft.world.entity.monster.illager.SpellcasterIllager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SpellcasterIllager.class)
public class SpellTicksAccessor implements SetSpellTicks {

    @Shadow protected int spellCastingTickCount;

    @Override
    public void antiquities$setSpellTicks(int ticks) {
        this.spellCastingTickCount = ticks;
    }
}
