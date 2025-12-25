package net.hollowed.antique.items;

import net.hollowed.antique.index.AdventureArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;

public class MyriadPauldronsItem extends Item {
    public MyriadPauldronsItem(AdventureArmorMaterial material, ArmorType type, Properties settings) {
        super(material.applySettings(settings, type));
    }
}
