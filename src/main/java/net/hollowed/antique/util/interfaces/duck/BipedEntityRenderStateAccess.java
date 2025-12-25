package net.hollowed.antique.util.interfaces.duck;

import net.minecraft.world.entity.LivingEntity;

public interface BipedEntityRenderStateAccess {
    void antique$setEntity(LivingEntity entity);
    LivingEntity antique$getEntity();
}
