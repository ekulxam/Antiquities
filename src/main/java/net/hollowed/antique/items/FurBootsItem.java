package net.hollowed.antique.items;

import net.hollowed.antique.index.AdventureArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.EquipmentType;

public class FurBootsItem extends Item {
    public FurBootsItem(AdventureArmorMaterial material, EquipmentType type, Item.Settings settings) {
        super(material.applySettings(settings, type));
    }
}
