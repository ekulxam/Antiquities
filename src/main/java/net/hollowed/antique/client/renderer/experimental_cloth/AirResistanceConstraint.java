package net.hollowed.antique.client.renderer.experimental_cloth;

import net.dustley.lemon.modules.citrus_physics.component.ActorComponent;
import net.dustley.lemon.modules.citrus_physics.component.constraint.SingleBodyConstraint;
import org.joml.Vector3d;

public class AirResistanceConstraint extends SingleBodyConstraint {

    public Vector3d airResistance;

    public AirResistanceConstraint(Vector3d accel) {
        airResistance = accel;
    }

    public void solve(ActorComponent actor, double deltaTime) {
        actor.acceleration.sub(new Vector3d(actor.acceleration).mul(airResistance));
    }
}
