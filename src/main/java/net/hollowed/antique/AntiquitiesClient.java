package net.hollowed.antique;

import com.nitron.nitrogen.Nitrogen;
import com.nitron.nitrogen.render.RenderUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.hollowed.antique.client.armor.models.VanillaArmorModel;
import net.hollowed.antique.index.*;
import net.hollowed.antique.blocks.screens.DyeingScreen;
import net.hollowed.antique.blocks.entities.renderer.PedestalRenderer;
import net.hollowed.antique.client.armor.models.AdventureArmor;
import net.hollowed.antique.client.armor.models.ArmorStandAdventureArmor;
import net.hollowed.antique.entities.MyriadShovelEntity;
import net.hollowed.antique.entities.SmokeBombEntity;
import net.hollowed.antique.entities.models.PaleWardenModel;
import net.hollowed.antique.entities.renderer.*;
import net.hollowed.antique.networking.*;
import net.hollowed.combatamenities.util.delay.ClientTickDelayScheduler;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.projectile.*;
import net.minecraft.entity.projectile.thrown.LingeringPotionEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.entity.projectile.thrown.SplashPotionEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.*;

public class AntiquitiesClient implements ClientModInitializer {

    private static final Map<UUID, Deque<Vec3d>> TRAILS = new HashMap<>();

    // Global variable to track the last time the use key was pressed
    private static long lastUseTime = 0;  // Time of last use in milliseconds
    private static final long COOLDOWN_TIME = 250;  // Cooldown time in milliseconds (500 ms = 0.5 seconds)
    private static boolean wasCrawling = false; // Store previous key state

    public static final EntityModelLayer PALE_WARDEN_LAYER = new EntityModelLayer(Identifier.of(Antiquities.MOD_ID, "pale_warden"), "main");

    @Override
    public void onInitializeClient() {

        MinecraftClient.getInstance().execute(() -> ClientTickDelayScheduler.schedule(-1, () -> {
            TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
            textureManager.registerTexture(Antiquities.id("textures/environment/star.png"));
        }));


        AntiqueParticles.initializeClient();
        HandledScreens.register(AntiqueScreenHandlerType.DYE_TABLE, DyeingScreen::new);

        /*
            Block Renderers
         */

        BlockEntityRendererFactories.register(AntiqueBlockEntities.PEDESTAL_BLOCK_ENTITY, context1 -> new PedestalRenderer());
        BlockRenderLayerMap.putBlocks(BlockRenderLayer.CUTOUT, AntiqueBlocks.PEDESTAL, AntiqueBlocks.HOLLOW_CORE, AntiqueBlocks.JAR, AntiqueBlocks.MYRIAD_CLUSTER, AntiqueBlocks.DEEPSLATE_MYRIAD_CLUSTER);
        BlockRenderLayerMap.putBlocks(BlockRenderLayer.TRIPWIRE, AntiqueBlocks.IVY);
        BlockRenderLayerMap.putBlocks(BlockRenderLayer.TRANSLUCENT, Blocks.GLASS, Blocks.GLASS_PANE);

        /*
            Packets
         */

        PedestalPacketReceiver.registerClientPacket();
        WallJumpParticlePacketReceiver.registerClientPacket();
        IllusionerParticlePacketReceiver.registerServerPacket();

        /*
            Entity Renderers
         */

        EntityModelLayerRegistry.registerModelLayer(AntiqueEntityLayers.VANILLA_ARMOR, VanillaArmorModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(AntiqueEntityLayers.ADVENTURE_ARMOR, AdventureArmor::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(AntiqueEntityLayers.ARMOR_STAND_ADVENTURE_ARMOR, ArmorStandAdventureArmor::getTexturedModelData);

        EntityRendererRegistry.register(AntiqueEntities.PALE_WARDEN, PaleWardenRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(PALE_WARDEN_LAYER, PaleWardenModel::getTexturedModelData);

        EntityRendererRegistry.register(AntiqueEntities.MYRIAD_SHOVEL, MyriadShovelEntityRenderer::new);
        EntityRendererRegistry.register(AntiqueEntities.MYRIAD_SHOVEL_PART, MyriadShovelPartRenderer::new);

        EntityRendererRegistry.register(AntiqueEntities.ILLUSIONER, IllusionerEntityRenderer::new);
        EntityRendererRegistry.register(AntiqueEntities.ILLUSIONER_CLONE, IllusionerCloneEntityRenderer::new);
        EntityRendererRegistry.register(AntiqueEntities.SMOKE_BOMB, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(AntiqueEntities.CAKE_ENTITY, CakeRenderer::new);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) {
                return;
            }

            if (AntiqueKeyBindings.crawl.wasPressed() && !client.player.isSubmergedInWater()) { // Detect key press event
                wasCrawling = !wasCrawling; // Toggle crawling state
                ClientPlayNetworking.send(new CrawlPacketPayload(wasCrawling));
            }
            if (!client.player.isOnGround()) {
                wasCrawling = false;
                ClientPlayNetworking.send(new CrawlPacketPayload(false));
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                Vec3d pos = client.player.getPos();
                Box box = new Box(pos.x - 1, pos.y - 1, pos.z - 1, pos.x + 1, pos.y + 1, pos.z + 1);
                box = box.expand(60);
                if (client.world != null) {
                    for (SnowballEntity entity : client.world.getEntitiesByClass(SnowballEntity.class, box, arrowEntity -> true)) {
                        if (Math.random() > 0.65) {
                            entity.getWorld().addParticleClient(ParticleTypes.ITEM_SNOWBALL, entity.getX(), entity.getY(), entity.getZ(), 0, 0, 0);
                        }
                    }
                }
            }
        });

        // Right Click Listener
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Ensure the client player is not null
            if (client.player == null) {
                return;
            }

            long currentTime = System.currentTimeMillis();  // Get current time in milliseconds

            // Check if the show satchel key is pressed and if the cooldown period has passed
            if (AntiqueKeyBindings.showSatchel.isPressed()) {
                // Check if right-click is pressed and if the cooldown has passed
                if (client.options.useKey.isPressed() && currentTime - lastUseTime >= COOLDOWN_TIME) {
                    // Send the packet if right-click is detected and the other keys are pressed
                    ClientPlayNetworking.send(new SatchelPacketPayload(true));

                    // Update the last use time to the current time
                    lastUseTime = currentTime;
                }
            }
        });

