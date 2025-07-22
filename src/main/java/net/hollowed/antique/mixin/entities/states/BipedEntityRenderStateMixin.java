package net.hollowed.antique.mixin.entities.states;

import net.hollowed.antique.util.interfaces.duck.BipedEntityRenderStateAccess;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BipedEntityRenderState.class)
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
