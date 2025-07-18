package net.hollowed.antique.items;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.component.ModComponents;
import net.hollowed.antique.entities.ModEntities;
import net.hollowed.antique.items.custom.*;
import net.hollowed.antique.items.custom.myriadStaff.MyriadStaffItem;
import net.hollowed.antique.items.custom.myriadTool.*;
import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.InstrumentComponent;
import net.minecraft.item.*;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.*;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Unit;

import java.util.List;

public class ModItems {

    public static RegistryEntryLookup<Block> registryEntryLookup = Registries.createEntryLookup(Registries.BLOCK);

    public static final Item IRON_GREATSWORD = registerItem("iron_greatsword", new GreatswordItem(ModToolMaterial.IRON, 4.0F, -2.7F, 0.3F, 0.5F, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "iron_greatsword")))
            .maxCount(1)
    ));

    public static final Item GOLDEN_GREATSWORD = registerItem("golden_greatsword", new GreatswordItem(ModToolMaterial.GOLD, 4.0F, -2.7F, 0.2F, 0.5F, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "golden_greatsword")))
            .maxCount(1)
    ));

    public static final Item DIAMOND_GREATSWORD = registerItem("diamond_greatsword", new GreatswordItem(ModToolMaterial.DIAMOND, 4.0F, -2.7F, 0.4F, 0.5F, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "diamond_greatsword")))
            .maxCount(1)
    ));

    public static final Item NETHERITE_GREATSWORD = registerItem("netherite_greatsword", new GreatswordItem(ModToolMaterial.NETHERITE, 4.0F, -2.7F, 0.6F, 0.5F, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "netherite_greatsword")))
            .maxCount(1)
    ));

    public static final Item EXPLOSIVE_SPEAR = registerItem("explosive_spear", new ExplosiveSpearItem(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "explosive_spear")))
            .maxCount(1)
    ));

    public static final Item RAW_MYRIAD = registerItem("raw_myriad", new Item(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "raw_myriad")))
    ));

    public static final Item MYRIAD_INGOT = registerItem("myriad_ingot", new Item(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "myriad_ingot")))
    ));

    public static final Item NETHERITE_PAULDRONS = registerItem("netherite_pauldrons", new NetheritePauldronsItem(ModArmorMaterials.ADVENTURE, EquipmentType.CHESTPLATE, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "netherite_pauldrons")))
            .maxCount(1).fireproof()
    ));

    public static final Item SATCHEL = registerItem("satchel", new SatchelItem(
            new Item.Settings().armor(ModArmorMaterials.ADVENTURE_BASIC, EquipmentType.LEGGINGS)
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "satchel")))
                    .component(ModComponents.SATCHEL_STACK, List.of())
                    .maxCount(1)
                    .fireproof()
    ));

    public static final Item FUR_BOOTS = registerItem("fur_boots", new FurBootsItem(ModArmorMaterials.ADVENTURE, EquipmentType.BOOTS, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "fur_boots")))
            .maxCount(1).fireproof()
    ));

    public static final Item SCEPTER = registerItem("scepter", new VelocityTransferMaceItem(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "scepter")))
            .maxCount(1).attributeModifiers(VelocityTransferMaceItem.createAttributeModifiers()).maxDamage(500).enchantable(10).rarity(Rarity.UNCOMMON)
    ));

    public static final Item PALE_WARDENS_GREATSWORD = registerItem("pale_wardens_greatsword", new Item(new Item.Settings().sword(ToolMaterial.NETHERITE, 1.0F, -2.4F)
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "pale_wardens_greatsword")))
            .maxCount(1).attributeModifiers(VelocityTransferMaceItem.createAttributeModifiers()).maxDamage(2031).enchantable(10).rarity(Rarity.RARE)
    ));

    public static final Item REVERENCE = registerItem("reverence", new ReverenceItem(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "reverence")))
            .maxCount(1).attributeModifiers(ReverenceItem.createAttributeModifiers()).maxDamage(2031).enchantable(10).rarity(Rarity.EPIC)
            .fireproof()
    ));

    public static final Item IRREVERENT = registerItem("irreverence", new DeathItem(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "irreverence")))
            .maxCount(1).attributeModifiers(DeathItem.createAttributeModifiers()).maxDamage(2031).enchantable(10).rarity(Rarity.EPIC)
            .fireproof()
    ));

    public static final Item MYRIAD_TOOL = registerItem("myriad_tool", new MyriadToolItem(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "myriad_tool")))
            .maxCount(1).attributeModifiers(MyriadToolItem.createAttributeModifiers(4, 1.8, 0.25)).enchantable(10).rarity(Rarity.UNCOMMON).fireproof()
            .component(ModComponents.MYRIAD_STACK, ItemStack.EMPTY)
            .component(net.hollowed.combatamenities.util.items.ModComponents.INTEGER_PROPERTY, 0)
            .component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)
            .component(DataComponentTypes.DYED_COLOR, new DyedColorComponent(0xd43b69))
    ));

    public static final Item MYRIAD_STAFF = registerItem("myriad_staff", new MyriadStaffItem(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "myriad_staff")))
            .maxCount(1).attributeModifiers(MyriadToolItem.createAttributeModifiers(4, 1.8, 0.25)).enchantable(10).rarity(Rarity.UNCOMMON).fireproof()
            .component(ModComponents.MYRIAD_STACK, ItemStack.EMPTY)
            .component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)
            .component(DataComponentTypes.DYED_COLOR, new DyedColorComponent(0xd43b69))
    ));

    public static final Item MYRIAD_PICK_HEAD = registerItem("myriad_mattock_head", new MyriadMattockBit(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "myriad_mattock_head")))
            .maxCount(1).attributeModifiers(MyriadToolItem.createAttributeModifiers(3, 2, 0.25)).enchantable(10).rarity(Rarity.UNCOMMON)
            .fireproof().component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE).component(DataComponentTypes.TOOL, ShearsItem.createToolComponent())
    ));

    public static final Item MYRIAD_AXE_HEAD = registerItem("myriad_axe_head", new MyriadAxeBit(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "myriad_axe_head")))
            .maxCount(1).attributeModifiers(MyriadToolItem.createAttributeModifiers(4, 1.7, 0.25)).enchantable(10).rarity(Rarity.UNCOMMON)
            .fireproof().component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)
    ));

    public static final Item MYRIAD_SHOVEL_HEAD = registerItem("myriad_shovel_head", new MyriadShovelBit(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "myriad_shovel_head")))
            .maxCount(1).attributeModifiers(MyriadToolItem.createAttributeModifiers(2, 2.2, 0.25)).enchantable(10).rarity(Rarity.UNCOMMON)
            .fireproof().component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)
    ));

    public static final Item MYRIAD_CLEAVER_BLADE = registerItem("myriad_cleaver_blade", new MyriadCleaverBit(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "myriad_cleaver_blade")))
            .maxCount(1).attributeModifiers(MyriadToolItem.createAttributeModifiers(6, 1.6, 0.25)).enchantable(10).rarity(Rarity.UNCOMMON)
            .fireproof().component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)
    ));

    public static final Item MYRIAD_CLAW = registerItem("myriad_claw", new MyriadClawBit(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "myriad_claw")))
            .maxCount(1).attributeModifiers(MyriadToolItem.createAttributeModifiers(4, 1.8, 0.25)).enchantable(10).rarity(Rarity.UNCOMMON)
            .fireproof().component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)
    ));

    @SuppressWarnings("unused")
    public static final Item DORMANT_REVERENCE = registerItem("dormant_reverence", new ReverenceItem(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "dormant_reverence")))
            .maxCount(1).attributeModifiers(ReverenceItem.createAttributeModifiers()).maxDamage(2031).enchantable(10).rarity(Rarity.EPIC)
    ));

    @SuppressWarnings("all")
    public static final Item WARHORN = registerItem("warhorn", new GoatHornItem(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "warhorn")))
            .maxCount(1).rarity(Rarity.UNCOMMON)
            .component(DataComponentTypes.INSTRUMENT, new InstrumentComponent(ModInstruments.WARHORN))
    ));

    public static final Item PALE_WARDEN_STATUE = registerItem("pale_warden_statue", new PaleWardenSpawnEggItem(ModEntities.PALE_WARDEN, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "pale_warden_statue")))
            .maxCount(64)
    ));

    public static final Item ILLUSIONER_SPAWN_EGG = registerItem("illusioner_spawn_egg", new SpawnEggItem(ModEntities.ILLUSIONER, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Antiquities.MOD_ID, "illusioner_spawn_egg")))
            .maxCount(64)
    ));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Antiquities.MOD_ID, name), item);
    }

    public static void initialize() {
        Antiquities.LOGGER.info("Antiquities Items Initialized");

        ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, tooltipType, list) -> {
            if (itemStack.isOf(ModItems.MYRIAD_TOOL) || itemStack.isOf(MYRIAD_STAFF)) {
                int toRemove = -1;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).toString().contains("dyed")) {
                        toRemove = i;
                    }
                }
                if (toRemove != -1) list.remove(toRemove);
            }
            if (itemStack.isOf(MYRIAD_CLAW)) {
                list.add(1, Text.translatable("item.antique.myriad_claw.tooltip"));
            }
            if (itemStack.isOf(MYRIAD_TOOL)) {
                int toRemove = -1;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).toString().contains("item.color")) {
                        toRemove = i;
                    }
                }
                if (toRemove != -1) list.remove(toRemove);
                assert Formatting.GRAY.getColorValue() != null;
                Text line = Text.translatable("item.antique.myriad_tool.no_tool").withColor(Formatting.GRAY.getColorValue());

                if (!itemStack.getOrDefault(ModComponents.MYRIAD_STACK, ItemStack.EMPTY).isEmpty()) {
                    String string = itemStack.getOrDefault(ModComponents.MYRIAD_STACK, ItemStack.EMPTY).getItem().getTranslationKey();
                    string = string.substring(20);
                    string = "item.antique.myriad_tool." + string.substring(0, string.indexOf("_"));
                    line = Text.translatable(string).withColor(Formatting.GRAY.getColorValue());
                }

                // Add to tooltip
                list.add(1, line);
            }
            if (itemStack.isOf(MYRIAD_STAFF)) {
                int toRemove = -1;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).toString().contains("item.color")) {
                        toRemove = i;
                    }
                }
                if (toRemove != -1) list.remove(toRemove);
                Text text = Text.literal(" - ");
                text = text.copy().append(itemStack.getOrDefault(ModComponents.MYRIAD_STACK, ItemStack.EMPTY).getItemName());
                text = text.copy().append(" -");
                list.add(1, text.getString().equals(" - Air -") ? Text.translatable("item.antique.myriad_staff.empty") : text);
            }
            if (itemStack.getItem() instanceof SatchelItem satchelItem) {
//                List<ItemStack> storedStacks = SatchelItem.getStoredStacks(itemStack);
//                MinecraftClient client = MinecraftClient.getInstance();
//                int maxWidth = 0;
//
//                // Check if satchel is full and center the full message
//                if (SatchelItem.getStoredStacks(itemStack).size() == SatchelItem.MAX_STACKS) {
//                    assert Formatting.RED.getColorValue() != null;
//
//                    // Calculate padding for centering
//                    Text fullMessage = Text.translatable("item.antique.satchel.satchel_full").withColor(Formatting.RED.getColorValue());
//                    int fullMessageWidth = client.textRenderer.getWidth(fullMessage);
//                    int padding = (maxWidth - fullMessageWidth) / 2 + 1;
//
//                    // Add the centered thinga-ma-bobber
//                    list.add(1, Text.literal(" ".repeat(Math.max(0, padding / client.textRenderer.getWidth(" ")))).append(fullMessage));
//
//                    // Add empty line for spacing
//                    list.add(1, Text.literal(""));
//                }
//
//                // Add item information to the tooltip
//                for (int i = storedStacks.size() - 1; i >= 0 ; i--) {
//                    ItemStack storedStack = storedStacks.get(i);
//                    if (!storedStack.isEmpty()) {
//                        assert Formatting.GRAY.getColorValue() != null;
//                        assert storedStack.getRarity().getFormatting().getColorValue() != null;
//
//                        // Determine the prefix based on the index
//                        String prefix = (i == satchelItem.getInternalIndex()) ? "[-] " : " -  ";
//
//                        assert Formatting.WHITE.getColorValue() != null;
//                        int color = Formatting.WHITE.getColorValue(); // Default color
//                        Text customName = storedStack.get(DataComponentTypes.ITEM_NAME);
//                        if (customName != null && customName.getStyle() != null && customName.getStyle().getColor() != null) {
//                            color = customName.getStyle().getColor().getRgb();
//                        } else if (storedStack.getRarity().getFormatting().getColorValue() != null) {
//                            color = storedStack.getRarity().getFormatting().getColorValue();
//                        }
//
//
//                        // Build the line
//                        Text line = Text.literal(prefix).withColor(Formatting.GRAY.getColorValue())
//                                .append(Text.literal(storedStack.getCount() + "x ").withColor(color))
//                                .append(Text.translatable(storedStack.getItem().getTranslationKey()).withColor(color));
//
//                        // Add to tooltip
//                        list.add(1, line);
//
//                        // Calculate max width
//                        int lineWidth = client.textRenderer.getWidth(line);
//                        maxWidth = Math.max(maxWidth, lineWidth);
//                    }
//                }
            }
        });
    }
}
