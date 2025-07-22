package net.hollowed.antique.util.resources;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.hollowed.combatamenities.CombatAmenities;
import net.hollowed.combatamenities.util.delay.ClientTickDelayScheduler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyriadStaffTransformResourceReloadListener implements SimpleSynchronousResourceReloadListener {
    private static final Map<Identifier, MyriadStaffTransformData> transforms = new HashMap<>();
    private static MyriadStaffTransformData defaultTransforms;

    @Override
    public Identifier getFabricId() {
        return Identifier.of(CombatAmenities.MOD_ID, "staff_transforms");
    }

    @Override
    public void reload(ResourceManager manager) {
        MinecraftClient.getInstance().execute(() -> this.actuallyLoad(manager));
    }

    public void actuallyLoad(ResourceManager manager) {
        ClientTickDelayScheduler.schedule(-1, () -> {
            transforms.clear();

            manager.findResources("staff_transforms", path -> path.getPath().endsWith(".json")).keySet().forEach(id -> {
                if (manager.getResource(id).isPresent()) {
                    try (InputStream stream = manager.getResource(id).get().getInputStream()) {
                        var json = JsonHelper.deserialize(new InputStreamReader(stream, StandardCharsets.UTF_8));
                        DataResult<MyriadStaffTransformData> result = MyriadStaffTransformData.CODEC.parse(JsonOps.INSTANCE, json);

                        result.resultOrPartial(CombatAmenities.LOGGER::error).ifPresent(data -> {
                            if (data.model().equals(Identifier.of("default"))) {
                                data = new MyriadStaffTransformData(
                                        data.item(),
                                        Identifier.of(data.item()),
                                        data.scale(),
                                        data.rotation(),
                                        data.translation()
                                );
                            }
                            if (data.item().equals("default")) {
                                defaultTransforms = data;
                            } else if (data.item().startsWith("#")) {
                                // Remove the '#' prefix
                                String tagString = data.item().substring(1);
                                Identifier tagId = Identifier.of(tagString);

                                TagKey<Item> tag = TagKey.of(Registries.ITEM.getKey(), tagId);

                                if (tag != null) {
                                    MyriadStaffTransformData finalData = data;
                                    Registries.ITEM.forEach(item -> {
                                        Identifier itemId = Registries.ITEM.getId(item);
                                        if (item.getDefaultStack().getRegistryEntry().isIn(tag)) {
                                            transforms.put(itemId, finalData);
                                        }
                                    });
                                } else {
                                    CombatAmenities.LOGGER.warn("Tag #{} not found while loading item transforms!", tagId);
                                }
                            } else {
                                transforms.put(Identifier.of(data.item()), data);
                            }
                        });
                    } catch (Exception e) {
                        CombatAmenities.LOGGER.error("Failed to load transform for {}: {}", id, e.getMessage());
                    }
                }
            });
        });
    }

    public static MyriadStaffTransformData getTransform(Identifier itemId) {
        MyriadStaffTransformData baseTransform = transforms.getOrDefault(itemId, defaultTransforms);

        if (baseTransform != null) {
            return baseTransform;
        }

        // Fallback to a fully default transform if no data is available
        return new MyriadStaffTransformData(
                itemId.toString(),
                itemId,
                List.of(1.0f, 1.0f, 1.0f), // Default scale
                List.of(0.0f, 0.0f, 0.0f), // Default rotation
                List.of(0.0f, 0.0f, 0.0f) // Default translation
        );
    }
}
