package net.hollowed.antique.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.Arrays;

public class SatchelInventoryComponent {

    private ItemStack[] inventory;
    private int selectedStack;

    public static final Codec<SatchelInventoryComponent> CODEC = ItemStack.CODEC.listOf().flatXmap(stack -> DataResult.success(new SatchelInventoryComponent(stack.toArray(new ItemStack[0]), 0)), component -> DataResult.success(Arrays.asList(component.getInventory())));
    public static final PacketCodec<RegistryByteBuf, SatchelInventoryComponent> PACKET_CODEC = ItemStack.PACKET_CODEC.collect(PacketCodecs.toList()).xmap(stacks -> new SatchelInventoryComponent(stacks.toArray(new ItemStack[0])), component -> Arrays.asList(component.getInventory()));

    SatchelInventoryComponent(ItemStack[] inventory, int selectedStack) {
        this.inventory = new ItemStack[8];
        this.selectedStack = selectedStack;
    }

    public SatchelInventoryComponent(ItemStack[] inventory) {
        this(inventory, 0);
    }

    public ItemStack getStack(int slot) {
        return inventory[slot];
    }

    public void setStack(int slot, ItemStack stack) {
        this.inventory[slot] = stack;
    }

    public int getSelectedStack() {
        return selectedStack;
    }

    public void setSelectedStack(int selectedStack) {
        this.selectedStack = selectedStack;
    }

    public ItemStack[] getInventory() {
        return inventory;
    }

    public void removeStack(int slot) {
        this.inventory[slot] = ItemStack.EMPTY;
    }

}
