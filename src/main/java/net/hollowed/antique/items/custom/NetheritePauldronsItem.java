package net.hollowed.antique.items.custom;

import net.hollowed.antique.items.AdventureArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.EquipmentType;

public class NetheritePauldronsItem extends Item {
    public NetheritePauldronsItem(AdventureArmorMaterial material, EquipmentType type, Settings settings) {
        super(material.applySettings(settings, type));
    }
}
