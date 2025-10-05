package net.hollowed.antique.mixin.entities.projectile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ThrowablePotionItem.class)
public class PotionThrowSoundMixin {

    @Inject(method = "use", at = @At("RETURN"))
    public void use(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        player.getEntityWorld().playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_WITCH_THROW, SoundCategory.PLAYERS, 0.8F, 1.0F);
    }
}
