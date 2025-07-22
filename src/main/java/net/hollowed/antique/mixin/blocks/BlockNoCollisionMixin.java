/*
    This class is under an MIT License - some pieces of Origins (fabric) were used to make this function

    Copyright 2025 HollowedWanderer

    Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
    files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge,
    publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished
    to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
    OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
    BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
    IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.hollowed.antique.mixin.blocks;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.hollowed.antique.index.AntiqueEffects;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class BlockNoCollisionMixin {

    @Inject(method = "getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", at = @At("RETURN"), cancellable = true)
    private void getCollision(BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        Entity entity;
        if ((context instanceof EntityShapeContext shapeContext) && (entity = shapeContext.getEntity()) != null && !cir.getReturnValue().isEmpty()) {
            if (entity instanceof LivingEntity living && living.hasStatusEffect(AntiqueEffects.ANIME_EFFECT) && world.getBlockState(pos).getBlock().getBlastResistance() < 500) {
                cir.setReturnValue(VoxelShapes.empty());
            }
        }
    }

    @WrapWithCondition(method = "onEntityCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onEntityCollision(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/EntityCollisionHandler;)V"))
    private boolean onEntityCollision(Block instance, BlockState blockState, World world, BlockPos blockPos, Entity entity, EntityCollisionHandler entityCollisionHandler) {
        return !(entity instanceof LivingEntity living && living.hasStatusEffect(AntiqueEffects.ANIME_EFFECT) && world.getBlockState(blockPos).getBlock().getBlastResistance() < 500);
    }
}
