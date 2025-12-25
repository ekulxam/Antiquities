package net.hollowed.antique.mixin.entities.states;

import net.hollowed.antique.util.interfaces.duck.IsHuskGetter;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ZombieRenderState.class)
public class ZombieRenderStateHuskAdder implements IsHuskGetter {
    @Unique
    private boolean isHusk = false;

    @Override
    public void antiquities$setHusk(boolean isHusk) {
        this.isHusk = isHusk;
    }

    @Override
    public boolean antiquities$getHusk() {
        return isHusk;
    }
}
