package net.hollowed.antique.items;

import net.hollowed.antique.index.AdventureArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.EquipmentType;

public class NetheritePauldronsItem extends Item {
    public NetheritePauldronsItem(AdventureArmorMaterial material, EquipmentType type, Settings settings) {
        super(material.applySettings(settings, type));
    }
}
