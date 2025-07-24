package net.hollowed.antique;

import com.nitron.nitrogen.config.Config;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.hollowed.antique.index.AntiqueBlockEntities;
import net.hollowed.antique.index.*;
import net.hollowed.antique.networking.*;
import net.hollowed.antique.index.AntiqueStats;
import net.hollowed.antique.index.AntiqueLootTableModifiers;
import net.hollowed.antique.util.resources.MyriadStaffTransformResourceReloadListener;
import net.hollowed.antique.util.delay.TickDelayScheduler;
import net.hollowed.antique.util.resources.ClothSkinListener;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.*;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Predicate;

import static net.minecraft.item.Item.BASE_ATTACK_DAMAGE_MODIFIER_ID;
import static net.minecraft.item.Item.BASE_ATTACK_SPEED_MODIFIER_ID;

public class Antiquities implements ModInitializer {
	public static final String MOD_ID = "antique";

	public static Identifier id(String string) {
		return Identifier.of(MOD_ID, string);
	}

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@SuppressWarnings("all")
	@Override
	public void onInitialize() {

		/*
			Initializers
		 */

		AntiqueBlocks.initialize();
		AntiqueScreenHandlerType.init();
		AntiqueStats.init();
		AntiqueEnchantments.initialize();
		AntiqueBlockEntities.initialize();
		AntiqueLootTableModifiers.modifyLootTables();
		AntiqueEntities.initialize();
		AntiqueComponents.initialize();
		AntiqueParticles.initialize();
		AntiqueItems.initialize();
		AntiqueKeyBindings.initialize();
		AntiqueSounds.initialize();
		AntiqueEffects.initialize();
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new MyriadStaffTransformResourceReloadListener());

		/*
			Packets
		 */

