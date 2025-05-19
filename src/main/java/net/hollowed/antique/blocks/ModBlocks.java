package net.hollowed.antique.blocks;

import net.hollowed.antique.Antiquities;
import net.hollowed.antique.blocks.custom.JarBlock;
import net.hollowed.antique.blocks.custom.OminousPedestalBlock;
import net.hollowed.antique.blocks.custom.PedestalBlock;
import net.minecraft.block.*;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ColorCode;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.function.Function;

public class ModBlocks {

    public static final Block PEDESTAL = registerBlock("pedestal",
            new PedestalBlock(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Antiquities.MOD_ID, "pedestal")))
                    .sounds(BlockSoundGroup.LODESTONE).strength(1.5F, 6F).nonOpaque().requiresTool()), Rarity.COMMON);

    public static final Block JAR = registerBlock("jar",
            new JarBlock(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Antiquities.MOD_ID, "jar")))
                    .sounds(BlockSoundGroup.DECORATED_POT).strength(0.3F).nonOpaque()), Rarity.COMMON);

    public static final Block OMINOUS_PEDESTAL = registerBlock("ominous_pedestal",
            new OminousPedestalBlock(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Antiquities.MOD_ID, "ominous_pedestal")))
                    .sounds(BlockSoundGroup.LODESTONE).strength(1.5F, 6F).nonOpaque().requiresTool()), Rarity.COMMON);

    public static final Block BLACK_SAND = registerBlock("black_sand",
            new ColoredFallingBlock(new ColorCode(14406560), AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Antiquities.MOD_ID, "black_sand")))
                    .sounds(BlockSoundGroup.SAND).strength(1.5F, 6F)), Rarity.COMMON);

    public static final Block IVY = registerBlock(
            "ivy",
            new GlowLichenBlock(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Antiquities.MOD_ID, "ivy")))
                    .mapColor(MapColor.GREEN)
                    .replaceable()
                    .noCollision()
                    .strength(0.2F)
                    .sounds(BlockSoundGroup.CAVE_VINES)
                    .burnable()
                    .pistonBehavior(PistonBehavior.DESTROY)),
            Rarity.COMMON
    );

    public static final Block GILDED_BLACK_SAND = registerBlock("gilded_black_sand",
            new ColoredFallingBlock(new ColorCode(14406560), AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Antiquities.MOD_ID, "gilded_black_sand")))
                    .sounds(BlockSoundGroup.SAND).strength(1.5F, 6F)), Rarity.COMMON);

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

    private static Block register(String id, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        return register(keyOf(id), factory, settings);
    }

    public static Block register(RegistryKey<Block> key, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        Block block = factory.apply(settings.registryKey(key));
        return Registry.register(Registries.BLOCK, key, block);
    }

    public static Block register(RegistryKey<Block> key, AbstractBlock.Settings settings) {
        return register(key, Block::new, settings);
    }

    private static RegistryKey<Block> keyOf(String id) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Antiquities.MOD_ID, id));
    }

    private static AbstractBlock.Settings copyLootTable(Block block, boolean copyTranslationKey) {
        AbstractBlock.Settings settings = block.getSettings();
        AbstractBlock.Settings settings2 = AbstractBlock.Settings.create().lootTable(block.getLootTableKey());
        if (copyTranslationKey) {
            settings2 = settings2.overrideTranslationKey(block.getTranslationKey());
        }

        return settings2;
    }

    public static void initialize() {
        Antiquities.LOGGER.info("Antiquities Blocks Initialized");
    }
}
