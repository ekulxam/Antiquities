package net.hollowed.antique.entities;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.entities.custom.PaleWardenEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<PaleWardenEntity> PALE_WARDEN = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(Antiquities.MOD_ID, "pale_warden"),
            EntityType.Builder.create(PaleWardenEntity::new, SpawnGroup.MISC).dimensions(0.9f, 3f).build(keyOf("pale_warden"))
    );

    private static RegistryKey<EntityType<?>> keyOf(String id) {
        return RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Antiquities.MOD_ID, id));
    }

    public static void initialize() {
        FabricDefaultAttributeRegistry.register(ModEntities.PALE_WARDEN, PaleWardenEntity.createAttributes());
    }
}