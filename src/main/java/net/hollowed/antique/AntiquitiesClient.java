package net.hollowed.antique;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.hollowed.antique.blocks.ModBlocks;
import net.hollowed.antique.blocks.entities.ModBlockEntities;
import net.hollowed.antique.blocks.entities.renderer.PedestalRenderer;
import net.hollowed.antique.client.ModEntityLayers;
import net.hollowed.antique.client.armor.models.AdventureArmor;
import net.hollowed.antique.client.armor.models.ArmorStandAdventureArmor;
import net.hollowed.antique.client.pedestal.PedestalTooltipRenderer;
import net.hollowed.antique.items.custom.SatchelItem;
import net.hollowed.antique.networking.PedestalPacketReceiver;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import java.util.List;

public class AntiquitiesClient implements ClientModInitializer {

    // Variable to keep track of the current item index inside the satchel
    private int currentItemIndex = 0;

    // Variable to track the key press state
    private boolean wasKeyPressed = false;

    @Override
    public void onInitializeClient() {

        /*
            Block Renderers
         */
        BlockEntityRendererFactories.register(ModBlockEntities.PEDESTAL_BLOCK_ENTITY, PedestalRenderer::new);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), ModBlocks.PEDESTAL, ModBlocks.HOLLOW_CORE);

        /*
            Packets
         */
        PedestalPacketReceiver.registerClientPacket();

        /*
            In Game Tooltips
         */
        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.world != null && client.player != null && client.player.isSneaking()) {
                int screenWidth = client.getWindow().getScaledWidth();
                int screenHeight = client.getWindow().getScaledHeight();
                PedestalTooltipRenderer.renderTooltip(context, screenWidth, screenHeight);
            }
        });

        /*
            Entity Renderers
         */
        EntityModelLayerRegistry.registerModelLayer(ModEntityLayers.ADVENTURE_ARMOR, AdventureArmor::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModEntityLayers.ARMOR_STAND_ADVENTURE_ARMOR, ArmorStandAdventureArmor::getTexturedModelData);
    }
}
