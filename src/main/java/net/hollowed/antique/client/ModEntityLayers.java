package net.hollowed.antique.client;

import com.google.common.collect.Sets;
import net.hollowed.antique.Antiquities;
import net.minecraft.block.MagmaBlock;
import net.minecraft.block.SoulSandBlock;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Set;

public class ModEntityLayers extends EntityModelLayers {
    private static final Set<EntityModelLayer> LAYERS = Sets.newHashSet();

    public static final EntityModelLayer ADVENTURE_ARMOR = registerMain("adventure_armor");
    public static final EntityModelLayer ARMOR_STAND_ADVENTURE_ARMOR = registerMain("armor_stand_adventure_armor");

    private static EntityModelLayer registerMain(String id) {
        return register(id, "main");
    }

    private static EntityModelLayer register(String id, String layer) {
        EntityModelLayer entityModelLayer = create(id, layer);
        if (!LAYERS.add(entityModelLayer)) {
            throw new IllegalStateException("Duplicate registration for " + entityModelLayer);
        } else {
            return entityModelLayer;
        }
    }

    private static EntityModelLayer create(String id, String layer) {
        return new EntityModelLayer(Identifier.of(Antiquities.MOD_ID, id), layer);
    }
}
