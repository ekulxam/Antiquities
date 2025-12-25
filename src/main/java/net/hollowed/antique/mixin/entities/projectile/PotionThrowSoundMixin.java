package net.hollowed.antique.mixin.entities.projectile;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ThrowablePotionItem.class)
public class PotionThrowSoundMixin {

    @Inject(method = "use", at = @At("RETURN"))
    public void use(Level world, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        player.level().playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.WITCH_THROW, SoundSource.PLAYERS, 0.8F, 1.0F);
    }
}
