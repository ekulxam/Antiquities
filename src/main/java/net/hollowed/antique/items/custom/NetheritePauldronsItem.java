package net.hollowed.antique.items.custom;

import net.hollowed.antique.items.AttributeArmorMaterial;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.util.Identifier;

public class NetheritePauldronsItem extends Item {
    public NetheritePauldronsItem(AttributeArmorMaterial material, EquipmentType type, Settings settings) {
        super(material.applySettings(settings, type, createAttributeModifiers()));
    }

    public static AttributeModifiersComponent createAttributeModifiers() {
        return AttributeModifiersComponent.builder()
                .add(EntityAttributes.ARMOR, new EntityAttributeModifier(Identifier.ofVanilla("armor"), 8.0, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.CHEST)
                .add(EntityAttributes.ARMOR_TOUGHNESS, new EntityAttributeModifier(Identifier.ofVanilla("armor_toughness"), 1.0, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.CHEST)
                .add(EntityAttributes.BURNING_TIME, new EntityAttributeModifier(Identifier.ofVanilla("burn_time"), -1.0, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE), AttributeModifierSlot.CHEST)
                .build();
    }

}
