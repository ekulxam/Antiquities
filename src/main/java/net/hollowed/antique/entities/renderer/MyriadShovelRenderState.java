package net.hollowed.antique.entities.renderer;

import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public class MyriadShovelRenderState extends EntityRenderState {
    public Entity entity;
    public ItemStack stack;
    public int color;
    public boolean isEnchanted;
    public String cloth;
}