package net.hollowed.antique.index;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

public class AntiqueLootTableModifiers {
    private static final Identifier TRIAL_VAULT =
            Identifier.ofVanilla("chests/trial_chambers/reward_unique");

    // List of village loot tables where bundles may appear
    private static final Identifier[] VILLAGE_CHESTS = new Identifier[] {
            Identifier.ofVanilla("chests/village/village_desert_house"),
            Identifier.ofVanilla("chests/village/village_plains_house"),
            Identifier.ofVanilla("chests/village/village_savanna_house"),
            Identifier.ofVanilla("chests/village/village_snowy_house"),
            Identifier.ofVanilla("chests/village/village_taiga_house"),
            Identifier.ofVanilla("chests/village/village_tannery")
    };

    public static void modifyLootTables() {
        LootTableEvents.MODIFY.register((id, tableBuilder, source, registries) -> {

            // Add Hollow Core to trial vault
            if (id.getValue().equals(TRIAL_VAULT)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.2f))
                        .with(ItemEntry.builder(AntiqueBlocks.HOLLOW_CORE))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());
                tableBuilder.pool(poolBuilder.build());
            }

            // Modify bundle items in village chests
            for (Identifier chestId : VILLAGE_CHESTS) {
                if (id.getValue().equals(chestId)) {
                    LootPool.Builder enchantedBundlePool = LootPool.builder()
                            .conditionally(RandomChanceLootCondition.builder(0.5f)) // 50% chance to appear
                            .rolls(ConstantLootNumberProvider.create(1))
                            .with(ItemEntry.builder(Items.BUNDLE)
                                    .apply(EnchantRandomlyLootFunction.builder(registries))); // Random enchant

                    tableBuilder.pool(enchantedBundlePool.build());
                }
            }
        });
    }
}
