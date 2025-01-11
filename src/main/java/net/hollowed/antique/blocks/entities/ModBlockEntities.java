package net.hollowed.antique.blocks.entities;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.blocks.ModBlocks;
import net.hollowed.antique.blocks.entities.custom.PedestalBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<PedestalBlockEntity> PEDESTAL_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(Antiquities.MOD_ID, "pedestal"),
                    FabricBlockEntityTypeBuilder.create(PedestalBlockEntity::new,
                            ModBlocks.PEDESTAL, ModBlocks.OMINOUS_PEDESTAL).build());

    public static void initialize() {
        Antiquities.LOGGER.info("Antiquities Block Entities Initialized");
    }
}
