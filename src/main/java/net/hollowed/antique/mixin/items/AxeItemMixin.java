package net.hollowed.antique.mixin.items;

import net.hollowed.antique.util.BlockUtil;
import net.hollowed.antique.index.AntiqueParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.particle.ParticleUtil;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(AxeItem.class)
public class AxeItemMixin {

    @Inject(method = "tryStrip", at = @At("HEAD"), cancellable = true)
    private void tryStrip(World world, BlockPos pos, @Nullable PlayerEntity player, BlockState state, CallbackInfoReturnable<Optional<BlockState>> cir) {
        Optional<BlockState> optional = getPreviousTarnishLevel(state);
        if (optional.isPresent()) {
            world.playSound(player, pos, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            ParticleUtil.spawnParticle(world, pos, AntiqueParticles.SCRAPE, UniformIntProvider.create(3, 5));
            cir.setReturnValue(optional);
        } else {
            Optional<BlockState> optional2 = getUncoat(state);
            if (optional2.isPresent()) {
                world.playSound(player, pos, SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.syncWorldEvent(player, WorldEvents.WAX_REMOVED, pos, 0);
                cir.setReturnValue(optional2);
            }
        }
    }

    @Unique
    private Optional<BlockState> getPreviousTarnishLevel(BlockState state) {
        return Optional.ofNullable(BlockUtil.TARNISHING_BLOCKS_REVERSE.get(state.getBlock()))
                .map(Block::getDefaultState);
    }

    @Unique
    private Optional<BlockState> getUncoat(BlockState state) {
        return Optional.ofNullable(BlockUtil.COATED_MYRIAD_BLOCKS_REVERSE.get(state.getBlock()))
                .map(Block::getDefaultState);
    }
}
