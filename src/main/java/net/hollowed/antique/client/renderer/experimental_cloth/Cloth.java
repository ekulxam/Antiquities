package net.hollowed.antique.client.renderer.experimental_cloth;

import dev.dominion.ecs.api.Entity;
import net.dustley.lemon.modules.citrus_physics.PhysicsWorld;
import net.dustley.lemon.modules.citrus_physics.component.ActorComponent;
import net.dustley.lemon.modules.citrus_physics.component.collision.colliders.Collider;
import net.dustley.lemon.modules.citrus_physics.component.collision.colliders.SphereCollider;
import net.dustley.lemon.modules.citrus_physics.component.constraint.single.GravityConstraint;
import net.dustley.lemon.modules.citrus_physics.component.constraint.single.StaticConstraint;
import net.hollowed.antique.util.interfaces.duck.MyriadPhysicsWorldCollider;
import org.joml.Vector3d;

import java.util.HashMap;
import java.util.Map;

public class Cloth {

    private static final int DEFAULT_SEGMENT_COUNT = 15;
    private static final double DEFAULT_LENGTH = 1.4 / DEFAULT_SEGMENT_COUNT; // Distance between segments so [total length / segment count] (thanks dust)
    private static final double DEFAULT_WIDTH = 0.1;
    private static final double SEGMENT_MASS = 0.5;
    private static final double GRAVITY = 1;
    private static final double AIR_RESISTANCE = 0.95;
    private static final double VERTICAL_AIR_RESISTANCE = 0.1;

    private final PhysicsWorld physicsWorld;
    private final Entity[] segments;
    private final double length;
    private final double width;
    private final net.minecraft.entity.Entity entity;

    private Vector3d prevPos;

    private StaticConstraint rootPosConstraint;
    @SuppressWarnings("all")
    private final Map<Entity, AirResistanceConstraint> airResistanceConstraints = new HashMap<>();
    @SuppressWarnings("all")
    private final Map<Entity, GravityConstraint> gravityConstraints = new HashMap<>();

    public Cloth(net.minecraft.entity.Entity entity, PhysicsWorld physicsWorld) {
        this.physicsWorld = physicsWorld;
        this.segments = new Entity[DEFAULT_SEGMENT_COUNT];
        this.length = DEFAULT_LENGTH;
        this.width = DEFAULT_WIDTH;
        this.entity = entity;

        this.createCloth();
    }

    public Cloth(net.minecraft.entity.Entity entity, PhysicsWorld physicsWorld, int segmentCount, double length, double width) {
        this.physicsWorld = physicsWorld;
        this.segments = new Entity[segmentCount];
        this.length = length / segmentCount;
        this.width = width;
        this.entity = entity;

        this.createCloth();
    }

    private void createCloth() {
        for (Entity segment : segments) {
            if(segment != null) physicsWorld.ecsWorld.deleteEntity(segment);
        }

        for (int i = 0; i < segments.length; i++) {
            LifeTimeComponent lifeTimeConstraint = new LifeTimeComponent(physicsWorld, 20);

            var actor = new ActorComponent(new Vector3d(), SEGMENT_MASS);
            segments[i] = physicsWorld.createEntity().add(actor).add(lifeTimeConstraint);

            GravityConstraint gravityConstraint = new GravityConstraint(new Vector3d(0.0, -GRAVITY, 0.0));
            gravityConstraints.put(segments[i], gravityConstraint);
            physicsWorld.addConstraint(segments[i], gravityConstraint);

            double radius = 0.25;
            Collider collider = new SphereCollider(radius);

            //physicsWorld.addWorldCollider(segments[i], collider);

            if (this.physicsWorld instanceof MyriadPhysicsWorldCollider worldCollider) {
                //worldCollider.antiquities$addEntityCollider(this.entity, segments[i], collider);
            }
        }

        for (int i = 0; i < segments.length - 1; i++) {
            var segmentA = segments[i];
            var segmentB = segments[i+1];

            physicsWorld.addConstraint(segmentA, new SafeFixedDistanceConstraint(segmentB, length));
        }

        rootPosConstraint = new StaticConstraint(new Vector3d());
        physicsWorld.addConstraint(segments[0], rootPosConstraint);

        for (Entity segment : segments) {
            AirResistanceConstraint airResistanceConstraint = new AirResistanceConstraint(new Vector3d(AIR_RESISTANCE, VERTICAL_AIR_RESISTANCE, AIR_RESISTANCE));
            airResistanceConstraints.put(segment, airResistanceConstraint);
            physicsWorld.addConstraint(segment, airResistanceConstraint);
        }
    }

    public void update(Vector3d rootPos, double deltaTime) {
        rootPosConstraint.position = prevPos != null ? rootPos.lerp(prevPos, deltaTime) : rootPos;
        prevPos = rootPos;

        for (Entity segment : segments) {
            if (segment.get(LifeTimeComponent.class) != null) segment.get(LifeTimeComponent.class).time = 20;
        }
    }

    public Entity getSegment(int id) {
        return this.segments[id];
    }

    public int getSegmentCount() {
        return this.segments.length;
    }

    public double getLength() {
        return length;
    }

    public double getWidth() {
        return width;
    }
}