		PayloadTypeRegistry.playS2C().register(PedestalPacketPayload.ID, PedestalPacketPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(PaleWardenTickPacketPayload.ID, PaleWardenTickPacketPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(SatchelPacketPayload.ID, SatchelPacketPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(WallJumpPacketPayload.ID, WallJumpPacketPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(WallJumpParticlePacketPayload.ID, WallJumpParticlePacketPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(CrawlPacketPayload.ID, CrawlPacketPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(DyePacketPayload.ID, DyePacketPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(IllusionerParticlePacketPayload.ID, IllusionerParticlePacketPayload.CODEC);

		SatchelPacketReceiver.registerServerPacket();
		PaleWardenTickPacketReceiver.registerServerPacket();
		WallJumpPacketReceiver.registerServerPacket();
		CrawlPacketReceiver.registerServerPacket();
		DyePacketReceiver.registerServerPacket();

		/*
			Tick Events
		 */

		ServerTickEvents.END_SERVER_TICK.register(server -> TickDelayScheduler.tick());

		ServerTickEvents.END_WORLD_TICK.register(world -> {
			for (ServerPlayerEntity player : world.getPlayers()) {
				if (player.isSneaking()) {
					player.setSwimming(true);
				}
			}
		});

		/*
			Item Group
		 */

		Registry.register(Registries.ITEM_GROUP, ANTIQUITIES_GROUP_KEY, ANTIQUITIES_GROUP);
		addItems();

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(itemGroup -> {
			itemGroup.addAfter(Items.HUSK_SPAWN_EGG, AntiqueItems.ILLUSIONER_SPAWN_EGG);
		});

		/*
			Component Modification
		 */

		DefaultItemComponentEvents.MODIFY.register(ctx -> ctx.modify(
				Predicate.isEqual(AntiqueItems.REVERENCE),
				(builder, item) -> builder.add(DataComponentTypes.ITEM_NAME, Text.translatable(item.getTranslationKey()).withColor(0xff5a00))
		));

		DefaultItemComponentEvents.MODIFY.register(ctx -> ctx.modify(
				List.of(
						Items.BUNDLE, Items.WHITE_BUNDLE, Items.LIGHT_GRAY_BUNDLE, Items.GRAY_BUNDLE, Items.BLACK_BUNDLE, Items.BROWN_BUNDLE, Items.RED_BUNDLE,
						Items.ORANGE_BUNDLE, Items.YELLOW_BUNDLE, Items.LIME_BUNDLE, Items.GREEN_BUNDLE, Items.CYAN_BUNDLE, Items.LIGHT_BLUE_BUNDLE, Items.BLUE_BUNDLE,
						Items.PURPLE_BUNDLE, Items.MAGENTA_BUNDLE, Items.PINK_BUNDLE
						),
				(builder, item) -> builder.add(DataComponentTypes.ENCHANTABLE, new EnchantableComponent(10))
		));

		/*
			Resource Pack
		 */

		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new ClothSkinListener());

		FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent((container) ->
				ResourceManagerHelper.registerBuiltinResourcePack(Identifier.of(MOD_ID, "antique"), container, Text.translatable("resourcePack.hmi.name"), ResourcePackActivationType.NORMAL));

		/*
			Shenanigans
		 */

		Config.trailRenderers = false;
	}

	public static final RegistryKey<ItemGroup> ANTIQUITIES_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(MOD_ID, "antiquities_group"));
	public static final ItemGroup ANTIQUITIES_GROUP = FabricItemGroup.builder()
			.icon(() -> new ItemStack(AntiqueItems.FUR_BOOTS))
			.displayName(Text.translatable("itemGroup.antique.antiquities").withColor(0x7d4e33))
			.build();

	private void addItems() {
		// Register items to the custom item group.
		ItemGroupEvents.modifyEntriesEvent(ANTIQUITIES_GROUP_KEY).register(itemGroup -> {
			itemGroup.add(AntiqueItems.MYRIAD_TOOL);

			ItemStack myriadMattock = AntiqueItems.MYRIAD_TOOL.getDefaultStack();
			myriadMattock.set(AntiqueComponents.MYRIAD_STACK, AntiqueItems.MYRIAD_PICK_HEAD.getDefaultStack());
			myriadMattock.set(DataComponentTypes.ITEM_MODEL, Antiquities.id("myriad_mattock"));
			myriadMattock.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder()
					.add(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, 5.0, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
					.add(EntityAttributes.ATTACK_SPEED, new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, -2.4, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
					.add(EntityAttributes.ENTITY_INTERACTION_RANGE, new EntityAttributeModifier(Identifier.ofVanilla("base_attack_range"), 0.75, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
					.build());
			myriadMattock.set(DataComponentTypes.TOOL, new ToolComponent(
					List.of(
							ToolComponent.Rule.ofNeverDropping(AntiqueItems.registryEntryLookup.getOrThrow(BlockTags.INCORRECT_FOR_DIAMOND_TOOL)),
							ToolComponent.Rule.ofAlwaysDropping(AntiqueItems.registryEntryLookup.getOrThrow(TagKey.of(RegistryKeys.BLOCK, Identifier.of(Antiquities.MOD_ID, "mineable/mattock"))), 12)
					),
					1,
					1,
					true
			));
			itemGroup.add(myriadMattock);

			ItemStack myriadAxe = AntiqueItems.MYRIAD_TOOL.getDefaultStack();
			myriadAxe.set(AntiqueComponents.MYRIAD_STACK, AntiqueItems.MYRIAD_AXE_HEAD.getDefaultStack());
			myriadAxe.set(DataComponentTypes.ITEM_MODEL, Antiquities.id("myriad_axe"));
			myriadAxe.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder()
					.add(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, 9.0, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
					.add(EntityAttributes.ATTACK_SPEED, new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, -3, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
					.add(EntityAttributes.ENTITY_INTERACTION_RANGE, new EntityAttributeModifier(Identifier.ofVanilla("base_attack_range"), 0.75, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
					.build());
			myriadAxe.set(DataComponentTypes.TOOL, new ToolComponent(
					List.of(
							ToolComponent.Rule.ofNeverDropping(AntiqueItems.registryEntryLookup.getOrThrow(BlockTags.INCORRECT_FOR_DIAMOND_TOOL)),
							ToolComponent.Rule.ofAlwaysDropping(AntiqueItems.registryEntryLookup.getOrThrow(BlockTags.AXE_MINEABLE), 12)
					),
					1,
					1,
					true
			));
			myriadAxe.set(DataComponentTypes.WEAPON, new WeaponComponent(0, 2));
			itemGroup.add(myriadAxe);

			ItemStack myriadShovel = AntiqueItems.MYRIAD_TOOL.getDefaultStack();
			myriadShovel.set(AntiqueComponents.MYRIAD_STACK, AntiqueItems.MYRIAD_SHOVEL_HEAD.getDefaultStack());
			myriadShovel.set(DataComponentTypes.ITEM_MODEL, Antiquities.id("myriad_shovel"));
			myriadShovel.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder()
					.add(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, 8.0, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
					.add(EntityAttributes.ATTACK_SPEED, new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, -2.9, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
					.add(EntityAttributes.ENTITY_INTERACTION_RANGE, new EntityAttributeModifier(Identifier.ofVanilla("base_attack_range"), 0.75, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
					.build());
			myriadShovel.set(DataComponentTypes.TOOL, new ToolComponent(
					List.of(
							ToolComponent.Rule.ofNeverDropping(AntiqueItems.registryEntryLookup.getOrThrow(BlockTags.INCORRECT_FOR_DIAMOND_TOOL)),
							ToolComponent.Rule.ofAlwaysDropping(AntiqueItems.registryEntryLookup.getOrThrow(BlockTags.SHOVEL_MINEABLE), 12)
					),
					1,
					1,
					true
			));
			itemGroup.add(myriadShovel);

			ItemStack myriadCleaver = AntiqueItems.MYRIAD_TOOL.getDefaultStack();
			myriadCleaver.set(AntiqueComponents.MYRIAD_STACK, AntiqueItems.MYRIAD_CLEAVER_BLADE.getDefaultStack());
			myriadCleaver.set(DataComponentTypes.ITEM_MODEL, Antiquities.id("myriad_cleaver"));
			myriadCleaver.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder()
					.add(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, 6.0, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
					.add(EntityAttributes.ATTACK_SPEED, new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, -2.2, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
					.add(EntityAttributes.ENTITY_INTERACTION_RANGE, new EntityAttributeModifier(Identifier.ofVanilla("base_attack_range"), 1, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
					.build());
			myriadCleaver.set(net.hollowed.combatamenities.util.items.ModComponents.INTEGER_PROPERTY, 1);
			itemGroup.add(myriadCleaver);


			itemGroup.add(AntiqueItems.MYRIAD_PICK_HEAD);
			itemGroup.add(AntiqueItems.MYRIAD_AXE_HEAD);
			itemGroup.add(AntiqueItems.MYRIAD_SHOVEL_HEAD);
			itemGroup.add(AntiqueItems.MYRIAD_CLEAVER_BLADE);
			itemGroup.add(AntiqueBlocks.MYRIAD_ORE);
			itemGroup.add(AntiqueBlocks.DEEPSLATE_MYRIAD_ORE);
			itemGroup.add(AntiqueBlocks.MYRIAD_CLUSTER);
			itemGroup.add(AntiqueBlocks.DEEPSLATE_MYRIAD_CLUSTER);
			itemGroup.add(AntiqueItems.RAW_MYRIAD);
			itemGroup.add(AntiqueBlocks.RAW_MYRIAD_BLOCK);
			itemGroup.add(AntiqueItems.MYRIAD_INGOT);
			itemGroup.add(AntiqueBlocks.MYRIAD_BLOCK);
			itemGroup.add(AntiqueBlocks.EXPOSED_MYRIAD_BLOCK);
			itemGroup.add(AntiqueBlocks.WEATHERED_MYRIAD_BLOCK);
			itemGroup.add(AntiqueBlocks.TARNISHED_MYRIAD_BLOCK);
			itemGroup.add(AntiqueBlocks.COATED_MYRIAD_BLOCK);
			itemGroup.add(AntiqueBlocks.COATED_EXPOSED_MYRIAD_BLOCK);
			itemGroup.add(AntiqueBlocks.COATED_WEATHERED_MYRIAD_BLOCK);
			itemGroup.add(AntiqueBlocks.COATED_TARNISHED_MYRIAD_BLOCK);
			itemGroup.add(AntiqueItems.SILK);
			itemGroup.add(AntiqueItems.MIRAGE_SILK);
			itemGroup.add(AntiqueItems.NETHERITE_PAULDRONS);
			itemGroup.add(AntiqueItems.SATCHEL);
			itemGroup.add(AntiqueItems.FUR_BOOTS);
			itemGroup.add(AntiqueBlocks.HOLLOW_CORE);
			itemGroup.add(AntiqueItems.SCEPTER);
			itemGroup.add(AntiqueBlocks.PEDESTAL);
			itemGroup.add(AntiqueItems.WARHORN);
			itemGroup.add(AntiqueBlocks.IVY);
			itemGroup.add(AntiqueItems.IRON_GREATSWORD);
			itemGroup.add(AntiqueItems.GOLDEN_GREATSWORD);
			itemGroup.add(AntiqueItems.DIAMOND_GREATSWORD);
			itemGroup.add(AntiqueItems.NETHERITE_GREATSWORD);
			itemGroup.add(AntiqueBlocks.DYE_TABLE);

			itemGroup.add(AntiqueItems.IRREVERENT);
		});
	}

	public static ItemStack getMyriadShovelStack() {
		ItemStack myriadShovel = AntiqueItems.MYRIAD_TOOL.getDefaultStack();
		myriadShovel.set(AntiqueComponents.MYRIAD_STACK, AntiqueItems.MYRIAD_SHOVEL_HEAD.getDefaultStack());
		myriadShovel.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder()
				.add(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, 8.0, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
				.add(EntityAttributes.ATTACK_SPEED, new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, -2.9, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
				.add(EntityAttributes.ENTITY_INTERACTION_RANGE, new EntityAttributeModifier(Identifier.ofVanilla("base_attack_range"), 0.75, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
				.build());
		myriadShovel.set(DataComponentTypes.TOOL, new ToolComponent(
				List.of(
						ToolComponent.Rule.ofNeverDropping(AntiqueItems.registryEntryLookup.getOrThrow(BlockTags.INCORRECT_FOR_DIAMOND_TOOL)),
						ToolComponent.Rule.ofAlwaysDropping(AntiqueItems.registryEntryLookup.getOrThrow(BlockTags.SHOVEL_MINEABLE), 12)
				),
				1.0F,
				1,
				true
		));
		return myriadShovel;
	}
}