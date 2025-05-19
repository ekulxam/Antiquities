package net.hollowed.antique.mixin.features;

import net.hollowed.antique.util.IsHuskGetter;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ZombieEntityRenderState.class)
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
