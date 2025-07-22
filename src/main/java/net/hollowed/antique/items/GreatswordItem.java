package net.hollowed.antique.items;

import net.hollowed.antique.index.AntiqueToolMaterial;
import net.hollowed.antique.util.interfaces.duck.ShieldPiercer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GreatswordItem extends Item implements ShieldPiercer {
    private final float shieldPierce;

    public GreatswordItem(AntiqueToolMaterial material, float attackDamage, float attackSpeed, float shieldPierce, float reach, Settings settings) {
        super(material.applyGreatswordSettings(settings, attackDamage, attackSpeed, reach));
        this.shieldPierce = shieldPierce;
    }

    @Override
    public boolean canMine(ItemStack stack, BlockState state, World world, BlockPos pos, LivingEntity user) {
        return !user.isInCreativeMode();
    }

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, EquipmentSlot.MAINHAND);
    }

    @Override
    public float shieldPierce() {
        return this.shieldPierce;
    }
}
