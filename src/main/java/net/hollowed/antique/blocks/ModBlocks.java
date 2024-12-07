package net.hollowed.antique.blocks;

import net.hollowed.antique.Antiquities;
import net.hollowed.antique.blocks.custom.PedestalBlock;
import net.minecraft.block.*;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModBlocks {

    public static final Block PEDESTAL = registerBlock("pedestal",
            new PedestalBlock(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Antiquities.MOD_ID, "pedestal")))
                    .sounds(BlockSoundGroup.LODESTONE).strength(1.5F, 6F).nonOpaque().requiresTool()), Rarity.COMMON);

    public static final Block HOLLOW_CORE = registerBlock("hollow_core",
            new HeavyCoreBlock(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Antiquities.MOD_ID, "hollow_core")))
                    .sounds(ModBlockSoundGroup.HOLLOW_CORE).strength(10F, 1200F)
                    .pistonBehavior(PistonBehavior.NORMAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).mapColor(MapColor.IRON_GRAY)
                    .nonOpaque()), Rarity.UNCOMMON);

    private static Block registerBlock(String name, Block block, Rarity rarity) {
        registerBlockItem(name, block, rarity);
        return Registry.register(Registries.BLOCK, Identifier.of(Antiquities.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block, Rarity rarity) {
        Registry.register(Registries.ITEM, Identifier.of(Antiquities.MOD_ID, name),
                new BlockItem(block, new Item.Settings()
                        .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, name))).useBlockPrefixedTranslationKey().rarity(rarity)));
    }

    public static void initialize() {
        Antiquities.LOGGER.info("Antiquities Blocks Initialized");
    }
}
