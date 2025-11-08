package net.hollowed.antique.mixin.entities.living;

import net.hollowed.antique.index.AntiqueEntities;
import net.hollowed.antique.entities.IllusionerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.Difficulty;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Raid.class)
public abstract class RaidModifierMixin {

    @Shadow private int wavesSpawned;

    @Shadow public abstract int getMaxWaves(Difficulty difficulty);

    @Shadow public abstract void addRaider(ServerWorld world, int wave, RaiderEntity raider, @Nullable BlockPos pos, boolean existing);

    @Inject(method = "spawnNextWave", at = @At("HEAD"))
    public void spawnNextWave(ServerWorld world, BlockPos pos, CallbackInfo ci) {
        int i = this.wavesSpawned;
        IllusionerEntity raiderEntity = new IllusionerEntity(AntiqueEntities.ILLUSIONER, world);
        raiderEntity.setStackInHand(Hand.MAIN_HAND, Items.BOW.getDefaultStack());
        switch (world.getDifficulty()) {
            case Difficulty.EASY -> {
                if (i == this.getMaxWaves(Difficulty.EASY)) {
                    this.addRaider(world, i, raiderEntity, pos, false);
                }
            }
            case Difficulty.NORMAL -> {
                if (i == this.getMaxWaves(Difficulty.NORMAL)) {
                    this.addRaider(world, i, raiderEntity, pos, false);
                }
            }
            case Difficulty.HARD -> {
                if (i == this.getMaxWaves(Difficulty.HARD)) {
                    this.addRaider(world, i, raiderEntity, pos, false);
                }
            }
        }
    }
}
