package net.hollowed.antique.items;

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

    /**
     * A method that allows the tool bits to set the attributes of the Myriad Tool they are placed in
     *
     * @param stack - the ItemStack of the Myriad Tool
     */
    public abstract void setToolAttributes(ItemStack stack);

    /**
     * Gets called when the Myriad Tool hosting the tool bit has stopped being used
     *
     * @param stack - the ItemStack from Item's onStoppedUsing, in this case it is the Myriad Tool
     * @param world - the World from Item's onStoppedUsing
     * @param user - the LivingEntity from Item's onStoppedUsing
     * @param remainingUseTicks - the remainingUseTicks from Item's onStoppedUsing
     * @return - the same return style as Item's onStoppedUsing
     */
    public boolean toolOnStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        return false;
    }

    /**
     * Determines the use action of the Myriad Tool hosting the tool bit
     *
     * @param stack - the ItemStack from Item's getUseAction
     * @return - the same return style as Item's getUseAction
     */
    public UseAction toolGetUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    /**
     * Gets called the Myriad Tool hosting the tool bit is used
     *
     * @param world - the World from Item's use method
     * @param user - the PlayerEntity from Item's use method
     * @param hand - the Hand from Item's use method
     * @return - the same return style as Item's use method
     */
    public ActionResult toolUse(World world, PlayerEntity user, Hand hand) {
        return ActionResult.PASS;
    }

    /**
     *
     * @param context - the ItemUsageContext from Item's useOnBlock
     * @return - the same return style as Item's useOnBlock
     */
    public ActionResult toolUseOnBlock(ItemUsageContext context) {
        return ActionResult.PASS;
    }
}