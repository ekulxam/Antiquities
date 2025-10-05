package net.hollowed.antique.mixin.entities.states;

import net.hollowed.antique.util.interfaces.duck.IsWitherGetter;
import net.minecraft.client.render.entity.state.SkeletonEntityRenderState;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SkeletonEntityRenderState.class)
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
