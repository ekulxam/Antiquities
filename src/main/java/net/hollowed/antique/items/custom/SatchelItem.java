package net.hollowed.antique.items.custom;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentType;

import java.util.ArrayList;
import java.util.List;

public class SatchelItem extends BundleItem {

    public SatchelItem(ArmorMaterial material, EquipmentType type, Item.Settings settings) {
        super(material.applySettings(settings, type));
    }

    public List<ItemStack> getItems(ItemStack satchelStack) {
        BundleContentsComponent bundleContentsComponent = satchelStack.getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
        List<ItemStack> items = new ArrayList<>();

        for (int i = 0; i < bundleContentsComponent.size(); i++) {
            items.add(bundleContentsComponent.get(i));
        }

        return items;
    }
}
