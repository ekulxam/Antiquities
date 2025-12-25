package net.hollowed.antique.mixin.entities.states;

import net.hollowed.antique.util.interfaces.duck.IsWitherGetter;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SkeletonRenderState.class)
public class SkeletonRenderStateWitherAdder implements IsWitherGetter {
    @Unique
    private boolean isWither = false;


    @Override
    public void antiquities$setWither(boolean wither) {
        this.isWither = wither;
    }

    @Override
    public boolean antiquities$getWither() {
        return isWither;
    }
}
