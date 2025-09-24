package net.hollowed.antique.util.resources;

import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.hollowed.antique.Antiquities;
import net.hollowed.combatamenities.util.delay.ClientTickDelayScheduler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.slf4j.Logger;

public class PedestalDisplayListener implements SimpleSynchronousResourceReloadListener {
    private static final Map<Identifier, PedestalDisplayData> transforms = new HashMap<>();
    private static PedestalDisplayData defaultTransforms;

    public Identifier getFabricId() {
        return Antiquities.id("pedestal_transforms");
    }

    public void reload(ResourceManager manager) {
        MinecraftClient.getInstance().execute(() -> this.actuallyLoad(manager));
    }

    public void actuallyLoad(ResourceManager manager) {
        ClientTickDelayScheduler.schedule(-1, () -> {
            transforms.clear();
            manager.findResources("pedestal_transforms", (path) -> path.getPath().endsWith(".json")).keySet().forEach((id) -> {
                if (manager.getResource(id).isPresent()) {
                    try (InputStream stream = manager.getResource(id).get().getInputStream()) {
                        JsonObject json = JsonHelper.deserialize(new InputStreamReader(stream, StandardCharsets.UTF_8));
                        DataResult<PedestalDisplayData> result = PedestalDisplayData.CODEC.parse(JsonOps.INSTANCE, json);
                        Logger var10001 = Antiquities.LOGGER;
                        Objects.requireNonNull(var10001);
                        result.resultOrPartial(var10001::error).ifPresent((data) -> {
                            if (data.item().equals("default")) {
                                defaultTransforms = data;
                            } else if (data.item().startsWith("#")) {
                                String tagString = data.item().substring(1);
                                Identifier tagId = Identifier.of(tagString);
                                TagKey<Item> tag = TagKey.of(Registries.ITEM.getKey(), tagId);
                                if (tag != null) {
                                    Registries.ITEM.forEach((item) -> {
                                        Identifier itemId = Registries.ITEM.getId(item);
                                        if (item.getDefaultStack().getRegistryEntry().isIn(tag)) {
                                            transforms.putIfAbsent(itemId, data);
                                        }

                                    });
                                } else {
                                    Antiquities.LOGGER.warn("Tag #{} not found while loading item transforms!", tagId);
                                }
                            } else {
                                transforms.put(Identifier.of(data.item()), data);
                            }

                        });
                    } catch (Exception e) {
                        Antiquities.LOGGER.error("Failed to load transform for {}: {}", id, e.getMessage());
                    }
                }

            });
        });
    }

    public static PedestalDisplayData getTransform(Identifier itemId) {
        return transforms.getOrDefault(itemId, defaultTransforms);
    }
}
