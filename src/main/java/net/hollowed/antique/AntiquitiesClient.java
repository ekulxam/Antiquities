package net.hollowed.antique;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.hollowed.antique.blocks.ModBlocks;
import net.hollowed.antique.blocks.entities.ModBlockEntities;
import net.hollowed.antique.blocks.entities.renderer.PedestalRenderer;
import net.hollowed.antique.client.ModEntityLayers;
import net.hollowed.antique.client.armor.models.AdventureArmor;
import net.hollowed.antique.client.armor.models.ArmorStandAdventureArmor;
import net.hollowed.antique.entities.ModEntities;
import net.hollowed.antique.entities.models.ExplosiveSpearCloth;
import net.hollowed.antique.entities.models.PaleWardenModel;
import net.hollowed.antique.entities.renderer.*;
import net.hollowed.antique.networking.*;
import net.hollowed.antique.particles.ModParticles;
import net.hollowed.antique.util.FreezeFrameManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class AntiquitiesClient implements ClientModInitializer {

    // Global variable to track the last time the use key was pressed
    private static long lastUseTime = 0;  // Time of last use in milliseconds
    private static final long COOLDOWN_TIME = 250;  // Cooldown time in milliseconds (500 ms = 0.5 seconds)
    private static boolean wasCrawling = false; // Store previous key state

    public static final EntityModelLayer PALE_WARDEN_LAYER = new EntityModelLayer(Identifier.of(Antiquities.MOD_ID, "pale_warden"), "main");
    public static final EntityModelLayer EXPLOSIVE_SPEAR_CLOTH_LAYER = new EntityModelLayer(Identifier.of(Antiquities.MOD_ID, "explosive_spear_cloth"), "main");

    @Override
    public void onInitializeClient() {

        ModParticles.initializeClient();

        /*
            Block Renderers
         */

        BlockEntityRendererFactories.register(ModBlockEntities.PEDESTAL_BLOCK_ENTITY, PedestalRenderer::new);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), ModBlocks.PEDESTAL, ModBlocks.HOLLOW_CORE, ModBlocks.JAR);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTripwire(), ModBlocks.IVY);

        /*
            Packets
         */

        PedestalPacketReceiver.registerClientPacket();
        WallJumpParticlePacketReceiver.registerClientPacket();
        IllusionerParticlePacketReceiver.registerServerPacket();

        /*
            Entity Renderers
         */

        EntityModelLayerRegistry.registerModelLayer(ModEntityLayers.ADVENTURE_ARMOR, AdventureArmor::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModEntityLayers.ARMOR_STAND_ADVENTURE_ARMOR, ArmorStandAdventureArmor::getTexturedModelData);

        EntityRendererRegistry.register(ModEntities.PALE_WARDEN, PaleWardenRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(PALE_WARDEN_LAYER, PaleWardenModel::getTexturedModelData);

        EntityRendererRegistry.register(ModEntities.MYRIAD_SHOVEL, MyriadShovelEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.MYRIAD_SHOVEL_PART, MyriadShovelPartRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(EXPLOSIVE_SPEAR_CLOTH_LAYER, ExplosiveSpearCloth::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.EXPLOSIVE_SPEAR, ExplosiveSpearEntityRenderer::new);

        EntityRendererRegistry.register(ModEntities.ILLUSIONER, IllusionerEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.ILLUSIONER_CLONE, IllusionerCloneEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.SMOKE_BOMB, SmokeBombRenderer::new);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) {
                return;
            }

            if (ModKeyBindings.crawl.wasPressed()) { // Detect key press event
                wasCrawling = !wasCrawling; // Toggle crawling state
                ClientPlayNetworking.send(new CrawlPacketPayload(wasCrawling));
            }
            if (!client.player.isOnGround()) {
                wasCrawling = false;
                ClientPlayNetworking.send(new CrawlPacketPayload(wasCrawling));
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
                FreezeFrameManager.tick();
        });

        // Right Click Listener
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Ensure the client player is not null
            if (client.player == null) {
                return;
            }

            long currentTime = System.currentTimeMillis();  // Get current time in milliseconds

            // Check if the show satchel key is pressed and if the cooldown period has passed
            if (ModKeyBindings.showSatchel.isPressed()) {
                // Check if right-click is pressed and if the cooldown has passed
                if (client.options.useKey.isPressed() && currentTime - lastUseTime >= COOLDOWN_TIME) {
                    // Send the packet if right-click is detected and the other keys are pressed
                    ClientPlayNetworking.send(new SatchelPacketPayload(true));

                    // Update the last use time to the current time
                    lastUseTime = currentTime;
                }
            }
        });
    }
}
