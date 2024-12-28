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
            Identifier.of(Antiquities.MOD_ID, "satchel"),
            ComponentType.<List<ItemStack>>builder()
                    .codec(ItemStack.CODEC.listOf()) // List of ItemStacks
                    .build()
    );

    public static void initialize() {}
}