        /*
            This entire section is ARR
         */

        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;
            Vec3d pos = client.player.getPos();
            Box box = new Box(pos.x - 1, pos.y - 1, pos.z - 1, pos.x + 1, pos.y + 1, pos.z + 1);
            box = box.expand(60);

            if (client.world != null) {
                for (ArrowEntity entity : client.world.getEntitiesByClass(ArrowEntity.class, box, arrowEntity -> true)) {
                    Deque<Vec3d> trail = TRAILS.computeIfAbsent(entity.getUuid(), id -> new ArrayDeque<>());
                    Vector3f color = ColorHelper.toVector(entity.getColor());

                    if (entity.isRegionUnloaded()) {
                        TRAILS.remove(entity.getUuid());
                    }

                    RenderUtils.renderEntityTrail(
                            context.matrixStack(),
                            Objects.requireNonNull(context.consumers()).getBuffer(RenderLayer.getEntityTranslucentEmissive(Identifier.of(Antiquities.MOD_ID, "textures/render/color.png"))),
                            context.camera(),
                            entity,
                            context.tickCounter().getTickProgress(false),
                            trail,
                            200,
                            0.1f,
                            0.001f,
                            200,
                            75,
                            color.x,
                            color.y,
                            color.z,
                            new Vec3d(0.0, 0, 0.0)
                    );
                }
                for (SplashPotionEntity entity : client.world.getEntitiesByClass(SplashPotionEntity.class, box, arrowEntity -> true)) {
                    Deque<Vec3d> trail = TRAILS.computeIfAbsent(entity.getUuid(), id -> new ArrayDeque<>());
                    Vector3f color = ColorHelper.toVector(entity.getStack().getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT).getColor());

                    if (entity.isRegionUnloaded()) {
                        TRAILS.remove(entity.getUuid());
                    }

                    RenderUtils.renderEntityTrail(
                            context.matrixStack(),
                            Objects.requireNonNull(context.consumers()).getBuffer(RenderLayer.getEntityTranslucent(Identifier.of(Antiquities.MOD_ID, "textures/render/color.png"))),
                            context.camera(),
                            entity,
                            context.tickCounter().getTickProgress(false),
                            trail,
                            40,
                            0.1f,
                            0.001f,
                            200,
                            0,
                            color.x,
                            color.y,
                            color.z,
                            new Vec3d(0.0, 0, 0.0)
                    );
                }
                for (LingeringPotionEntity entity : client.world.getEntitiesByClass(LingeringPotionEntity.class, box, arrowEntity -> true)) {
                    Deque<Vec3d> trail = TRAILS.computeIfAbsent(entity.getUuid(), id -> new ArrayDeque<>());
                    Vector3f color = ColorHelper.toVector(entity.getStack().getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT).getColor());

