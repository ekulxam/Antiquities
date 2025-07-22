package net.hollowed.antique.index;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.blocks.entities.PedestalBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public interface AntiqueBlockEntities {
    BlockEntityType<PedestalBlockEntity> PEDESTAL_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(Antiquities.MOD_ID, "pedestal"),
                    FabricBlockEntityTypeBuilder.create(PedestalBlockEntity::new,
                            AntiqueBlocks.PEDESTAL).build());

    static void initialize() {
        Antiquities.LOGGER.info("Antiquities Block Entities Initialized");
    }
}
