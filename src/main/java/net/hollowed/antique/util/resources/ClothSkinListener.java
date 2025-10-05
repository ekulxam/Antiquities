package net.hollowed.antique.util.resources;

import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.hollowed.antique.Antiquities;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.JsonHelper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ClothSkinListener implements SynchronousResourceReloader {
    private static final Map<String, ClothSkinData.ClothSubData> transforms = new LinkedHashMap<>();

    @Override
    public void reload(ResourceManager manager) {
        Antiquities.addClothItems();
        manager.findResources("cloth_skins", path -> path.getPath().endsWith(".json")).keySet().forEach(id -> {
            if (manager.getResource(id).isPresent()) {
                try (InputStream stream = manager.getResource(id).get().getInputStream()) {
                    JsonObject json = JsonHelper.deserialize(new InputStreamReader(stream, StandardCharsets.UTF_8));
                    DataResult<ClothSkinData> result = ClothSkinData.CODEC.parse(JsonOps.INSTANCE, json);

                    result.resultOrPartial(Antiquities.LOGGER::error).ifPresent(data -> {
                        for (ClothSkinData.ClothSubData entry : data.list()) {
                            transforms.putIfAbsent(entry.model().toString(), entry);
                        }
                    });
                } catch (Exception e) {
                    Antiquities.LOGGER.error("Failed to load transform for {}: {}", id, e.getMessage());
                }
            }
        });
    }

    public static Collection<ClothSkinData.ClothSubData> getTransforms() {
        return transforms.values();
    }

    public static ClothSkinData.ClothSubData getTransform(String id) {
        return transforms.getOrDefault(id, new ClothSkinData.ClothSubData(Antiquities.id("cloth"), "d13a68", 1.4F, 0.1F, 8, 0, true, true));
    }
}
