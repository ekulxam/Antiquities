package net.hollowed.antique.client.renderer.experimental_cloth;

import dev.dominion.ecs.api.Entity;
import net.dustley.lemon.modules.citrus_physics.PhysicsWorld;

public class LifeTimeComponent {

    private final PhysicsWorld physicsWorld;
    public int time;

    public LifeTimeComponent(PhysicsWorld physicsWorld, int time) {
        this.physicsWorld = physicsWorld;
        this.time = time;
    }

    public void tick(Entity entity) {
        if (time <= 0) {
            if (entity != null) physicsWorld.ecsWorld.deleteEntity(entity);
        } else {
            time--;
        }
    }
}
