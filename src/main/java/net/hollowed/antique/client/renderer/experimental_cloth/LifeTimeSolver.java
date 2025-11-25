package net.hollowed.antique.client.renderer.experimental_cloth;

import dev.dominion.ecs.api.Dominion;
import net.dustley.lemon.modules.citrus_physics.PhysicsWorld;
import net.dustley.lemon.modules.citrus_physics.solver.Solver;

public class LifeTimeSolver extends Solver {

    private final Dominion ecs;

    public LifeTimeSolver(PhysicsWorld world, Dominion ecs) {
        super(world, ecs);
        this.ecs = ecs;
    }

    @Override
    public void solve(double deltaTime) {
        ecs.findEntitiesWith(LifeTimeComponent.class).stream().forEach(result -> result.comp().tick(result.entity()));
    }
}
