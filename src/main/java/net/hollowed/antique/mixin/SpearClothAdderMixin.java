package net.hollowed.antique.mixin;

import net.hollowed.antique.client.item.explosive_spear.ClothManager;
import net.hollowed.antique.util.SpearClothAccess;
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
public abstract class SpearClothAdderMixin extends Entity implements SpearClothAccess {

    @Unique
    ClothManager clothManager;
    @Unique
    ClothManager clothManager1;
    @Unique
    ClothManager clothManager2;

    public SpearClothAdderMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void addCloth(EntityType entityType, World world, CallbackInfo ci) {
        this.clothManager = new ClothManager(new Vector3d(this.getX(), this.getY(), this.getZ()), 4);
        this.clothManager1 = new ClothManager(new Vector3d(this.getX(), this.getY(), this.getZ()), 4);
        this.clothManager2 = new ClothManager(new Vector3d(this.getX(), this.getY(), this.getZ()), 4);
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
}
