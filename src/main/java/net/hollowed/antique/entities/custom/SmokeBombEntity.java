package net.hollowed.antique.entities.custom;

import net.hollowed.antique.ModSounds;
import net.hollowed.antique.util.TickDelayScheduler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class SmokeBombEntity extends ThrownItemEntity {
    public SmokeBombEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.SNOWBALL;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        HitResult.Type type = hitResult.getType();
        if (type != HitResult.Type.ENTITY) {
            super.onCollision(hitResult);
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        World world = this.getWorld();

        ItemStack stack = Items.FIREWORK_ROCKET.getDefaultStack();
        stack.set(DataComponentTypes.FIREWORKS, IllusionerArrowEntity.randomFireworkBall());

        world.playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.FIRECRACKER, SoundCategory.BLOCKS, 1F, (float) ((Math.random() * 0.5) + 0.75));
        world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.75F, 1.5F);
        if (world instanceof ServerWorld serverWorld) {
            FireworkRocketEntity projectile = new FireworkRocketEntity(world, null, this.getX(), this.getY() + 1, this.getZ(), stack);
            projectile.setSilent(true);
            projectile.velocityModified = true;
            serverWorld.spawnEntity(projectile);

            serverWorld.getServer().getCommandManager().execute(
                    serverWorld.getServer().getCommandManager().getDispatcher().parse("particle minecraft:large_smoke " + this.getX() + " " + this.getY() + 1 + " " + this.getZ() + " 0.5 0.5 0.5 0.1 64 force", serverWorld.getServer().getCommandSource()
                            .withSilent()
                    ),
                    "particle minecraft:large_smoke " + this.getX() + " " + this.getY() + 1 + " " + this.getZ() + " 0.5 0.5 0.5 0.1 64 force"
            );
            for (int i = 1; i < 4; i++) {
                int finalI = i;
                TickDelayScheduler.schedule(3 * i, () -> serverWorld.getServer().getCommandManager().execute(
                        serverWorld.getServer().getCommandManager().getDispatcher().parse("particle minecraft:campfire_signal_smoke " + this.getX() + " " + this.getY() + 1 + " " + this.getZ() + " 1 1 1 0.005 " + 64 * finalI + " force", serverWorld.getServer().getCommandSource()
                                .withSilent()
                        ),
                        "particle minecraft:campfire_signal_smoke " + this.getX() + " " + this.getY() + 1 + " " + this.getZ() + " 1 1 1 0.005 " + 64 * finalI + " force"
                ));
            }
        }
        this.discard();
    }
}
