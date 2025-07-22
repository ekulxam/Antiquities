package net.hollowed.antique.index;

import net.hollowed.antique.Antiquities;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.List;

public interface AntiqueComponents {
    ComponentType<List<ItemStack>> SATCHEL_STACK = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(Antiquities.MOD_ID, "satchel_stacks"),
            ComponentType.<List<ItemStack>>builder()
                    .codec(ItemStack.CODEC.listOf().fieldOf("satchel_stacks").codec())
                    .build()
    );
    ComponentType<ItemStack> MYRIAD_STACK = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(Antiquities.MOD_ID, "myriad_stack"),
            ComponentType.<ItemStack>builder()
                    .codec(ItemStack.CODEC.fieldOf("myriad_stack").codec())
                    .build()
    );

    static void initialize() {}
}