                    if (entity.isRegionUnloaded()) {
                        TRAILS.remove(entity.getUuid());
                    }

                    RenderUtils.renderEntityTrail(
                            context.matrixStack(),
                            Objects.requireNonNull(context.consumers()).getBuffer(RenderLayer.getEntityTranslucent(Identifier.of(Antiquities.MOD_ID, "textures/render/color.png"))),
                            context.camera(),
                            entity,
                            context.tickCounter().getTickProgress(false),
                            trail,
                            40,
                            0.1f,
                            0.001f,
                            200,
                            0,
                            color.x,
                            color.y,
                            color.z,
                            new Vec3d(0.0, 0, 0.0)
                    );
                }
                for (TridentEntity entity : client.world.getEntitiesByClass(TridentEntity.class, box, arrowEntity -> true)) {
                    Deque<Vec3d> trail = TRAILS.computeIfAbsent(entity.getUuid(), id -> new ArrayDeque<>());
                    Vector3f color = new Vector3f((float) 173 / 255.0F, (float) 255 / 255.0F, (float) 237 / 255.0F);

                    if (entity.isRegionUnloaded()) {
                        TRAILS.remove(entity.getUuid());
                    }

                    RenderUtils.renderEntityTrail(
                            context.matrixStack(),
                            Objects.requireNonNull(context.consumers()).getBuffer(RenderLayer.getEntityTranslucentEmissive(Identifier.of(Antiquities.MOD_ID, "textures/render/color.png"))),
                            context.camera(),
                            entity,
                            context.tickCounter().getTickProgress(false),
                            trail,
                            200,
                            0.1f,
                            0.001f,
                            200,
                            75,
                            color.x,
                            color.y,
                            color.z,
                            new Vec3d(0.0, 0, 0.0)
                    );
                }
                for (WindChargeEntity entity : client.world.getEntitiesByClass(WindChargeEntity.class, box, arrowEntity -> true)) {
                    Deque<Vec3d> trail = TRAILS.computeIfAbsent(entity.getUuid(), id -> new ArrayDeque<>());
                    Vector3f color = new Vector3f((float) 1, (float) 1, (float) 1);

                    if (entity.isRegionUnloaded()) {
                        TRAILS.remove(entity.getUuid());
                    }

                    RenderUtils.renderEntityTrail(
                            context.matrixStack(),
                            Objects.requireNonNull(context.consumers()).getBuffer(RenderLayer.getEntityTranslucentEmissive(Identifier.of(Nitrogen.MOD_ID, "textures/render/color.png"))),
                            context.camera(),
                            entity,
                            context.tickCounter().getTickProgress(false),
                            trail,
                            300,
                            0.1f,
                            0.001f,
                            255,
                            75,
                            color.x,
                            color.y,
                            color.z,
                            new Vec3d(0.0, 0, 0.0)
                    );
                }
                for (MyriadShovelEntity entity : client.world.getEntitiesByClass(MyriadShovelEntity.class, box, arrowEntity -> true)) {
                    Deque<Vec3d> trail = TRAILS.computeIfAbsent(entity.getUuid(), id -> new ArrayDeque<>());
                    Vector3f color = ColorHelper.toVector(entity.getDyeColor());

                    if (entity.isRegionUnloaded()) {
                        TRAILS.remove(entity.getUuid());
                    }

                    RenderUtils.renderEntityTrail(
                            context.matrixStack(),
                            Objects.requireNonNull(context.consumers()).getBuffer(RenderLayer.getEntityTranslucentEmissive(Identifier.of(Antiquities.MOD_ID, "textures/render/color.png"))),
                            context.camera(),
                            entity,
                            context.tickCounter().getTickProgress(false),
                            trail,
                            200,
                            0.1f,
                            0.001f,
                            200,
                            75,
                            color.x,
                            color.y,
                            color.z,
                            new Vec3d(0.0, 0, 0.0)
                    );
                }
                for (FireworkRocketEntity entity : client.world.getEntitiesByClass(FireworkRocketEntity.class, box, arrowEntity -> true)) {
                    Deque<Vec3d> trail = TRAILS.computeIfAbsent(entity.getUuid(), id -> new ArrayDeque<>());
                    Vector3f color = new Vector3f((float) 1, (float) 1, (float) 1);

                    if (entity.isRegionUnloaded()) {
                        TRAILS.remove(entity.getUuid());
                    }

                    RenderUtils.renderEntityTrail(
                            context.matrixStack(),
                            Objects.requireNonNull(context.consumers()).getBuffer(RenderLayer.getEntityTranslucentEmissive(Identifier.of(Antiquities.MOD_ID, "textures/render/color.png"))),
                            context.camera(),
                            entity,
                            context.tickCounter().getTickProgress(false),
                            trail,
                            200,
                            0.1f,
                            0.001f,
                            200,
                            0,
                            color.x,
                            color.y,
                            color.z,
                            new Vec3d(0.0, 0, 0.0)
                    );
                }
                for (SpectralArrowEntity entity : client.world.getEntitiesByClass(SpectralArrowEntity.class, box, arrowEntity -> true)) {
                    Deque<Vec3d> trail = TRAILS.computeIfAbsent(entity.getUuid(), id -> new ArrayDeque<>());
                    Vector3f color = new Vector3f((float) 255 / 255, (float) 248 / 255, (float) 93 / 255);

                    if (entity.isRegionUnloaded()) {
                        TRAILS.remove(entity.getUuid());
                    }

                    RenderUtils.renderEntityTrail(
                            context.matrixStack(),
                            Objects.requireNonNull(context.consumers()).getBuffer(RenderLayer.getEntityTranslucentEmissive(Identifier.of(Antiquities.MOD_ID, "textures/render/color.png"))),
                            context.camera(),
                            entity,
                            context.tickCounter().getTickProgress(false),
                            trail,
                            200,
                            0.1f,
                            0.001f,
                            200,
                            75,
                            color.x,
                            color.y,
                            color.z,
                            new Vec3d(0.0, 0, 0.0)
                    );
                }
                for (SmokeBombEntity entity : client.world.getEntitiesByClass(SmokeBombEntity.class, box, arrowEntity -> true)) {
                    Deque<Vec3d> trail = TRAILS.computeIfAbsent(entity.getUuid(), id -> new ArrayDeque<>());
                    Vector3f color = new Vector3f(1.0f, 0.216f, 0.51f);

                    if (entity.isRegionUnloaded()) {
                        TRAILS.remove(entity.getUuid());
                    }

                    RenderUtils.renderEntityTrail(
                            context.matrixStack(),
                            Objects.requireNonNull(context.consumers()).getBuffer(RenderLayer.getEntityTranslucentEmissive(Identifier.of(Antiquities.MOD_ID, "textures/render/color.png"))),
                            context.camera(),
                            entity,
                            context.tickCounter().getTickProgress(false),
                            trail,
                            75,
                            0.1f,
                            0.001f,
                            200,
                            75,
                            color.x,
                            color.y,
                            color.z,
                            new Vec3d(0.0, 0, 0.0)
                    );
                }
                for (AllayEntity entity : client.world.getEntitiesByClass(AllayEntity.class, box, arrowEntity -> true)) {
                    Deque<Vec3d> trail = TRAILS.computeIfAbsent(entity.getUuid(), id -> new ArrayDeque<>());
                    Vector3f color = new Vector3f((float) 95 / 255, (float) 205 / 255, (float) 228 / 255);

                    if (entity.isRegionUnloaded()) {
                        TRAILS.remove(entity.getUuid());
                    }

                    RenderUtils.renderEntityTrail(
                            context.matrixStack(),
                            Objects.requireNonNull(context.consumers()).getBuffer(RenderLayer.getEntitySmoothCutout(Identifier.of(Nitrogen.MOD_ID, "textures/render/color.png"))),
                            context.camera(),
                            entity,
                            context.tickCounter().getTickProgress(false),
                            trail,
                            200,
                            0.1f,
                            0.001f,
                            255,
                            0,
                            color.x,
                            color.y,
                            color.z,
                            new Vec3d(0.0, 0.25, 0.0)
                    );
                }
            }
        });

        /*
            End of ARR section
         */
    }
}
