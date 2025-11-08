package net.hollowed.antique.index;

import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.ArmorMaterials;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;

import java.util.EnumMap;

@SuppressWarnings("all")
public interface AntiqueArmorMaterials extends ArmorMaterials {
    AdventureArmorMaterial ADVENTURE = new AdventureArmorMaterial(2000, Util.make(new EnumMap(EquipmentType.class), (map) -> {
        map.put(EquipmentType.BOOTS, 3);
        map.put(EquipmentType.LEGGINGS, 2);
        map.put(EquipmentType.CHESTPLATE, 8);
        map.put(EquipmentType.HELMET, 0);
        map.put(EquipmentType.BODY, 0);
    }), 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.0F, 0.025F, ItemTags.REPAIRS_LEATHER_ARMOR, AntiqueEquipmentAssetKeys.EMPTY, 0.5F, 1.0F, 0.2F);
    ArmorMaterial ADVENTURE_BASIC = new ArmorMaterial(5, Util.make(new EnumMap(EquipmentType.class), (map) -> {
        map.put(EquipmentType.BOOTS, 3);
        map.put(EquipmentType.LEGGINGS, 2);
        map.put(EquipmentType.CHESTPLATE, 8);
        map.put(EquipmentType.HELMET, 0);
        map.put(EquipmentType.BODY, 8);
    }), 15, RegistryEntry.of(SoundEvents.INTENTIONALLY_EMPTY), 1.0F, 0.025F, ItemTags.REPAIRS_LEATHER_ARMOR, AntiqueEquipmentAssetKeys.EMPTY);
}
