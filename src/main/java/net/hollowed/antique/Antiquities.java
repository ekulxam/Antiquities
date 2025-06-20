package net.hollowed.antique;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.hollowed.antique.blocks.ModBlocks;
import net.hollowed.antique.blocks.entities.ModBlockEntities;
import net.hollowed.antique.component.ModComponents;
import net.hollowed.antique.effect.AnimeEffect;
import net.hollowed.antique.effect.BounceEffect;
import net.hollowed.antique.enchantments.ModEnchantments;
import net.hollowed.antique.entities.ModEntities;
import net.hollowed.antique.items.ModItems;
import net.hollowed.antique.networking.*;
import net.hollowed.antique.particles.ModParticles;
import net.hollowed.antique.util.ModLootTableModifiers;
import net.hollowed.antique.util.MyriadStaffTransformResourceReloadListener;
import net.hollowed.antique.util.TickDelayScheduler;
import net.minecraft.block.LeafLitterBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.*;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.*;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
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

	@Override
	public void onInitialize() {

		/*
			Initializers
		 */

		ModEnchantments.initialize();
		ModBlocks.initialize();
		ModBlockEntities.initialize();
		ModLootTableModifiers.modifyLootTables();
		ModEntities.initialize();
		ModComponents.initialize();
		ModParticles.initialize();
		ModItems.initialize();
		ModKeyBindings.initialize();
		ModSounds.initialize();
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
		PayloadTypeRegistry.playS2C().register(IllusionerParticlePacketPayload.ID, IllusionerParticlePacketPayload.CODEC);

		SatchelPacketReceiver.registerServerPacket();
		PaleWardenTickPacketReceiver.registerServerPacket();
		WallJumpPacketReceiver.registerServerPacket();
		CrawlPacketReceiver.registerServerPacket();

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
			itemGroup.addAfter(Items.HUSK_SPAWN_EGG, ModItems.ILLUSIONER_SPAWN_EGG);
		});

		/*
			Component Modification
		 */

		DefaultItemComponentEvents.MODIFY.register(ctx -> ctx.modify(
				Predicate.isEqual(ModItems.REVERENCE),
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

		FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent((container) ->
				ResourceManagerHelper.registerBuiltinResourcePack(Identifier.of(MOD_ID, "antique"), container, Text.translatable("resourcePack.hmi.name"), ResourcePackActivationType.NORMAL));

		/*
			Shenanigans
		 */

		//Config.trailRenderers = false;
	}

	public static final RegistryEntry<StatusEffect> VOLATILE_BOUNCE_EFFECT;
	public static final RegistryEntry<StatusEffect> BOUNCE_EFFECT;
	public static final RegistryEntry<StatusEffect> ANIME_EFFECT;

	static {
		BOUNCE_EFFECT = registerEffect("bouncy", new BounceEffect().addAttributeModifier(EntityAttributes.STEP_HEIGHT, Identifier.ofVanilla("effect.step_height"), 1, EntityAttributeModifier.Operation.ADD_VALUE));
		VOLATILE_BOUNCE_EFFECT = registerEffect("volatile_bouncy", new BounceEffect().addAttributeModifier(EntityAttributes.STEP_HEIGHT, Identifier.ofVanilla("effect.step_height"), 1, EntityAttributeModifier.Operation.ADD_VALUE));
		ANIME_EFFECT = registerEffect("anime_effect", new AnimeEffect());
	}

	private static RegistryEntry<StatusEffect> registerEffect(String id, StatusEffect statusEffect) {
		return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MOD_ID, id), statusEffect);
	}

	public static final RegistryKey<ItemGroup> ANTIQUITIES_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(MOD_ID, "antiquities_group"));
	public static final ItemGroup ANTIQUITIES_GROUP = FabricItemGroup.builder()
			.icon(() -> new ItemStack(ModItems.FUR_BOOTS))
			.displayName(Text.translatable("itemGroup.antique.antiquities").withColor(0x7d4e33))
			.build();

	private void addItems() {
		// Register items to the custom item group.
		ItemGroupEvents.modifyEntriesEvent(ANTIQUITIES_GROUP_KEY).register(itemGroup -> {
			itemGroup.add(ModItems.MYRIAD_INGOT);
			itemGroup.add(ModBlocks.MYRIAD_BLOCK);
			itemGroup.add(ModItems.NETHERITE_PAULDRONS);
			itemGroup.add(ModItems.SATCHEL);
			itemGroup.add(ModItems.FUR_BOOTS);
			itemGroup.add(ModBlocks.HOLLOW_CORE);
			itemGroup.add(ModItems.SCEPTER);
			itemGroup.add(ModBlocks.PEDESTAL);
			itemGroup.add(ModBlocks.IVY);
//			itemGroup.add(ModItems.PALE_WARDENS_GREATSWORD);
//			itemGroup.add(ModItems.PALE_WARDEN_STATUE);
			itemGroup.add(ModItems.MYRIAD_TOOL);

			ItemStack myriadMattock = ModItems.MYRIAD_TOOL.getDefaultStack();

			myriadMattock.set(ModComponents.MYRIAD_STACK, ModItems.MYRIAD_PICK_HEAD.getDefaultStack());
			myriadMattock.set(net.hollowed.combatamenities.util.items.ModComponents.INTEGER_PROPERTY, 1);
			myriadMattock.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder()
					.add(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, 5.0, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
					.add(EntityAttributes.ATTACK_SPEED, new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, -2.4, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
					.add(EntityAttributes.ENTITY_INTERACTION_RANGE, new EntityAttributeModifier(Identifier.ofVanilla("base_attack_range"), 0.75, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
					.build());
			myriadMattock.set(DataComponentTypes.TOOL, new ToolComponent(
					List.of(
							ToolComponent.Rule.ofNeverDropping(ModItems.registryEntryLookup.getOrThrow(BlockTags.INCORRECT_FOR_DIAMOND_TOOL)),
							ToolComponent.Rule.ofAlwaysDropping(ModItems.registryEntryLookup.getOrThrow(TagKey.of(RegistryKeys.BLOCK, Identifier.of(Antiquities.MOD_ID, "mineable/mattock"))), 12)
					),
					1,
					1,
					true
			));
			itemGroup.add(myriadMattock);

			ItemStack myriadAxe = ModItems.MYRIAD_TOOL.getDefaultStack();
			myriadAxe.set(ModComponents.MYRIAD_STACK, ModItems.MYRIAD_AXE_HEAD.getDefaultStack());
			myriadAxe.set(net.hollowed.combatamenities.util.items.ModComponents.INTEGER_PROPERTY, 2);
			myriadAxe.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder()
					.add(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, 9.0, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
					.add(EntityAttributes.ATTACK_SPEED, new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, -3, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
					.add(EntityAttributes.ENTITY_INTERACTION_RANGE, new EntityAttributeModifier(Identifier.ofVanilla("base_attack_range"), 0.75, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
					.build());
			myriadAxe.set(DataComponentTypes.TOOL, new ToolComponent(
					List.of(
							ToolComponent.Rule.ofNeverDropping(ModItems.registryEntryLookup.getOrThrow(BlockTags.INCORRECT_FOR_DIAMOND_TOOL)),
							ToolComponent.Rule.ofAlwaysDropping(ModItems.registryEntryLookup.getOrThrow(BlockTags.AXE_MINEABLE), 12)
					),
					1,
					1,
					true
			));
			myriadAxe.set(DataComponentTypes.WEAPON, new WeaponComponent(0, 2));
			itemGroup.add(myriadAxe);

			ItemStack myriadShovel = ModItems.MYRIAD_TOOL.getDefaultStack();
			myriadShovel.set(ModComponents.MYRIAD_STACK, ModItems.MYRIAD_SHOVEL_HEAD.getDefaultStack());
			myriadShovel.set(net.hollowed.combatamenities.util.items.ModComponents.INTEGER_PROPERTY, 3);
			myriadShovel.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder()
					.add(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, 5.5, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
					.add(EntityAttributes.ATTACK_SPEED, new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, -2.8, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
					.add(EntityAttributes.ENTITY_INTERACTION_RANGE, new EntityAttributeModifier(Identifier.ofVanilla("base_attack_range"), 0.75, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
					.build());
			myriadShovel.set(DataComponentTypes.TOOL, new ToolComponent(
					List.of(
							ToolComponent.Rule.ofNeverDropping(ModItems.registryEntryLookup.getOrThrow(BlockTags.INCORRECT_FOR_DIAMOND_TOOL)),
							ToolComponent.Rule.ofAlwaysDropping(ModItems.registryEntryLookup.getOrThrow(BlockTags.SHOVEL_MINEABLE), 12)
					),
					1,
					1,
					true
			));
			itemGroup.add(myriadShovel);

			ItemStack myriadCleaver = ModItems.MYRIAD_TOOL.getDefaultStack();
			myriadCleaver.set(ModComponents.MYRIAD_STACK, ModItems.MYRIAD_CLEAVER_BLADE.getDefaultStack());
			myriadCleaver.set(net.hollowed.combatamenities.util.items.ModComponents.INTEGER_PROPERTY, 4);
			myriadCleaver.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder()
					.add(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, 6.0, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
					.add(EntityAttributes.ATTACK_SPEED, new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, -2.2, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
					.add(EntityAttributes.ENTITY_INTERACTION_RANGE, new EntityAttributeModifier(Identifier.ofVanilla("base_attack_range"), 1, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
					.build());
			itemGroup.add(myriadCleaver);
			itemGroup.add(ModItems.MYRIAD_STAFF);

			itemGroup.add(ModItems.MYRIAD_PICK_HEAD);
			itemGroup.add(ModItems.MYRIAD_AXE_HEAD);
			itemGroup.add(ModItems.MYRIAD_SHOVEL_HEAD);
			itemGroup.add(ModItems.MYRIAD_CLEAVER_BLADE);
			itemGroup.add(ModItems.MYRIAD_CLAW);
			itemGroup.add(ModItems.WARHORN);
			itemGroup.add(ModItems.IRON_GREATSWORD);
			itemGroup.add(ModItems.GOLDEN_GREATSWORD);
			itemGroup.add(ModItems.DIAMOND_GREATSWORD);
			itemGroup.add(ModItems.NETHERITE_GREATSWORD);
			itemGroup.add(ModBlocks.JAR);

			itemGroup.add(ModItems.IRREVERENT);
		});
	}

	public static ItemStack getMyriadShovelStack() {
		ItemStack myriadShovel = ModItems.MYRIAD_TOOL.getDefaultStack();
		myriadShovel.set(ModComponents.MYRIAD_STACK, ModItems.MYRIAD_SHOVEL_HEAD.getDefaultStack());
		myriadShovel.set(net.hollowed.combatamenities.util.items.ModComponents.INTEGER_PROPERTY, 3);
		myriadShovel.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder()
				.add(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, 5.5, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
				.add(EntityAttributes.ATTACK_SPEED, new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, -2.8, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
				.add(EntityAttributes.ENTITY_INTERACTION_RANGE, new EntityAttributeModifier(Identifier.ofVanilla("base_attack_range"), 0.75, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
				.build());
		myriadShovel.set(DataComponentTypes.TOOL, new ToolComponent(
				List.of(
						ToolComponent.Rule.ofNeverDropping(ModItems.registryEntryLookup.getOrThrow(BlockTags.INCORRECT_FOR_DIAMOND_TOOL)),
						ToolComponent.Rule.ofAlwaysDropping(ModItems.registryEntryLookup.getOrThrow(BlockTags.SHOVEL_MINEABLE), 12)
				),
				1.0F,
				1,
				true
		));
		return myriadShovel;
	}
}