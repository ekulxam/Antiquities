package net.hollowed.antique.component;

import net.hollowed.antique.Antiquities;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModComponents {

    public static final ComponentType<SatchelInventoryComponent> SATCHEL_INVENTORY = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Antiquities.id("satchel_inventory"),
            new ComponentType.Builder<SatchelInventoryComponent>().codec(SatchelInventoryComponent.CODEC).packetCodec(SatchelInventoryComponent.PACKET_CODEC).cache().build()
    );
}
