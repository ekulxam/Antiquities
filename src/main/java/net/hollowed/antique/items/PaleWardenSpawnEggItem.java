package net.hollowed.antique.items;

import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class PaleWardenSpawnEggItem extends SpawnEggItem {
    private final EntityType<? extends Mob> type;

    public PaleWardenSpawnEggItem(EntityType<? extends Mob> type, Item.Properties settings) {
        super(settings);
        this.type = type;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ItemStack itemStack = context.getItemInHand();
        BlockPos blockPos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        BlockPos spawnPos = blockPos.relative(direction);
        EntityType<?> entityType = this.getType(itemStack);

        // Spawn entity and apply modifications
        if (world instanceof ServerLevel serverWorld) {
            assert entityType != null;
            Entity entity = entityType.spawn(
                    serverWorld,
                    itemStack,
                    context.getPlayer(),
                    spawnPos,
                    EntitySpawnReason.SPAWN_ITEM_USE,
                    true,
                    false
            );

            if (entity instanceof Mob mobEntity) {
                // Disable AI
                mobEntity.setNoAi(true);

                // Snap entity rotation to face the player
                final float snappedYaw = getSnappedYaw(context, entity);
                entity.setYRot(snappedYaw);
                entity.setYHeadRot(snappedYaw);
                entity.setXRot(0); // Ensure no vertical pitch
            }

            itemStack.shrink(1);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    private static float getSnappedYaw(UseOnContext context, Entity entity) {
        Vec3 playerPos = Objects.requireNonNull(context.getPlayer()).getEyePosition();
        Vec3 entityPos = entity.position();
        double dx = playerPos.x - entityPos.x;
        double dz = playerPos.z - entityPos.z;
        double angle = Math.atan2(-dz, -dx) * (180.0 / Math.PI) + 90.0;

        // Snap angle to nearest 90 degrees
        return (float) Math.round(angle / 45.0) * 45;
    }
}
