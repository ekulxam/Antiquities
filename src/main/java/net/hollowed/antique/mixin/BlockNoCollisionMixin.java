package net.hollowed.antique.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.util.EntityAnimeActivator;
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
            if (entity instanceof LivingEntity living && living.hasStatusEffect(Antiquities.ANIME_EFFECT)) {
                cir.setReturnValue(VoxelShapes.empty());
            }
        }
    }

    @WrapWithCondition(method = "onEntityCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onEntityCollision(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/EntityCollisionHandler;)V"))
    private boolean onEntityCollision(Block instance, BlockState blockState, World world, BlockPos blockPos, Entity entity, EntityCollisionHandler entityCollisionHandler) {
        return (entity instanceof LivingEntity living && living.hasStatusEffect(Antiquities.ANIME_EFFECT));
    }
}
