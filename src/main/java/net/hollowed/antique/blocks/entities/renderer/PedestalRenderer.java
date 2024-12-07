package net.hollowed.antique.blocks.entities.renderer;

import net.hollowed.antique.blocks.entities.custom.PedestalBlockEntity;
import net.hollowed.antique.items.custom.VelocityTransferMaceItem;
import net.hollowed.antique.mixin.EntityRenderDispatcherAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
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
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;

public class PedestalRenderer implements BlockEntityRenderer<PedestalBlockEntity> {

    private static final Vec3d ITEM_POS = new Vec3d(0.5, 1.5, 0.5);

    public PedestalRenderer(BlockEntityRendererFactory.Context context) {

    }

    @Override
    public void render(PedestalBlockEntity pedestalBlockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemStack heldItem = pedestalBlockEntity.getStack(0);

        // Proceed only if the item stack is not empty
        if (!heldItem.isEmpty()) {
            // Get the world time for animation
            long worldTime = Objects.requireNonNull(pedestalBlockEntity.getWorld()).getTime();
            float rotation = (worldTime + tickDelta) * 2; // Smooth rotation
            float bob = (float) Math.sin((worldTime + tickDelta) * 0.1f) * 0.0875f; // Smooth bobbing

            if (heldItem.getItem() instanceof EndCrystalItem) {
                renderEndCrystalEntity(ITEM_POS.add(0, 0, 0), tickDelta, matrices, vertexConsumers, light, pedestalBlockEntity.getWorld());
            } else if (heldItem.getItem() instanceof SpawnEggItem spawnEggItem) {
                renderEntityFromSpawnEgg(spawnEggItem, ITEM_POS.add(0, bob, 0), matrices, vertexConsumers, light, pedestalBlockEntity.getWorld(), rotation, tickDelta, heldItem);
            } else {
                // Render the item
                renderItem(heldItem, ITEM_POS.add(0, bob, 0), rotation, matrices, vertexConsumers, light, overlay, pedestalBlockEntity.getWorld());
            }
        }
    }

    private void renderItem(ItemStack itemStack, Vec3d offset, float yRot, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, World world) {
        matrices.push();

        // Get the item renderer instance
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

        // Apply transformations
        matrices.translate(offset.x, offset.y, offset.z);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yRot));
        matrices.scale(0.65f, 0.65f, 0.65f);

        if (itemStack.getItem() instanceof SwordItem || itemStack.getItem() instanceof MiningToolItem || itemStack.getItem() instanceof MaceItem || itemStack.getItem() instanceof VelocityTransferMaceItem) {
            matrices.translate(0, 0.1f, 0);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(45));
        }

        // Render the item
        itemRenderer.renderItem(itemStack, ModelTransformationMode.GUI, light, overlay, matrices, vertexConsumers, world, (int) world.getTime());

        matrices.pop();
    }

    private void renderEndCrystalEntity(Vec3d offset, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, World world) {
        matrices.push();
        if (world.isClient) {
            // Create an End Crystal entity only if necessary
            EndCrystalEntity endCrystal = new EndCrystalEntity(EntityType.END_CRYSTAL, world);
            endCrystal.setShowBottom(false);

            // Adjust its position
            endCrystal.setPosition(offset.x, offset.y, offset.z);

            float scale = 0.75F;
            matrices.scale(scale, scale, scale);

            // Center the crystal after scaling
            matrices.translate(0.5 * (1 - scale), 0.5 * (1 - scale), 0.5 * (1 - scale));

            // Set the inner rotation to a smooth value based on world time
            long normalizedTime = world.getTime() % 24000;
            endCrystal.endCrystalAge = (int) normalizedTime; // Increment smoothly

            // Get the EntityRenderDispatcher
            EntityRenderDispatcher dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();

            // Render the entity
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

    private void renderEntityFromSpawnEgg(SpawnEggItem spawnEggItem, Vec3d offset, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, World world, float yRot, float tickDelta, ItemStack stack) {
        matrices.push();

        DynamicRegistryManager registryAccess = world.getRegistryManager();
        RegistryWrapper.WrapperLookup wrapperLookup = RegistryWrapper.WrapperLookup.of(registryAccess.stream().distinct());

        // Get the entity type from the spawn egg
        EntityType<?> entityType = spawnEggItem.getEntityType(wrapperLookup, spawnEggItem.getDefaultStack());

        // Create the entity
        Entity entity = entityType.create(world, SpawnReason.MOB_SUMMONED);
        if (entity != null) {
            // Step 1: Translate to the pedestal's center
            matrices.translate(offset.x, offset.y, offset.z);

            // Step 2: Apply rotation around the Y-axis
            matrices.translate(0, entity.getHeight() / 2.0, 0); // Adjust for the entity's visual height
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yRot));

            if (stack.get(DataComponentTypes.CUSTOM_NAME) != null) {
                entity.setCustomName(stack.get(DataComponentTypes.CUSTOM_NAME));
            }
            matrices.translate(0, -entity.getHeight() / 2.0, 0); // Revert the height adjustment after rotation

            // Step 3: Position the entity correctly at the offset
            entity.setPos(offset.x, offset.y, offset.z);

            long normalizedTime = world.getTime() % 24000;
            entity.age = (int) normalizedTime;
            entity.setId(-1);

            // Get the EntityRenderDispatcher
            EntityRenderDispatcher dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();

            // Render the entity with the corrected transformations
            ((EntityRenderDispatcherAccessor) dispatcher).invokeRender(
                    entity,
                    0.0, // Render at the origin since we translated the matrices
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
