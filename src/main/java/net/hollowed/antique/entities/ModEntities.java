package net.hollowed.antique.entities;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.entities.custom.MyriadShovelEntity;
import net.hollowed.antique.entities.custom.PaleWardenEntity;
import net.hollowed.antique.entities.parts.MyriadShovelPart;
import net.minecraft.entity.Entity;
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

    public static final EntityType<MyriadShovelEntity> MYRIAD_SHOVEL = register(
            "myriad_shovel",
            EntityType.Builder.<MyriadShovelEntity>create(MyriadShovelEntity::new, SpawnGroup.MISC)
                    .dropsNothing()
                    .dimensions(1.2F, 0.75F)
    );

    public static final EntityType<MyriadShovelPart> MYRIAD_SHOVEL_PART = register(
            "myriad_shovel_part",
            EntityType.Builder.create(MyriadShovelPart::new, SpawnGroup.MISC)
                    .dropsNothing()
                    .dimensions(0.4F, 0.4F)
    );

    private static <T extends Entity> EntityType<T> register(RegistryKey<EntityType<?>> key, EntityType.Builder<T> type) {
        return Registry.register(Registries.ENTITY_TYPE, key, type.build(key));
    }

    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
        return register(keyOf(id), type);
    }

    private static RegistryKey<EntityType<?>> keyOf(String id) {
        return RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Antiquities.MOD_ID, id));
    }

    public static void initialize() {
        FabricDefaultAttributeRegistry.register(ModEntities.PALE_WARDEN, PaleWardenEntity.createAttributes());
    }
}