package net.hollowed.antique.mixin.entities.states;

import net.hollowed.antique.util.interfaces.duck.ArmedRenderStateAccess;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmedEntityRenderState.class)
public class ArmedEntityRenderStateMixin implements ArmedRenderStateAccess {

    @Unique
    private Entity entity;

    @Inject(method = "updateRenderState", at = @At("HEAD"))
    private static void updateRenderState(LivingEntity entity, ArmedEntityRenderState state, ItemModelManager itemModelManager, CallbackInfo ci) {
        if (state instanceof ArmedRenderStateAccess access) {
            access.antique$setEntity(entity);
        }
    }

    @Override
    public void antique$setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public Entity antique$getEntity() {
        return this.entity;
    }
}
