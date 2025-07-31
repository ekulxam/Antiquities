package net.hollowed.antique.entities;

import com.mojang.serialization.Codec;
import net.hollowed.antique.index.AntiqueItems;
import net.hollowed.antique.index.AntiqueSounds;
import net.hollowed.antique.util.FireworkUtil;
import net.hollowed.antique.util.delay.TickDelayScheduler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class SmokeBombEntity extends ThrownItemEntity {
    private static final TrackedData<Boolean> FIREWORK = DataTracker.registerData(SmokeBombEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public SmokeBombEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected Item getDefaultItem() {
        return AntiqueItems.SMOKE_BOMB;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(FIREWORK, false);
    }

    @Override
    public void readData(ReadView view) {
        super.readData(view);
        this.dataTracker.set(FIREWORK, view.getBoolean("firework", false));
    }

    public void setFirework(boolean firework) {
        this.dataTracker.set(FIREWORK, firework);
    }

    @Override
    public void writeData(WriteView view) {
        super.writeData(view);
        view.put("firework", Codec.BOOL, this.dataTracker.get(FIREWORK));
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
        stack.set(DataComponentTypes.FIREWORKS, FireworkUtil.randomFireworkBall());

        world.playSound(null, this.getX(), this.getY(), this.getZ(), AntiqueSounds.FIRECRACKER, SoundCategory.BLOCKS, 3F, (float) ((Math.random() * 0.5) + 0.75));
        world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.5F, 1.5F);
        if (world instanceof ServerWorld serverWorld) {
            if (this.dataTracker.get(FIREWORK)) {
                FireworkRocketEntity projectile = new FireworkRocketEntity(world, null, this.getX(), this.getY() + 1, this.getZ(), stack);
                projectile.setSilent(true);
                serverWorld.spawnEntity(projectile);
            }

            double raisedY = this.getY() + 1;
            serverWorld.spawnParticles(ParticleTypes.FLASH, true, true, this.getX(), raisedY, this.getZ(), 1, 0, 0, 0, 0);
            serverWorld.spawnParticles(ParticleTypes.LARGE_SMOKE, true, true, this.getX(), raisedY, this.getZ(), 64, 0.5, 0.5, 0.5, 0.1);
            for (int i = 1; i < 4; i++) {
                int finalI = i;
                TickDelayScheduler.schedule(3 * i, () -> serverWorld.spawnParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, true, true, this.getX(), raisedY, this.getZ(), 64 * finalI, 1, 1, 1, 0.0025));
            }
        }
        this.discard();
    }
}
