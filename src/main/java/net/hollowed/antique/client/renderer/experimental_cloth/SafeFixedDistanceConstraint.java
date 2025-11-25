package net.hollowed.antique.client.renderer.experimental_cloth;

import dev.dominion.ecs.api.Entity;
import net.dustley.lemon.modules.citrus_physics.component.ActorComponent;
import net.dustley.lemon.modules.citrus_physics.component.constraint.DoubleBodyConstraint;
import org.joml.Vector3d;

public class SafeFixedDistanceConstraint extends DoubleBodyConstraint {

    public double distance;

    public SafeFixedDistanceConstraint(Entity other, double distance) {
        super(other);
        this.distance = distance;
    }

    public void solve(ActorComponent actor, double deltaTime) {
        var otherActor = other.get(ActorComponent.class);

        if(actor != null && otherActor != null && actor.position.distance(otherActor.position) > distance) {
            Vector3d axis = actor.position.sub(otherActor.position, new Vector3d());
            double dist = axis.length();
            Vector3d norm = axis.div(dist, new Vector3d());
            double delta = distance - dist;
            actor.position.add(norm.mul(0.5 * delta, new Vector3d()));
            otherActor.position.sub(norm.mul(0.5 * delta, new Vector3d()));
        }
    }

}