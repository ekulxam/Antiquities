package net.hollowed.antique.items.custom;

import net.hollowed.antique.items.AttributeArmorMaterial;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.util.Identifier;

public class FurBootsItem extends Item {
    public FurBootsItem(AttributeArmorMaterial material, EquipmentType type, Item.Settings settings) {
        super(material.applySettings(settings, type, createAttributeModifiers()));
    }

    public static AttributeModifiersComponent createAttributeModifiers() {
        return AttributeModifiersComponent.builder()
                .add(EntityAttributes.MOVEMENT_SPEED, new EntityAttributeModifier(Identifier.ofVanilla("movement_speed"), 0.2, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE), AttributeModifierSlot.FEET)
                .add(EntityAttributes.ARMOR, new EntityAttributeModifier(Identifier.ofVanilla("armor"), 3.0, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.FEET)
                .add(EntityAttributes.STEP_HEIGHT, new EntityAttributeModifier(Identifier.ofVanilla("step_height"), 1.0, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.FEET)
                .add(EntityAttributes.ARMOR_TOUGHNESS, new EntityAttributeModifier(Identifier.ofVanilla("armor_toughness"), 1.0, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.FEET)
                .build();
    }
}
