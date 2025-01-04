package net.hollowed.antique.items.custom;

import net.hollowed.antique.items.AdventureArmorMaterial;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.util.Identifier;

public class FurBootsItem extends Item {
    public FurBootsItem(AdventureArmorMaterial material, EquipmentType type, Item.Settings settings) {
        super(material.applySettings(settings, type));
    }
}
