package net.hollowed.antique.util.interfaces.duck;

import dev.dominion.ecs.api.Entity;
import net.dustley.lemon.modules.citrus_physics.component.collision.colliders.Collider;

public interface MyriadPhysicsWorldCollider {
    Entity antiquities$addEntityCollider(net.minecraft.entity.Entity except, Entity entity, Collider... colliders);
}
