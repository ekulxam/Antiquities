package net.hollowed.antique.mixin.entities.features;

import net.hollowed.antique.client.renderer.cloth.ClothManager;
import net.hollowed.antique.util.interfaces.duck.SpearClothAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class PlayerClothAdderMixin extends Entity implements SpearClothAccess {

    @Unique
    ClothManager clothManager;
    @Unique
    ClothManager clothManager1;
    @Unique
    ClothManager clothManager2;
    @Unique
    ClothManager clothManager3;

    public PlayerClothAdderMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void addCloth(CallbackInfo ci) {
        if (this.clothManager == null || this.clothManager.bodies.isEmpty()) this.clothManager = new ClothManager(new Vector3d(this.getX(), this.getY(), this.getZ()), 8);
        if (this.clothManager1 == null || this.clothManager1.bodies.isEmpty()) this.clothManager1 = new ClothManager(new Vector3d(this.getX(), this.getY(), this.getZ()), 8);
        if (this.clothManager2 == null || this.clothManager2.bodies.isEmpty()) this.clothManager2 = new ClothManager(new Vector3d(this.getX(), this.getY(), this.getZ()), 8);
        if (this.clothManager3 == null || this.clothManager3.bodies.isEmpty()) this.clothManager3 = new ClothManager(new Vector3d(this.getX(), this.getY(), this.getZ()), 8);
    }

    @Override
    public ClothManager antique$getRightArmCloth() {
        return this.clothManager;
    }

    @Override
    public ClothManager antique$getLeftArmCloth() {
        return this.clothManager1;
    }

    @Override
    public ClothManager antique$getBackCloth() {
        return this.clothManager2;
    }

    @Override
    public ClothManager antique$getBeltCloth() {
        return this.clothManager3;
    }
}
