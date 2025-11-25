package net.hollowed.antique.client.renderer.experimental_cloth;

import dev.dominion.ecs.api.Dominion;
import net.dustley.lemon.modules.citrus_physics.PhysicsWorld;
import net.dustley.lemon.modules.citrus_physics.component.ActorComponent;
import net.dustley.lemon.modules.citrus_physics.component.collision.containers.ColliderContainerComponent;
import net.dustley.lemon.modules.citrus_physics.solver.Solver;
import org.spongepowered.asm.mixin.Unique;

public class MyriadCollisionSolver extends Solver {

    PhysicsWorld world;
    Dominion ecsWorld;

    public MyriadCollisionSolver(PhysicsWorld world, Dominion ecs) {
        super(world, ecs);

        this.world = world;
        this.ecsWorld = ecs;
    }

    public void solve(double deltaTime) {
        for (int i = 0; i < PhysicsWorld.COLLISION_RESOLUTION; i++) {
            ecsWorld.findEntitiesWith(ActorComponent.class, MyriadEntityColliderContainer.class).stream().forEach(result -> runForType(result.comp1(), result.comp2(), deltaTime));
        }
    }

    @Unique
    public void runForType(ActorComponent actorComponent, ColliderContainerComponent colliderComponent, double deltaTime) {
        colliderComponent.solve(world, actorComponent, deltaTime / PhysicsWorld.COLLISION_RESOLUTION);
    }
}
