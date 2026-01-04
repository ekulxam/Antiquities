package net.hollowed.antique.entities;

import net.hollowed.antique.index.AntiqueItems;
import net.hollowed.antique.index.AntiqueSounds;
import net.hollowed.antique.util.delay.TickDelayScheduler;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class SmokeBombEntity extends ThrowableItemProjectile {
    public SmokeBombEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return AntiqueItems.SMOKE_BOMB;
    }

    @Override
    protected void onHit(HitResult hitResult) {
        HitResult.Type type = hitResult.getType();
        if (type != HitResult.Type.ENTITY) {
            super.onHit(hitResult);
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        Level world = this.level();

        world.playSound(null, this.getX(), this.getY(), this.getZ(), AntiqueSounds.FIRECRACKER, SoundSource.BLOCKS, 3F, (float) ((Math.random() * 0.5) + 0.75));
        world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.5F, 1.5F);
        if (world instanceof ServerLevel serverWorld) {
            Fireworks fireworks = this.getItem().get(DataComponents.FIREWORKS);

            if (fireworks != null) {
                ItemStack stack = Items.FIREWORK_ROCKET.getDefaultInstance();
                stack.set(DataComponents.FIREWORKS, new Fireworks(-3, fireworks.explosions()));

                FireworkRocketEntity projectile = new FireworkRocketEntity(world, null, this.getX(), this.getY() + 1, this.getZ(), stack);
                projectile.setSilent(true);
                serverWorld.addFreshEntity(projectile);
            }

            double raisedY = this.getY() + 1;
            serverWorld.sendParticles(ColorParticleOption.create(ParticleTypes.FLASH, 0xFFFFFFFF), true, true, this.getX(), raisedY, this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
            serverWorld.sendParticles(ParticleTypes.LARGE_SMOKE, true, true, this.getX(), raisedY, this.getZ(), 64, 0.5, 0.5, 0.5, 0.1);
            for (int i = 1; i < 4; i++) {
                int finalI = i;
                TickDelayScheduler.schedule(3 * i, () -> serverWorld.sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, true, true, this.getX(), raisedY, this.getZ(), 64 * finalI, 1, 1, 1, 0.0025));
            }
        }
        this.discard();
    }
}
