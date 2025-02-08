package net.hollowed.antique.mixin;

import net.hollowed.antique.enchantments.EnchantmentListener;
import net.hollowed.antique.util.Crawl;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class CrawlLogicMixin extends LivingEntity implements Crawl {

    @Shadow public abstract boolean isSwimming();

    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    protected CrawlLogicMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private static final TrackedData<Boolean> CRAWLING =
            DataTracker.registerData(CrawlLogicMixin.class, TrackedDataHandlerRegistry.BOOLEAN);
    @Unique
    private static final TrackedData<Integer> SLIDE_TIMER =
            DataTracker.registerData(CrawlLogicMixin.class, TrackedDataHandlerRegistry.INTEGER);
    @Unique
    private static final TrackedData<Integer> SLIDE_COOLDOWN =
            DataTracker.registerData(CrawlLogicMixin.class, TrackedDataHandlerRegistry.INTEGER);

    @Inject(method = "initDataTracker", at = @At("HEAD"))
    protected void initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(CRAWLING, false);
        builder.add(SLIDE_TIMER, 0);
        builder.add(SLIDE_COOLDOWN, 0);
    }

    @Override
    public void antique$setCrawl(boolean crawling) {
        this.dataTracker.set(CRAWLING, crawling);
    }

    @Override
    public void antique$setCrawlStart(int time) {
        if (this.dataTracker.get(SLIDE_COOLDOWN) <= 0) {
            this.dataTracker.set(SLIDE_TIMER, time);
            this.dataTracker.set(SLIDE_COOLDOWN, time + 20);
        }
    }

    @Inject(method = "isSwimming", at = @At("RETURN"), cancellable = true)
    public void swimming(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() || this.dataTracker.get(CRAWLING));
    }

    @Inject(method = "tickMovement", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (this.dataTracker.get(SLIDE_TIMER) > 0 && this.isSwimming()) {
            if (this.dataTracker.get(SLIDE_TIMER) < 9) {
                this.addVelocity(this.getRotationVec(0).getHorizontal().normalize().multiply(this.isOnGround() ? EnchantmentListener.hasCustomEnchantment(this.getEquippedStack(EquipmentSlot.LEGS), "minecraft:swift_sneak") ? 0.55 : 0.3 : 0));
                if (this.getVelocity().length() < 0.2) {
                    this.dataTracker.set(SLIDE_TIMER, 0);
                }
            } else {
                this.addVelocity(this.getRotationVec(0).getHorizontal().normalize().multiply(this.isOnGround() ? 0.05 : 0));
            }

            this.velocityModified = true;
        }
        if (this.dataTracker.get(SLIDE_TIMER) > 0) {
            this.dataTracker.set(SLIDE_TIMER, this.dataTracker.get(SLIDE_TIMER) - 1);
        }
        if (this.dataTracker.get(SLIDE_COOLDOWN) > 0) {
            this.dataTracker.set(SLIDE_COOLDOWN, this.dataTracker.get(SLIDE_COOLDOWN) - 1);
        }
    }

    @Override
    public void jump() {
        super.jump();
        if (this.dataTracker.get(SLIDE_TIMER) > 0 && this.dataTracker.get(SLIDE_TIMER) < 4) {
            this.dataTracker.set(CRAWLING, false);
            this.addVelocity(this.getRotationVec(0).getHorizontal().normalize().multiply(0.25).add(0, 0.2, 0));
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_GOAT_LONG_JUMP, SoundCategory.PLAYERS, 1.0F, 1.0F);
            this.velocityModified = true;
            this.dataTracker.set(SLIDE_TIMER, 0);
        }
    }
}
