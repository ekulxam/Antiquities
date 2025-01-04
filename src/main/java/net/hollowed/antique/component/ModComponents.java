// Updated ModComponents.java
package net.hollowed.antique.component;

import net.hollowed.antique.Antiquities;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.List;

public class ModComponents {
    public static final ComponentType<List<ItemStack>> SATCHEL_STACK = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(Antiquities.MOD_ID, "quiver_stacks"),
            ComponentType.<List<ItemStack>>builder()
                    .codec(ItemStack.CODEC.listOf().fieldOf("quiver_stacks").codec())
                    .build()
    );

    public static void initialize() {}
}
