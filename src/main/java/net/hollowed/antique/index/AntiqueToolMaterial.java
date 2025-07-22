package net.hollowed.antique.index;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public record AntiqueToolMaterial(
	TagKey<Block> incorrectBlocksForDrops, int durability, float speed, float attackDamageBonus, int enchantmentValue, TagKey<Item> repairItems
) {

	@SuppressWarnings("unused")
	public static final AntiqueToolMaterial WOOD = new AntiqueToolMaterial(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 59, 2.0F, 0.0F, 15, ItemTags.WOODEN_TOOL_MATERIALS);
	@SuppressWarnings("unused")
	public static final AntiqueToolMaterial STONE = new AntiqueToolMaterial(BlockTags.INCORRECT_FOR_STONE_TOOL, 131, 4.0F, 1.0F, 5, ItemTags.STONE_TOOL_MATERIALS);
	public static final AntiqueToolMaterial IRON = new AntiqueToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 250, 6.0F, 2.0F, 14, ItemTags.IRON_TOOL_MATERIALS);
	public static final AntiqueToolMaterial DIAMOND = new AntiqueToolMaterial(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1561, 8.0F, 3.0F, 10, ItemTags.DIAMOND_TOOL_MATERIALS);
	public static final AntiqueToolMaterial GOLD = new AntiqueToolMaterial(BlockTags.INCORRECT_FOR_GOLD_TOOL, 32, 12.0F, 0.0F, 22, ItemTags.GOLD_TOOL_MATERIALS);
	public static final AntiqueToolMaterial NETHERITE = new AntiqueToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2031, 9.0F, 4.0F, 15, ItemTags.NETHERITE_TOOL_MATERIALS);


	private Item.Settings applyBaseSettings(Item.Settings settings) {
		return settings.maxDamage(this.durability).repairable(this.repairItems).enchantable(this.enchantmentValue);
	}

	public Item.Settings applyGreatswordSettings(Item.Settings settings, float attackDamage, float attackSpeed, float reach) {
		RegistryEntryLookup<Block> registryEntryLookup = Registries.createEntryLookup(Registries.BLOCK);
		return this.applyBaseSettings(settings)
				.component(
						DataComponentTypes.TOOL,
						new ToolComponent(
								List.of(
										ToolComponent.Rule.ofAlwaysDropping(RegistryEntryList.of(Blocks.COBWEB.getDefaultState().getRegistryEntry()), 15.0F),
										ToolComponent.Rule.of(registryEntryLookup.getOrThrow(BlockTags.SWORD_EFFICIENT), 1.5F)
								),
								1.0F,
								2,
								false
						)
				)
				.attributeModifiers(this.createGreatswordAttributeModifiers(attackDamage, attackSpeed, reach));
	}

	private AttributeModifiersComponent createGreatswordAttributeModifiers(float attackDamage, float attackSpeed, float reach) {
		return AttributeModifiersComponent.builder()
				.add(
						EntityAttributes.ATTACK_DAMAGE,
						new EntityAttributeModifier(
								Item.BASE_ATTACK_DAMAGE_MODIFIER_ID, attackDamage + this.attackDamageBonus, EntityAttributeModifier.Operation.ADD_VALUE
						),
						AttributeModifierSlot.MAINHAND
				)
				.add(
						EntityAttributes.ATTACK_SPEED,
						new EntityAttributeModifier(Item.BASE_ATTACK_SPEED_MODIFIER_ID, attackSpeed, EntityAttributeModifier.Operation.ADD_VALUE),
						AttributeModifierSlot.MAINHAND
				)
				.add(
						EntityAttributes.ENTITY_INTERACTION_RANGE,
						new EntityAttributeModifier(Identifier.ofVanilla("base_entity_reach"), reach, EntityAttributeModifier.Operation.ADD_VALUE),
						AttributeModifierSlot.MAINHAND
				)
				.build();
	}
}
