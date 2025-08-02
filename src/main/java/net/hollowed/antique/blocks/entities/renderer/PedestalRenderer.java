package net.hollowed.antique.blocks.entities.renderer;

import net.hollowed.antique.blocks.entities.PedestalBlockEntity;
import net.hollowed.antique.index.AntiqueItems;
import net.hollowed.antique.items.VelocityTransferMaceItem;
import net.hollowed.antique.mixin.accessors.EntityRenderDispatcherAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PedestalRenderer implements BlockEntityRenderer<PedestalBlockEntity> {

    private static final Vec3d ITEM_POS = new Vec3d(0.5, 1.5, 0.5);

    public PedestalRenderer() {}

    @Override
    public void render(PedestalBlockEntity pedestalBlockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos) {
        ItemStack heldItem = pedestalBlockEntity.getStack(0);
        if (!heldItem.isEmpty() && pedestalBlockEntity.getWorld() != null) {
            long worldTime = pedestalBlockEntity.getWorld().getTime();
            double preciseTime = (worldTime) % 360;
            float rotation = (float) ((preciseTime + tickDelta) * 3.0);

            float bob = (float) Math.sin((Math.toRadians(rotation))) * 0.0875f;

            if (heldItem.getItem() instanceof EndCrystalItem) {
                renderEndCrystalEntity(ITEM_POS.add(0, 0, 0), tickDelta, matrices, vertexConsumers, light, pedestalBlockEntity.getWorld());
            } else if (heldItem.getItem() instanceof SpawnEggItem spawnEggItem) {
                renderEntityFromSpawnEgg(spawnEggItem, ITEM_POS.add(0, bob, 0), matrices, vertexConsumers, light, pedestalBlockEntity.getWorld(), rotation, heldItem);
            } else {
                renderItem(heldItem, ITEM_POS.add(0, bob, 0), rotation, matrices, vertexConsumers, light, overlay, pedestalBlockEntity.getWorld());
            }
        }
    }

    private void renderItem(ItemStack itemStack, Vec3d offset, float yRot, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, World world) {
        matrices.push();
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        matrices.translate(offset.x, offset.y, offset.z);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yRot));
        matrices.scale(0.65f, 0.65f, 0.65f);

        if (itemStack.isOf(AntiqueItems.BAG_OF_TRICKS)) matrices.scale(0.75F, 0.75F, 0.75F);
        if (itemStack.getItem() instanceof MaceItem || itemStack.getItem() instanceof VelocityTransferMaceItem
            || itemStack.isIn(ItemTags.SWORDS) || itemStack.isIn(ItemTags.PICKAXES) || itemStack.isIn(ItemTags.AXES) || itemStack.isIn(ItemTags.SHOVELS) || itemStack.isIn(ItemTags.HOES)) {
            matrices.translate(0, 0.1f, 0);
            matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(45));
        }

        itemRenderer.renderItem(itemStack, ItemDisplayContext.FIXED, light, overlay, matrices, vertexConsumers, world, (int) world.getTime());
        matrices.pop();
    }

    private void renderEndCrystalEntity(Vec3d offset, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, World world) {
        matrices.push();
        if (world.isClient) {
            EndCrystalEntity endCrystal = new EndCrystalEntity(EntityType.END_CRYSTAL, world);
            endCrystal.setShowBottom(false);
            endCrystal.setPosition(offset.x, offset.y, offset.z);
            float scale = 0.75F;
            matrices.scale(scale, scale, scale);
            matrices.translate(0.5 * (1 - scale), 0.5 * (1 - scale), 0.5 * (1 - scale));
            long normalizedTime = world.getTime() % 24000;
            endCrystal.endCrystalAge = (int) normalizedTime;
            EntityRenderDispatcher dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
            ((EntityRenderDispatcherAccessor) dispatcher).invokeRender(
                    endCrystal,
                    offset.x,
                    offset.y,
                    offset.z,
                    tickDelta,
                    matrices,
                    vertexConsumers,
                    light,
                    dispatcher.getRenderer(endCrystal)
            );
        }
        matrices.pop();
    }

    private void renderEntityFromSpawnEgg(SpawnEggItem spawnEggItem, Vec3d offset, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, World world, float yRot, ItemStack stack) {
        matrices.push();
        DynamicRegistryManager registryAccess = world.getRegistryManager();
        RegistryWrapper.WrapperLookup wrapperLookup = RegistryWrapper.WrapperLookup.of(registryAccess.stream().distinct());
        EntityType<?> entityType = spawnEggItem.getEntityType(wrapperLookup, spawnEggItem.getDefaultStack());
        Entity entity = entityType.create(world, SpawnReason.MOB_SUMMONED);
        if (entity != null) {
            matrices.translate(offset.x, offset.y, offset.z);
            matrices.translate(0, entity.getHeight() / 2.0, 0);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yRot));
            if (stack.get(DataComponentTypes.CUSTOM_NAME) != null) {
                entity.setCustomName(stack.get(DataComponentTypes.CUSTOM_NAME));
            }
            matrices.translate(0, -entity.getHeight() / 2.0, 0);
            entity.setPos(offset.x, offset.y, offset.z);
            long normalizedTime = world.getTime() % 24000;
            entity.age = (int) normalizedTime;
            entity.setId(-1);
            EntityRenderDispatcher dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
            ((EntityRenderDispatcherAccessor) dispatcher).invokeRender(
                    entity,
                    0.0,
                    0.0,
                    0.0,
                    0,
                    matrices,
                    vertexConsumers,
                    light,
                    dispatcher.getRenderer(entity)
            );
        }
        matrices.pop();
    }
}
