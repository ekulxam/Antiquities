package net.hollowed.antique.blocks.entities.renderer;

import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class PedestalRenderState extends BlockEntityRenderState {

    public ItemStack storedStack;
    public long worldTime;
    public World world;

    public PedestalRenderState() {

    }
}
