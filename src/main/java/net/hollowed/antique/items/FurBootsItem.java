package net.hollowed.antique.items;

import net.hollowed.antique.index.AdventureArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;

public class FurBootsItem extends Item {
    public FurBootsItem(AdventureArmorMaterial material, ArmorType type, Item.Properties settings) {
        super(material.applySettings(settings, type));
    }
}
