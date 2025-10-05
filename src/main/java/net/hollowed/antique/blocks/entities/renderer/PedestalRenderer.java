package net.hollowed.antique.blocks.entities.renderer;

import net.hollowed.antique.blocks.entities.PedestalBlockEntity;
import net.hollowed.antique.util.resources.PedestalDisplayData;
import net.hollowed.antique.util.resources.PedestalDisplayListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.state.EndCrystalEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
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
                renderEndCrystalEntity(cameraState, queue, matrices, state.world);
            } else if (heldItem.getItem() instanceof SpawnEggItem spawnEggItem) {
                renderEntityFromSpawnEgg(spawnEggItem, heldItem, ITEM_POS.add(0, bob, 0), cameraState, queue, matrices, state.world, rotation);
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

    private void renderEndCrystalEntity(CameraRenderState cameraState, OrderedRenderCommandQueue queue, MatrixStack matrices, World world) {
        matrices.push();
        if (world.isClient()) {
            EndCrystalEntityRenderState entityState = new EndCrystalEntityRenderState();
            entityState.baseVisible = false;
            entityState.positionOffset = PedestalRenderer.ITEM_POS;
            float scale = 0.75F;
            matrices.scale(scale, scale, scale);
            matrices.translate(0.5 * (1 - scale), 0.5 * (1 - scale), 0.5 * (1 - scale));
            long normalizedTime = world.getTime() % 24000;
            entityState.age = ((int) normalizedTime);
            EntityRenderManager dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
            dispatcher.render(entityState, cameraState, 0, 0, 0, matrices, queue);
        }
        matrices.pop();
    }

    private void renderEntityFromSpawnEgg(SpawnEggItem spawnEggItem, ItemStack stack, Vec3d offset, CameraRenderState cameraState, OrderedRenderCommandQueue queue, MatrixStack matrices, World world, float yRot) {
        matrices.push();
        EntityType<?> entityType = spawnEggItem.getEntityType(stack);
        if (entityType == null) return;
        Entity entity = entityType.create(world, SpawnReason.MOB_SUMMONED);
        if (entity != null) {
            EntityRenderState entityState = new EntityRenderState();

            matrices.translate(offset.x, offset.y, offset.z);
            matrices.translate(0, entity.getHeight() / 2.0, 0);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yRot));
            if (stack.get(DataComponentTypes.CUSTOM_NAME) != null) {
                entityState.displayName = stack.get(DataComponentTypes.CUSTOM_NAME);
            }
            matrices.translate(0, -entity.getHeight() / 2.0, 0);
            entityState.positionOffset = offset;
            long normalizedTime = world.getTime() % 24000;
            entityState.age = (int) normalizedTime;

            EntityRenderManager dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
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
        state.worldTime = blockEntity.getWorld() != null ? blockEntity.getWorld().getTime() : 1L;
    }
}
