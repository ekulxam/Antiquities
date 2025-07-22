package net.hollowed.antique.mixin.entities.living;

import net.hollowed.antique.Antiquities;
import net.hollowed.antique.index.AntiqueItems;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class EntityLootMixin extends Entity {

    @Unique
    private static final TagKey<EntityType<?>> FORCE_LOOT = TagKey.of(Registries.ENTITY_TYPE.getKey(), Identifier.of(Antiquities.MOD_ID, "force_loot"));

    @Shadow protected int playerHitTimer;

    public EntityLootMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow protected abstract boolean shouldDropLoot();

    @Shadow protected abstract void dropLoot(ServerWorld world, DamageSource damageSource, boolean causedByPlayer);

    @Shadow protected abstract void dropEquipment(ServerWorld world, DamageSource source, boolean causedByPlayer);

    @Shadow protected abstract void dropExperience(ServerWorld world, @Nullable Entity attacker);

    @Shadow @Nullable public abstract ItemEntity dropItem(ItemStack stack, boolean dropAtSelf, boolean retainOwnership);

    @Inject(method = "drop", at = @At("HEAD"))
    public void drop(ServerWorld world, DamageSource damageSource, CallbackInfo ci) {
        if (damageSource.getAttacker() != null && damageSource.getAttacker().getWeaponStack() != null
                && damageSource.getAttacker().getWeaponStack().isOf(AntiqueItems.MYRIAD_CLEAVER_BLADE)
        ) {
            if (!this.getType().isIn(FORCE_LOOT)) {
                boolean bl = this.playerHitTimer > 0;
                if (this.shouldDropLoot() && world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                    this.dropLoot(world, damageSource, bl);
                    this.dropEquipment(world, damageSource, bl);
                }

                this.dropExperience(world, damageSource.getAttacker());
            }
            EntityType<?> type = this.getType();
            if (type.equals(EntityType.SKELETON) && Math.random() > 0.8) this.dropItem(world, Items.SKELETON_SKULL);
            if (type.equals(EntityType.CREEPER) && Math.random() > 0.8) this.dropItem(world, Items.CREEPER_HEAD);
            if (type.equals(EntityType.ZOMBIE) && Math.random() > 0.8) this.dropItem(world, Items.ZOMBIE_HEAD);
            if (type.equals(EntityType.WITHER_SKELETON) && Math.random() > 0.8) this.dropItem(world, Items.WITHER_SKELETON_SKULL);
            if (type.equals(EntityType.PIGLIN) && Math.random() > 0.8) this.dropItem(world, Items.PIGLIN_HEAD);
            if ((LivingEntity) (Object) this instanceof PlayerEntity player) {
                ItemStack stack = Items.PLAYER_HEAD.getDefaultStack();
                stack.set(DataComponentTypes.PROFILE, new ProfileComponent(player.getGameProfile()));
                this.dropItem(stack, true, false);
            }
        }
    }
}
