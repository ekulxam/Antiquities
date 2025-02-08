package net.hollowed.antique.items;

import net.hollowed.antique.Antiquities;
import net.hollowed.antique.component.ModComponents;
import net.hollowed.antique.entities.ModEntities;
import net.hollowed.antique.items.custom.*;
import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.item.*;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.List;

public class ModItems {

    public static RegistryEntryLookup<Block> registryEntryLookup = Registries.createEntryLookup(Registries.BLOCK);

    public static final Item EXPLOSIVE_SPEAR = registerItem("explosive_spear", new ExplosiveSpearItem(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "explosive_spear")))
            .maxCount(1)
    ));

    public static final Item NETHERITE_PAULDRONS = registerItem("netherite_pauldrons", new NetheritePauldronsItem(ModArmorMaterials.ADVENTURE, EquipmentType.CHESTPLATE, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "netherite_pauldrons")))
            .maxCount(1).fireproof()
    ));

    public static final Item SATCHEL = registerItem("satchel", new SatchelItem(
            ModArmorMaterials.ADVENTURE_BASIC,
            EquipmentType.LEGGINGS,
            new Item.Settings()
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "satchel")))
                    .component(ModComponents.SATCHEL_STACK, List.of())
                    .maxCount(1)
                    .fireproof()
    ));

    public static final Item FUR_BOOTS = registerItem("fur_boots", new FurBootsItem(ModArmorMaterials.ADVENTURE, EquipmentType.BOOTS, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "fur_boots")))
            .maxCount(1).fireproof()
    ));

    public static final Item WEIGHTLESS_SCEPTER = registerItem("weightless_scepter", new VelocityTransferMaceItem(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "weightless_scepter")))
            .maxCount(1).attributeModifiers(VelocityTransferMaceItem.createAttributeModifiers()).maxDamage(500).enchantable(10).rarity(Rarity.UNCOMMON)
    ));

    public static final Item PALE_WARDENS_GREATSWORD = registerItem("pale_wardens_greatsword", new SwordItem(ToolMaterial.NETHERITE, 1.0F, -2.4F, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "pale_wardens_greatsword")))
            .maxCount(1).attributeModifiers(VelocityTransferMaceItem.createAttributeModifiers()).maxDamage(2031).enchantable(10).rarity(Rarity.RARE)
    ));

    public static final Item REVERENCE = registerItem("reverence", new ReverenceItem(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "reverence")))
            .maxCount(1).attributeModifiers(ReverenceItem.createAttributeModifiers()).maxDamage(2031).enchantable(10).rarity(Rarity.EPIC)
            .fireproof()
    ));

    public static final Item IRREVERENT = registerItem("irreverence", new DeathItem(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "irreverence")))
            .maxCount(1).attributeModifiers(DeathItem.createAttributeModifiers()).maxDamage(2031).enchantable(10).rarity(Rarity.EPIC)
            .fireproof()
    ));

    public static final Item MYRIAD_TOOL = registerItem("myriad_tool", new MyriadToolItem(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "myriad_tool")))
            .maxCount(1).attributeModifiers(MyriadToolItem.createAttributeModifiers()).enchantable(10).rarity(Rarity.UNCOMMON).fireproof()
            .component(ModComponents.MYRIAD_STACK, ItemStack.EMPTY)
            .component(ModComponents.INTEGER_PROPERTY, 0)
            .component(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(true))
    ));

    public static final Item MYRIAD_PICK_HEAD = registerItem("myriad_mattock_head", new MyriadToolBitItem(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "myriad_mattock_head")))
            .maxCount(1).attributeModifiers(MyriadToolItem.createAttributeModifiers()).enchantable(10).rarity(Rarity.UNCOMMON)
            .fireproof().component(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(true)).component(DataComponentTypes.TOOL, ShearsItem.createToolComponent()), 1
    ));

    public static final Item MYRIAD_AXE_HEAD = registerItem("myriad_axe_head", new MyriadToolBitItem(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "myriad_axe_head")))
            .maxCount(1).attributeModifiers(MyriadToolItem.createAttributeModifiers()).enchantable(10).rarity(Rarity.UNCOMMON)
            .fireproof().component(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(true)), 2
    ));

    public static final Item MYRIAD_SHOVEL_HEAD = registerItem("myriad_shovel_head", new MyriadToolBitItem(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "myriad_shovel_head")))
            .maxCount(1).attributeModifiers(MyriadToolItem.createAttributeModifiers()).enchantable(10).rarity(Rarity.UNCOMMON)
            .fireproof().component(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(true)), 3
    ));

    public static final Item DORMANT_REVERENCE = registerItem("dormant_reverence", new ReverenceItem(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "dormant_reverence")))
            .maxCount(1).attributeModifiers(ReverenceItem.createAttributeModifiers()).maxDamage(2031).enchantable(10).rarity(Rarity.EPIC)
    ));

    public static final Item COPPER_HANDLE = registerItem("copper_handle", new Item(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "copper_handle")))
            .maxCount(64)
    ));

    public static final Item PALE_WARDEN_STATUE = registerItem("pale_warden_statue", new PaleWardenSpawnEggItem(ModEntities.PALE_WARDEN, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "pale_warden_statue")))
            .maxCount(64)
    ));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Antiquities.MOD_ID, name), item);
    }

    public static void initialize() {
        Antiquities.LOGGER.info("Antiquities Items Initialized");
    }
}
