package net.hollowed.antique;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.hollowed.antique.blocks.ModBlocks;
import net.hollowed.antique.blocks.entities.ModBlockEntities;
import net.hollowed.antique.effect.BounceEffect;
import net.hollowed.antique.enchantments.ModEnchantments;
import net.hollowed.antique.items.ModItems;
import net.hollowed.antique.networking.PedestalPacketPayload;
import net.hollowed.antique.util.ModLootTableModifiers;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		ModEnchantments.initialize();
		ModBlocks.initialize();
		ModBlockEntities.initialize();
		ModLootTableModifiers.modifyLootTables();

		PayloadTypeRegistry.playS2C().register(PedestalPacketPayload.ID, PedestalPacketPayload.CODEC);

		ModItems.initialize();

		// Register the group.
		Registry.register(Registries.ITEM_GROUP, ANTIQUITIES_GROUP_KEY, ANTIQUITIES_GROUP);

		addItems();

		ServerTickEvents.END_WORLD_TICK.register(world -> {
			for (ServerPlayerEntity player : world.getPlayers()) {
				if (player.isSneaking()) {
					player.setSwimming(true);
				}
			}
		});
	}

	public static final RegistryEntry<StatusEffect> VOLATILE_BOUNCE_EFFECT;
	public static final RegistryEntry<StatusEffect> BOUNCE_EFFECT;

	static {
		BOUNCE_EFFECT = registerEffect("bouncy", new BounceEffect().addAttributeModifier(EntityAttributes.STEP_HEIGHT, Identifier.ofVanilla("effect.step_height"), 1, EntityAttributeModifier.Operation.ADD_VALUE));
		VOLATILE_BOUNCE_EFFECT = registerEffect("volatile_bouncy", new BounceEffect().addAttributeModifier(EntityAttributes.STEP_HEIGHT, Identifier.ofVanilla("effect.step_height"), 1, EntityAttributeModifier.Operation.ADD_VALUE));
	}

	private static RegistryEntry<StatusEffect> registerEffect(String id, StatusEffect statusEffect) {
		return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MOD_ID, id), statusEffect);
	}

	public static final RegistryKey<ItemGroup> ANTIQUITIES_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(MOD_ID, "antiquities_group"));
	public static final ItemGroup ANTIQUITIES_GROUP = FabricItemGroup.builder()
			.icon(() -> new ItemStack(ModItems.FUR_BOOTS))
			.displayName(Text.translatable("itemGroup.antique.antiquities").withColor(0xBE7E4A))
			.build();

	private void addItems() {
		// Register items to the custom item group.
		ItemGroupEvents.modifyEntriesEvent(ANTIQUITIES_GROUP_KEY).register(itemGroup -> {
			itemGroup.add(ModItems.NETHERITE_PAULDRONS);
			itemGroup.add(ModItems.SATCHEL);
			itemGroup.add(ModItems.FUR_BOOTS);
			itemGroup.add(ModItems.COPPER_HANDLE);
			itemGroup.add(ModBlocks.HOLLOW_CORE);
			itemGroup.add(ModItems.WEIGHTLESS_SCEPTER);
			itemGroup.add(ModBlocks.PEDESTAL);
		});
	}
}