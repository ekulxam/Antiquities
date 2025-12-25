package net.hollowed.antique.mixin.entities.states;

import net.hollowed.antique.util.interfaces.duck.BipedEntityRenderStateAccess;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(HumanoidRenderState.class)
public class BipedEntityRenderStateMixin implements BipedEntityRenderStateAccess {

    @Unique
    private LivingEntity entity;

    @Override
    public void antique$setEntity(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public LivingEntity antique$getEntity() {
        return this.entity;
    }
}
