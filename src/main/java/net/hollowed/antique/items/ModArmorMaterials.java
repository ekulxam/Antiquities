package net.hollowed.antique.items;

import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.ArmorMaterials;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;

import java.util.EnumMap;

public interface ModArmorMaterials extends ArmorMaterials {
    AttributeArmorMaterial ADVENTURE = new AttributeArmorMaterial(5, Util.make(new EnumMap(EquipmentType.class), (map) -> {
        map.put(EquipmentType.BOOTS, 1);
        map.put(EquipmentType.LEGGINGS, 2);
        map.put(EquipmentType.CHESTPLATE, 3);
        map.put(EquipmentType.HELMET, 1);
        map.put(EquipmentType.BODY, 3);
    }), 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, ItemTags.REPAIRS_LEATHER_ARMOR, ModEquipmentAssetKeys.EMPTY);
    ArmorMaterial ADVENTURE_BASIC = new ArmorMaterial(5, Util.make(new EnumMap(EquipmentType.class), (map) -> {
        map.put(EquipmentType.BOOTS, 1);
        map.put(EquipmentType.LEGGINGS, 2);
        map.put(EquipmentType.CHESTPLATE, 3);
        map.put(EquipmentType.HELMET, 1);
        map.put(EquipmentType.BODY, 3);
    }), 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, ItemTags.REPAIRS_LEATHER_ARMOR, ModEquipmentAssetKeys.EMPTY);
}
