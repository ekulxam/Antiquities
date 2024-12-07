package net.hollowed.antique.mixin;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.MinecartItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.EntityType;

@Mixin(MinecartItem.class)
public interface MinecartItemAccessor {
    @Accessor("type")
    EntityType<? extends AbstractMinecartEntity> getType();
}
