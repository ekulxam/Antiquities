package net.hollowed.antique.items.custom.myriadTool;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public abstract class MyriadToolBitItem extends ShearsItem {

    public MyriadToolBitItem(Settings settings) {
        super(settings);
    }

    public boolean toolOnStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        return false;
    }

    public UseAction toolGetUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    public ActionResult toolUse(World world, PlayerEntity user, Hand hand) {
        return ActionResult.FAIL;
    }

    public void setToolAttributes(ItemStack tool) {

    }

    public ActionResult toolUseOnBlock(ItemUsageContext context) {
        return ActionResult.FAIL;
    }
}
