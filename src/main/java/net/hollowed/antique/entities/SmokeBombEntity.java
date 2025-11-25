package net.hollowed.antique.entities;

import net.hollowed.antique.index.AntiqueItems;
import net.hollowed.antique.index.AntiqueSounds;
import net.hollowed.antique.util.delay.TickDelayScheduler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.TintedParticleEffect;
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
        return AntiqueItems.SMOKE_BOMB;
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
        World world = this.getEntityWorld();

        world.playSound(null, this.getX(), this.getY(), this.getZ(), AntiqueSounds.FIRECRACKER, SoundCategory.BLOCKS, 3F, (float) ((Math.random() * 0.5) + 0.75));
        world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.5F, 1.5F);
        if (world instanceof ServerWorld serverWorld) {
            FireworksComponent fireworks = this.getStack().get(DataComponentTypes.FIREWORKS);

            if (fireworks != null) {
                ItemStack stack = Items.FIREWORK_ROCKET.getDefaultStack();
                stack.set(DataComponentTypes.FIREWORKS, new FireworksComponent(-3, fireworks.explosions()));

                FireworkRocketEntity projectile = new FireworkRocketEntity(world, null, this.getX(), this.getY() + 1, this.getZ(), stack);
                projectile.setSilent(true);
                serverWorld.spawnEntity(projectile);
            }

            double raisedY = this.getY() + 1;
            serverWorld.spawnParticles(TintedParticleEffect.create(ParticleTypes.FLASH, 0xFFFFFFFF), true, true, this.getX(), raisedY, this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
            serverWorld.spawnParticles(ParticleTypes.LARGE_SMOKE, true, true, this.getX(), raisedY, this.getZ(), 64, 0.5, 0.5, 0.5, 0.1);
            for (int i = 1; i < 4; i++) {
                int finalI = i;
                TickDelayScheduler.schedule(3 * i, () -> serverWorld.spawnParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, true, true, this.getX(), raisedY, this.getZ(), 64 * finalI, 1, 1, 1, 0.0025));
            }
        }
        this.discard();
    }
}
