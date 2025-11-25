package net.hollowed.antique.mixin.physics;

import dev.dominion.ecs.api.Dominion;
import dev.dominion.ecs.api.Entity;
import net.dustley.lemon.modules.citrus_physics.PhysicsWorld;
import net.dustley.lemon.modules.citrus_physics.component.collision.colliders.Collider;
import net.dustley.lemon.modules.citrus_physics.solver.Solver;
import net.hollowed.antique.client.renderer.experimental_cloth.LifeTimeSolver;
import net.hollowed.antique.client.renderer.experimental_cloth.MyriadCollisionSolver;
import net.hollowed.antique.client.renderer.experimental_cloth.MyriadEntityColliderContainer;
import net.hollowed.antique.util.interfaces.duck.MyriadPhysicsWorldCollider;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(PhysicsWorld.class)
public class PhysicsWorldMixin implements MyriadPhysicsWorldCollider {

    @Shadow public Dominion ecsWorld;

    @Shadow
    ArrayList<Solver> solvers;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ldev/dominion/ecs/api/Dominion;createScheduler()Ldev/dominion/ecs/api/Scheduler;"))
    private void init(World gameWorld, CallbackInfo ci) {
        PhysicsWorld world = (PhysicsWorld) (Object) this;

        this.solvers.add(new LifeTimeSolver(world, this.ecsWorld));
        this.solvers.add(new MyriadCollisionSolver(world, this.ecsWorld));
    }

    @Override
    public Entity antiquities$addEntityCollider(net.minecraft.entity.Entity except, Entity entity, Collider... colliders) {
        var constraint = entity.get(MyriadEntityColliderContainer.class);
        if(constraint == null) {
            constraint = new MyriadEntityColliderContainer(except);
            entity.add(constraint);
        }

        constraint.shapes.addAll(List.of(colliders));

        return entity;
    }
}
