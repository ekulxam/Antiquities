package net.hollowed.antique.items;

import net.hollowed.antique.Antiquities;
import net.hollowed.antique.items.custom.FurBootsItem;
import net.hollowed.antique.items.custom.NetheritePauldronsItem;
import net.hollowed.antique.items.custom.SatchelItem;
import net.hollowed.antique.items.custom.VelocityTransferMaceItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.*;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {

    public static final Item NETHERITE_PAULDRONS = registerItem("netherite_pauldrons", new NetheritePauldronsItem(ModArmorMaterials.ADVENTURE, EquipmentType.CHESTPLATE, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "netherite_pauldrons")))
            .maxCount(1).fireproof()
    ));

    public static final Item SATCHEL = registerItem("satchel", new SatchelItem(ModArmorMaterials.ADVENTURE_BASIC, EquipmentType.LEGGINGS, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "satchel")))
            .component(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT)
            .maxCount(1).fireproof()
    ));

    public static final Item FUR_BOOTS = registerItem("fur_boots", new FurBootsItem(ModArmorMaterials.ADVENTURE, EquipmentType.BOOTS, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "fur_boots")))
            .maxCount(1).fireproof()
    ));

    public static final Item WEIGHTLESS_SCEPTER = registerItem("weightless_scepter", new VelocityTransferMaceItem(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "weightless_scepter")))
            .maxCount(1).attributeModifiers(VelocityTransferMaceItem.createAttributeModifiers()).maxDamage(500).enchantable(10).rarity(Rarity.UNCOMMON)
    ));

    public static final Item COPPER_HANDLE = registerItem("copper_handle", new Item(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "copper_handle")))
            .maxCount(64)
    ));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Antiquities.MOD_ID, name), item);
    }

    public static void initialize() {
        Antiquities.LOGGER.info("Antiquities Items Initialized");
    }
}
