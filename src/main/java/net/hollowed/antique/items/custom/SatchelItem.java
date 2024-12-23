package net.hollowed.antique.items.custom;

import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentType;

public class SatchelItem extends BundleItem {

    public SatchelItem(ArmorMaterial material, EquipmentType type, Item.Settings settings) {
        super(material.applySettings(settings, type));
    }
}