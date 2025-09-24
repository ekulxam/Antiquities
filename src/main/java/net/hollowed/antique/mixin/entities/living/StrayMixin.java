package net.hollowed.antique.mixin.entities.living;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.util.Hand;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StrayEntity.class)
public abstract class StrayMixin extends AbstractSkeletonEntity {

    protected StrayMixin(EntityType<? extends AbstractSkeletonEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        super.initEquipment(random, localDifficulty);
        ItemStack bow = Items.BOW.getDefaultStack();
        bow.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.of(Items.TIPPED_ARROW.getDefaultStack()));
        bow.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(new PotionContentsComponent(Potions.SLOWNESS).getColor()));
        this.setStackInHand(Hand.MAIN_HAND, bow);
    }
}
