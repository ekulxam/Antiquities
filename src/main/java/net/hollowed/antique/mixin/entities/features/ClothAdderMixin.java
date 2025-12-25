package net.hollowed.antique.mixin.entities.features;

import net.hollowed.antique.client.renderer.cloth.ClothManager;
import net.hollowed.antique.util.interfaces.duck.ClothAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.Map;

@Mixin(Entity.class)
public abstract class ClothAdderMixin implements ClothAccess {

    @Unique
    private Map<Identifier, ClothManager> map = new HashMap<>();

    @Override
    public Map<Identifier, ClothManager> antique$getManagers() {
        return this.map;
    }
}
