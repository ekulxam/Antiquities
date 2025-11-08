package net.hollowed.antique.mixin.worldgen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.hollowed.antique.worldgen.features.AntiqueOreVeinSampler;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.noise.NoiseRouter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(ChunkNoiseSampler.class)
public abstract class ChunkNoiseSamplerMixin {

    @Shadow protected abstract DensityFunction getActualDensityFunction(DensityFunction function);

    @Unique
    private ArrayList<ChunkNoiseSampler.BlockStateSampler> list;

    @WrapOperation(method = "<init>", at = @At(value = "NEW", target = "()Ljava/util/ArrayList;"))
    private ArrayList<ChunkNoiseSampler.BlockStateSampler> getList(Operation<ArrayList<ChunkNoiseSampler.BlockStateSampler>> original) {
        this.list = original.call();
        return list;
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 1))
    private void addToList(int horizontalCellCount, NoiseConfig noiseConfig, int startBlockX, int startBlockZ, GenerationShapeConfig generationShapeConfig, DensityFunctionTypes.Beardifying beardifying, ChunkGeneratorSettings chunkGeneratorSettings, AquiferSampler.FluidLevelSampler fluidLevelSampler, Blender blender, CallbackInfo ci) {
        NoiseRouter noiseRouter = noiseConfig.getNoiseRouter();
        NoiseRouter noiseRouter2 = noiseRouter.apply(this::getActualDensityFunction);

        if (chunkGeneratorSettings.oreVeins()) {
            list.add(AntiqueOreVeinSampler.create(noiseRouter2.veinToggle(), noiseRouter2.veinRidged(), noiseRouter2.veinGap(), noiseConfig.getOreRandomDeriver()));
        }
    }
}
