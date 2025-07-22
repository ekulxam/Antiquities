package net.hollowed.antique.util.resources;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.hollowed.antique.Antiquities;
import net.hollowed.combatamenities.util.delay.ClientTickDelayScheduler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ClothSkinListener implements SimpleSynchronousResourceReloadListener {
    private static final Map<Integer, ClothSkinData.ClothSubData> transforms = new HashMap<>();

    @Override
    public Identifier getFabricId() {
        return Identifier.of(Antiquities.MOD_ID, "cloth_skins");
    }

    @Override
    public void reload(ResourceManager manager) {
        MinecraftClient.getInstance().execute(() -> this.actuallyLoad(manager));
    }

    private void actuallyLoad(ResourceManager manager) {
        ClientTickDelayScheduler.schedule(-1, () -> {
            transforms.clear();

            manager.findResources("cloth_skins", path -> path.getPath().endsWith(".json")).keySet().forEach(id -> {
                if (manager.getResource(id).isPresent()) {
                    try (InputStream stream = manager.getResource(id).get().getInputStream()) {
                        var json = JsonHelper.deserialize(new InputStreamReader(stream, StandardCharsets.UTF_8));
                        DataResult<ClothSkinData> result = ClothSkinData.CODEC.parse(JsonOps.INSTANCE, json);

                        result.resultOrPartial(Antiquities.LOGGER::error).ifPresent(data -> {
                            for (ClothSkinData.ClothSubData entry : data.list()) {
                                int intValue = 0;
                                try {
                                    if (!entry.hex().isBlank()) {
                                        intValue = Integer.parseInt(entry.hex(), 16);
                                    }
                                } catch (NumberFormatException e) {
                                    System.err.println("Invalid hexadecimal string format: " + e.getMessage());
                                }

                                transforms.put(intValue, entry);
                            }
                        });
                    } catch (Exception e) {
                        Antiquities.LOGGER.error("Failed to load transform for {}: {}", id, e.getMessage());
                    }
                }
            });
        });
    }

    public static ClothSkinData.ClothSubData getTransform(int hex) {
        return transforms.getOrDefault(hex, new ClothSkinData.ClothSubData("", "", 0, 0, 0, 0));
    }
}
