package net.hollowed.antique.index;

import net.hollowed.antique.Antiquities;
import net.hollowed.antique.blocks.*;
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

import java.util.function.Function;

public interface AntiqueBlocks {

    Block PEDESTAL = register("pedestal", settings -> new PedestalBlock(settings
            .strength(1.5F, 6F)
            .sounds(BlockSoundGroup.LODESTONE)
            .requiresTool()
            .nonOpaque()
            ), Rarity.COMMON
    );
    Block JAR = register("jar", settings -> new JarBlock(settings
            .sounds(BlockSoundGroup.DECORATED_POT)
            .strength(0.3F)
            .nonOpaque()
            ), Rarity.COMMON
    );
    Block DYE_TABLE = register("dye_table", settings -> new DyeTableBlock(settings
            .sounds(BlockSoundGroup.WOOD)
            .strength(2.5F)
            ), Rarity.COMMON
    );
    Block MYRIAD_CLUSTER = register("myriad_cluster", settings -> new AmethystClusterBlock(8, 8, settings
            .strength(15F, 600F)
            .sounds(BlockSoundGroup.IRON)
            .requiresTool()
            ), Rarity.COMMON
    );
    Block MYRIAD_ORE = register("myriad_ore", settings -> new Block(settings
            .strength(25F, 600F)
            .sounds(BlockSoundGroup.IRON)
            .requiresTool()
            ), Rarity.COMMON
    );
    Block DEEPSLATE_MYRIAD_CLUSTER = register("deepslate_myriad_cluster", settings -> new AmethystClusterBlock(8, 8, settings
            .strength(20F, 600F)
            .sounds(BlockSoundGroup.IRON)
            .requiresTool()
            ), Rarity.COMMON
    );
    Block DEEPSLATE_MYRIAD_ORE = register("deepslate_myriad_ore", settings -> new Block(settings
            .strength(30F, 600F)
            .sounds(BlockSoundGroup.IRON)
            .requiresTool()
            ), Rarity.COMMON
    );
    Block RAW_MYRIAD_BLOCK = register("raw_myriad_block", settings -> new Block(settings
            .strength(30F, 600F)
            .sounds(BlockSoundGroup.IRON)
            .requiresTool()
            ), Rarity.COMMON
    );
    Block MYRIAD_BLOCK = register("myriad_block", settings -> new TarnishingBlock(settings
            .strength(30F, 600F)
            .sounds(BlockSoundGroup.IRON)
            .requiresTool()
            ), Rarity.COMMON
    );
    Block EXPOSED_MYRIAD_BLOCK = register("exposed_myriad_block", settings -> new TarnishingBlock(settings
            .strength(30F, 600F)
            .sounds(BlockSoundGroup.IRON)
            .requiresTool()
            ), Rarity.COMMON
    );
    Block WEATHERED_MYRIAD_BLOCK = register("weathered_myriad_block", settings -> new TarnishingBlock(settings
            .strength(30F, 600F)
            .sounds(BlockSoundGroup.IRON)
            .requiresTool()
            ), Rarity.COMMON
    );
    Block TARNISHED_MYRIAD_BLOCK = register("tarnished_myriad_block", settings -> new TarnishingBlock(settings
            .strength(30F, 600F)
            .sounds(BlockSoundGroup.IRON)
            .requiresTool()
            ), Rarity.COMMON
    );
    Block COATED_MYRIAD_BLOCK = register("coated_myriad_block", settings -> new Block(settings
            .strength(30F, 600F)
            .sounds(BlockSoundGroup.IRON)
            .requiresTool()
            ), Rarity.COMMON
    );
    Block COATED_EXPOSED_MYRIAD_BLOCK = register("coated_exposed_myriad_block", settings -> new Block(settings
            .strength(30F, 600F)
            .sounds(BlockSoundGroup.IRON)
            .requiresTool()
            ), Rarity.COMMON
    );
    Block COATED_WEATHERED_MYRIAD_BLOCK = register("coated_weathered_myriad_block", settings -> new Block(settings
            .strength(30F, 600F)
            .sounds(BlockSoundGroup.IRON)
            .requiresTool()
            ), Rarity.COMMON
    );
    Block COATED_TARNISHED_MYRIAD_BLOCK = register("coated_tarnished_myriad_block", settings -> new Block(settings
            .strength(30F, 600F)
            .sounds(BlockSoundGroup.IRON)
            .requiresTool()
            ), Rarity.COMMON
    );
    Block IVY = register("ivy", settings -> new GlowLichenBlock(settings
            .pistonBehavior(PistonBehavior.DESTROY)
            .sounds(BlockSoundGroup.CAVE_VINES)
            .mapColor(MapColor.GREEN)
            .strength(0.2F)
            .replaceable()
            .noCollision()
            .burnable()
            ), Rarity.COMMON
    );
    Block HOLLOW_CORE = register("hollow_core", settings -> new HeavyCoreBlock(settings
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .sounds(AntiqueBlockSoundGroup.HOLLOW_CORE)
            .strength(10F, 1200F)
            .pistonBehavior(PistonBehavior.NORMAL)
            .mapColor(MapColor.IRON_GRAY)
            .nonOpaque()
            ), Rarity.UNCOMMON
    );

    static Block register(String id, Function<AbstractBlock.Settings, Block> factory, Rarity rarity) {
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, Antiquities.id(id));
        Block block = factory.apply(AbstractBlock.Settings.create().registryKey(key));
        registerBlockItem(id, block, rarity);
        return Registry.register(Registries.BLOCK, key, block);
    }

    private static void registerBlockItem(String name, Block block, Rarity rarity) {
        Registry.register(Registries.ITEM, Identifier.of(Antiquities.MOD_ID, name),
                new BlockItem(block, new Item.Settings()
                        .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, name)))
                        .useBlockPrefixedTranslationKey()
                        .rarity(rarity)
                ));
    }

    static void initialize() {
        Antiquities.LOGGER.info("Antiquities Blocks Initialized");
    }
}
