package net.hollowed.antique.blocks.entities.renderer;

import net.hollowed.antique.blocks.entities.PedestalBlockEntity;
import net.hollowed.antique.util.interfaces.duck.BipedEntityRenderStateAccess;
import net.hollowed.antique.util.resources.PedestalDisplayData;
import net.hollowed.antique.util.resources.PedestalDisplayListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.state.*;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.PiglinBruteEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PedestalRenderer implements BlockEntityRenderer<PedestalBlockEntity, PedestalRenderState> {

    private static final Vec3d ITEM_POS = new Vec3d(0.5, 1.5, 0.5);

    public PedestalRenderer() {}

    @Override
    public void render(PedestalRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        ItemStack heldItem = state.storedStack;
        if (!heldItem.isEmpty()) {
            float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(false);
            double preciseTime = (state.worldTime) % 360;
            float rotation = (float) ((preciseTime + tickDelta) * 3.0);

            float bob = (float) Math.sin((Math.toRadians(rotation))) * 0.0875f;

            if (heldItem.getItem() instanceof EndCrystalItem) {
                renderEndCrystalEntity(cameraState, queue, matrices, state.world, tickDelta, state.lightmapCoordinates);
            } else if (heldItem.getItem() instanceof SpawnEggItem spawnEggItem) {
                renderEntityFromSpawnEgg(spawnEggItem, heldItem, ITEM_POS.add(0, bob, 0), cameraState, queue, matrices, state.world, rotation, tickDelta, state.lightmapCoordinates);
            } else {
                renderItem(heldItem, ITEM_POS.add(0, bob, 0), rotation, matrices, queue, state.lightmapCoordinates);
            }
        }
    }

    private void renderItem(ItemStack itemStack, Vec3d offset, float yRot, MatrixStack matrices, OrderedRenderCommandQueue queue, int light) {
        PedestalDisplayData data = PedestalDisplayListener.getTransform(Registries.ITEM.getId(itemStack.getItem()));
        List<Float> translations = data.translations();
        List<Float> rotations = data.rotations();
        List<Float> scales = data.scale();

        matrices.push();
        matrices.translate(offset.x, offset.y, offset.z);
        matrices.translate(translations.getFirst(), translations.get(1), translations.get(2));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yRot));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotations.getFirst()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotations.get(1)));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotations.get(2)));
        matrices.scale(0.65f, 0.65f, 0.65f);
        matrices.scale(scales.getFirst(), scales.get(1), scales.get(2));

        ItemRenderState stackRenderState = new ItemRenderState();
        MinecraftClient.getInstance().getItemModelManager().update(stackRenderState, itemStack, ItemDisplayContext.FIXED, MinecraftClient.getInstance().world, null, 1);
        stackRenderState.render(matrices, queue, light, OverlayTexture.DEFAULT_UV, 0);

        matrices.pop();
    }

    private void renderEndCrystalEntity(CameraRenderState cameraState, OrderedRenderCommandQueue queue, MatrixStack matrices, World world, float tickDelta, int light) {
        matrices.push();
        if (world.isClient()) {
            EndCrystalEntityRenderState entityState = new EndCrystalEntityRenderState();
            entityState.baseVisible = false;
            entityState.positionOffset = PedestalRenderer.ITEM_POS;
            float scale = 0.75F;
            matrices.scale(scale, scale, scale);
            matrices.translate(0.5 * (1 - scale), 0.5 * (1 - scale), 0.5 * (1 - scale));
            entityState.age = world.getTime() % 24000 + tickDelta;
            entityState.entityType = EntityType.END_CRYSTAL;
            entityState.light = light;
            EntityRenderManager dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
            dispatcher.render(entityState, cameraState, 0, 0, 0, matrices, queue);
        }
        matrices.pop();
    }

    private void renderEntityFromSpawnEgg(SpawnEggItem spawnEggItem, ItemStack stack, Vec3d offset, CameraRenderState cameraState, OrderedRenderCommandQueue queue, MatrixStack matrices, World world, float yRot, float tickDelta, int light) {
        matrices.push();
        EntityType<?> entityType = spawnEggItem.getEntityType(stack);
        if (entityType == null) return;
        Entity entity = entityType.create(world, SpawnReason.MOB_SUMMONED);
        if (entity instanceof LivingEntity livingEntity) {
            EntityRenderManager dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
            var entityState = dispatcher.getRenderer(livingEntity).createRenderState();

            matrices.translate(offset.x, offset.y, offset.z);
            matrices.translate(0, entity.getHeight() / 2.0, 0);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yRot));
            if (stack.get(DataComponentTypes.CUSTOM_NAME) != null) {
                entityState.displayName = stack.get(DataComponentTypes.CUSTOM_NAME);
            }
            matrices.translate(0, -entity.getHeight() / 2.0, 0);
            entityState.positionOffset = Vec3d.ZERO;
            entityState.entityType = entityType;
            entityState.age = world.getTime() % 24000 + tickDelta;
            entityState.light = light;

            if (entityState instanceof BipedEntityRenderStateAccess access) {
                access.antique$setEntity(livingEntity);
            }
            if (entityState instanceof PhantomEntityRenderState phantomEntityRenderState) {
                phantomEntityRenderState.wingFlapProgress = entityState.age;
            }
            if (entityState instanceof ChickenEntityRenderState chickenEntityRenderState && livingEntity instanceof ChickenEntity chickenEntity) {
                chickenEntityRenderState.variant = chickenEntity.getVariant().value();
            }
            if (entityState instanceof CatEntityRenderState catEntityRenderState && livingEntity instanceof CatEntity catEntity) {
                catEntityRenderState.texture = catEntity.getVariant().value().assetInfo().texturePath();
            }
            if (entityState instanceof PigEntityRenderState pigEntityRenderState && livingEntity instanceof PigEntity pigEntity) {
                pigEntityRenderState.variant = pigEntity.getVariant().value();
            }
            if (entityState instanceof CowEntityRenderState cowEntityRenderState && livingEntity instanceof CowEntity cowEntity) {
                cowEntityRenderState.variant = cowEntity.getVariant().value();
            }
            if (entityState instanceof PiglinEntityRenderState piglinEntityRenderState && livingEntity instanceof PiglinBruteEntity) {
                piglinEntityRenderState.brute = true;
            }
            if (entityState instanceof SnowGolemEntityRenderState snowGolemEntityRenderState) {
                snowGolemEntityRenderState.hasPumpkin = true;
            }
            if (entityState instanceof LlamaEntityRenderState llamaEntityRenderState && entity instanceof TraderLlamaEntity) {
                llamaEntityRenderState.trader = true;
            }
            if (entityState instanceof VillagerEntityRenderState villagerEntityRenderState && entity instanceof VillagerEntity villagerEntity) {
                villagerEntityRenderState.villagerData = villagerEntity.getVillagerData();
            }
            if (entityState instanceof ZombieVillagerRenderState villagerEntityRenderState && entity instanceof ZombieVillagerEntity villagerEntity) {
                villagerEntityRenderState.villagerData = villagerEntity.getVillagerData().withProfession(Registries.VILLAGER_PROFESSION.getOrThrow(VillagerProfession.NONE));
            }

            dispatcher.render(entityState, cameraState, 0, 0, 0, matrices, queue);
        }
        matrices.pop();
    }

    @Override
    public PedestalRenderState createRenderState() {
        return new PedestalRenderState();
    }

    @Override
    public void updateRenderState(PedestalBlockEntity blockEntity, PedestalRenderState state, float tickProgress, Vec3d cameraPos, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        BlockEntityRenderer.super.updateRenderState(blockEntity, state, tickProgress, cameraPos, crumblingOverlay);
        state.storedStack = blockEntity.getStack(0);
        state.world = blockEntity.getWorld();
        state.worldTime = blockEntity.getWorld() != null ? blockEntity.getWorld().getTime() : 1L;
    }
}
