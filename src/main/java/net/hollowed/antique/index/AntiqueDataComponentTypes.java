package net.hollowed.antique.index;

import com.mojang.serialization.Codec;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.items.components.MyriadToolComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.List;

public interface AntiqueDataComponentTypes {
    ComponentType<List<ItemStack>> SATCHEL_STACK = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(Antiquities.MOD_ID, "satchel_stacks"),
            ComponentType.<List<ItemStack>>builder()
                    .codec(ItemStack.CODEC.listOf().fieldOf("satchel_stacks").codec())
                    .build()
    );
    ComponentType<MyriadToolComponent> MYRIAD_TOOL = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(Antiquities.MOD_ID, "myriad_tool"),
            ComponentType.<MyriadToolComponent>builder()
                    .codec(MyriadToolComponent.CODEC)
                    .build()
    );
    ComponentType<ItemStack> MYRIAD_STACK = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(Antiquities.MOD_ID, "myriad_stack"),
            ComponentType.<ItemStack>builder()
                    .codec(ItemStack.CODEC.fieldOf("myriad_stack").codec())
                    .build()
    );
    ComponentType<Integer> COUNTER = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(Antiquities.MOD_ID, "counter"),
            ComponentType.<Integer>builder()
                    .codec(Codec.INT.fieldOf("counter").codec())
                    .build()
    );
    ComponentType<String> CLOTH_TYPE = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(Antiquities.MOD_ID, "cloth_type"),
            ComponentType.<String>builder()
                    .codec(Codec.STRING.fieldOf("cloth_type").codec())
                    .build()
    );
    ComponentType<String> CLOTH_PATTERN = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(Antiquities.MOD_ID, "cloth_pattern"),
            ComponentType.<String>builder()
                    .codec(Codec.STRING.fieldOf("cloth_pattern").codec())
                    .build()
    );
    ComponentType<DyedColorComponent> SECONDARY_DYED_COLOR = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(Antiquities.MOD_ID, "secondary_color"),
            ComponentType.<DyedColorComponent>builder()
                    .codec(DyedColorComponent.CODEC)
                    .build()
    );
    ComponentType<Boolean> STICKY_TOOLTIP = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(Antiquities.MOD_ID, "sticky_tooltip"),
            ComponentType.<Boolean>builder()
                    .codec(Codec.BOOL.fieldOf("sticky_tooltip").codec())
                    .build()
    );

    static void initialize() {}
}
