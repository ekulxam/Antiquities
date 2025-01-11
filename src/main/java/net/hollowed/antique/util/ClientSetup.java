package net.hollowed.antique.util;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.items.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.model.*;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ClientSetup {
    public static final Map<ModelIdentifier, Map<ModelTransformationMode, ModelIdentifier>> LARGE_MODEL = new HashMap<>();

    public static final Map<ModelIdentifier, BakedModel> BAKED_MODELS = new HashMap<>();

    public static ModelIdentifier getCustomModel(ModelIdentifier item, ModelTransformationMode context) {
        return LARGE_MODEL.containsKey(item) && LARGE_MODEL.get(item).containsKey(context) ? new ModelIdentifier(LARGE_MODEL.get(item).get(context).id().withPrefixedPath("item/"), "fabric_resource") : item;
    }

    public static void modifyBakingResult(Map<Identifier, ItemModel> models) {
        // Define a list of pairs for base models and their overlay models
        List<ModelPair> doubleModelPairs = List.of(
                // Example of defining a model pair with base and overlay
                // new ModelPair(Identifier.of(WicksWrivets.MOD_ID, "top_hat"), Identifier.of("wikwriv", "top_hat_inventory"))
        );

        List<ModelPair> tripleModelPairs = List.of(
                new ModelPair(Identifier.of(Antiquities.MOD_ID, "reverence"), Antiquities.id("weightless_scepter"), Identifier.of("minecraft", "stick"))
        );

        for (ModelPair pair : doubleModelPairs) {
            Identifier baseID = pair.baseModelId;
            ItemModel baseModel = models.get(baseID);
            Identifier overlayID = pair.overlayModelId;
            ItemModel overlayModel = models.get(overlayID);

            if (baseModel == null) {
                System.out.println("Base model for " + baseID + " is null");
                continue; // Skip this iteration if base model is missing
            }

            // Combine models only if overlay is non-null
            if (overlayModel != null) {
                // I have no idea what to do here ;-;
                //models.put(baseID, combineDoubleModel(baseModel, overlayModel));
            } else {
                models.put(baseID, baseModel); // Use base model only if overlay is null
                System.out.println("Overlay model for '" + overlayID + "' is null, using base model only.");
            }
        }

        for (ModelPair pair : tripleModelPairs) {
            Identifier baseID = pair.baseModelId;
            ItemModel baseModel = models.get(baseID);
            Identifier overlayID = pair.overlayModelId;
            ItemModel overlayModel = models.get(overlayID);
            Identifier overlayID1 = pair.overlayModelId1;
            ItemModel overlayModel1 = models.get(overlayID1);

            if (baseModel == null) {
                System.out.println("Base model for " + baseID + " is null");
                continue; // Skip this iteration if base model is missing
            }

            // Combine models only if overlays are non-null
            if (overlayModel != null && overlayModel1 != null) {
                // I have no idea what to do here either ;-;
            } else {
                models.put(baseID, baseModel); // Use base model only if overlays are null
                System.out.println("One or more overlays for '" + baseID + "' are null, using base model only.");
            }
        }
    }

    // Helper class to hold pairs of base and overlay model identifiers
    private static class ModelPair {
        final Identifier baseModelId;
        final Identifier overlayModelId;
        final Identifier overlayModelId1;

        ModelPair(Identifier baseModelId, Identifier overlayModelId) {
            this.baseModelId = baseModelId;
            this.overlayModelId = overlayModelId;
            this.overlayModelId1 = null;
        }

        ModelPair(Identifier baseModelId, Identifier overlayModelId, Identifier overlayModelId1) {
            this.baseModelId = baseModelId;
            this.overlayModelId = overlayModelId;
            this.overlayModelId1 = overlayModelId1;
        }
    }

    private static void registerOverlay(String id) {
        for (ModelTransformationMode mode : ModelTransformationMode.values()) {
            LARGE_MODEL.put(new ModelIdentifier(Antiquities.id(id), "inventory"), Map.of(
                    mode, new ModelIdentifier(Antiquities.id(id + "_e"), "inventory")
            ));
        }
    }

    private static void registerDoubleOverlay(String id) {
        for (ModelTransformationMode mode : ModelTransformationMode.values()) {
            LARGE_MODEL.put(new ModelIdentifier(Antiquities.id(id), "inventory"), Map.of(
                    mode, new ModelIdentifier(Antiquities.id(id + "_e"), "inventory")
            ));
            LARGE_MODEL.put(new ModelIdentifier(Antiquities.id(id), "inventory"), Map.of(
                    mode, new ModelIdentifier(Antiquities.id(id + "_e1"), "inventory")
            ));
        }
    }

    public static void clientSetup() {
        registerDoubleOverlay("reverence");
        System.out.println("client was called");
    }
}
