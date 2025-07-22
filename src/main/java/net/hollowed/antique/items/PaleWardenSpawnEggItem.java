package net.hollowed.antique.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;

public class PaleWardenSpawnEggItem extends SpawnEggItem {
    public PaleWardenSpawnEggItem(EntityType<? extends MobEntity> type, Item.Settings settings) {
        super(type, settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        ItemStack itemStack = context.getStack();
        BlockPos blockPos = context.getBlockPos();
        Direction direction = context.getSide();
        BlockPos spawnPos = blockPos.offset(direction);
        EntityType<?> entityType = this.getEntityType(world.getRegistryManager(), itemStack);

        // Spawn entity and apply modifications
        if (world instanceof ServerWorld serverWorld) {
            Entity entity = entityType.spawnFromItemStack(
                    serverWorld,
                    itemStack,
                    context.getPlayer(),
                    spawnPos,
                    SpawnReason.SPAWN_ITEM_USE,
                    true,
                    false
            );

            if (entity instanceof MobEntity mobEntity) {
                // Disable AI
                mobEntity.setAiDisabled(true);

                // Snap entity rotation to face the player
                final float snappedYaw = getSnappedYaw(context, entity);
                entity.setYaw(snappedYaw);
                entity.setHeadYaw(snappedYaw);
                entity.setPitch(0); // Ensure no vertical pitch
            }

            itemStack.decrement(1);
            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }

    private static float getSnappedYaw(ItemUsageContext context, Entity entity) {
        Vec3d playerPos = Objects.requireNonNull(context.getPlayer()).getEyePos();
        Vec3d entityPos = entity.getPos();
        double dx = playerPos.x - entityPos.x;
        double dz = playerPos.z - entityPos.z;
        double angle = Math.atan2(-dz, -dx) * (180.0 / Math.PI) + 90.0;

        // Snap angle to nearest 90 degrees
        return (float) Math.round(angle / 45.0) * 45;
    }
}
