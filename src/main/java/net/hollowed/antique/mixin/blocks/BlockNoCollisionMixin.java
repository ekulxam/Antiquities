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
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockNoCollisionMixin {

    @Inject(method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At("RETURN"), cancellable = true)
    private void getCollision(BlockGetter world, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        Entity entity;
        if ((context instanceof EntityCollisionContext shapeContext) && (entity = shapeContext.getEntity()) != null && !cir.getReturnValue().isEmpty()) {
            if (entity instanceof LivingEntity living && living.hasEffect(AntiqueEffects.ANIME_EFFECT) && world.getBlockState(pos).getBlock().getExplosionResistance() < 500) {
                cir.setReturnValue(Shapes.empty());
            }
        }
    }

    @WrapWithCondition(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;entityInside(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/InsideBlockEffectApplier;Z)V"))
    private boolean onEntityCollision(Block instance, BlockState blockState, Level world, BlockPos blockPos, Entity entity, InsideBlockEffectApplier entityCollisionHandler, boolean b) {
        return !(entity instanceof LivingEntity living && living.hasEffect(AntiqueEffects.ANIME_EFFECT) && world.getBlockState(blockPos).getBlock().getExplosionResistance() < 500);
    }
}
