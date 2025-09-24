package net.hollowed.antique.util.resources;

import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.hollowed.antique.Antiquities;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ClothOverlayListener implements SimpleSynchronousResourceReloadListener {
    private static final ArrayList<Identifier> transforms = new ArrayList<>();

    @Override
    public Identifier getFabricId() {
        return Identifier.of(Antiquities.MOD_ID, "cloth_overlays");
    }

    @Override
    public void reload(ResourceManager manager) {
        Antiquities.addClothItems();
        manager.findResources("cloth_overlays", path -> path.getPath().endsWith(".json")).keySet().forEach(id -> {
            if (manager.getResource(id).isPresent()) {
                try (InputStream stream = manager.getResource(id).get().getInputStream()) {
                    JsonObject json = JsonHelper.deserialize(new InputStreamReader(stream, StandardCharsets.UTF_8));
                    DataResult<ClothOverlayData> result = ClothOverlayData.CODEC.parse(JsonOps.INSTANCE, json);

                    result.resultOrPartial(Antiquities.LOGGER::error).ifPresent(data -> transforms.addAll(data.list()));
                } catch (Exception e) {
                    Antiquities.LOGGER.error("Failed to load transform for {}: {}", id, e.getMessage());
                }
            }
        });
    }

    public static ArrayList<Identifier> getTransforms() {
        return transforms;
    }
}
