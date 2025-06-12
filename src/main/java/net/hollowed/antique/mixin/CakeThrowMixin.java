package net.hollowed.antique.mixin;

import net.hollowed.antique.entities.ModEntities;
import net.hollowed.antique.entities.custom.CakeEntity;
import net.hollowed.combatamenities.mixin.tweaks.fireCharge.FireChargeItemMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class CakeThrowMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void use(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (player.getStackInHand(hand).isOf(Items.CAKE)) {
            ItemStack stack = player.getStackInHand(hand);
            player.swingHand(hand, true);
            if (!world.isClient) {
                CakeEntity cake = new CakeEntity(ModEntities.CAKE_ENTITY, world);
                cake.setPos(player.getX(), player.getY() + 1.5, player.getZ());
                cake.setVelocity(player.getRotationVector().multiply(0.75));
                cake.setAngles(-player.getHeadYaw(), -player.getPitch());
                world.spawnEntity(cake);

                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_WIND_CHARGE_THROW, SoundCategory.NEUTRAL, 0.5F, 0.1F);
            }
            stack.decrementUnlessCreative(1, player);
            player.incrementStat(Stats.USED.getOrCreateStat((Item) (Object) this));
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}
