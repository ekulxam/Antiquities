package net.hollowed.antique.blocks.entities;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hollowed.antique.Antiquities;
import net.hollowed.antique.index.AntiqueBlockEntities;
import net.hollowed.antique.networking.PedestalPacketPayload;
import net.hollowed.antique.util.interfaces.ImplementedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentHolder;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PedestalBlockEntity extends BlockEntity implements ComponentHolder, SidedInventory, ImplementedInventory {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public PedestalBlockEntity(BlockPos pos, BlockState state) {
        super(AntiqueBlockEntities.PEDESTAL_BLOCK_ENTITY, pos, state);
    }

    // Handles dropping the contents when the block is broken
    public void drops() {
        if (world != null) {
            for (ItemStack stack : items) {
                if (!stack.isEmpty()) {
                    ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                    world.spawnEntity(itemEntity);
                }
            }
        }
    }

    @Override
    protected void writeData(WriteView view) {
        Inventories.writeData(view, items);
        super.writeData(view);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        Inventories.readData(view, items);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        ErrorReporter.Logging logging = new ErrorReporter.Logging(this.getReporterContext(), Antiquities.LOGGER);

        NbtCompound var4;
        try {
            NbtWriteView nbtWriteView = NbtWriteView.create(logging, registries);
            Inventories.writeData(nbtWriteView, items, true);
            var4 = nbtWriteView.getNbt();
        } catch (Throwable var6) {
            try {
                logging.close();
            } catch (Throwable var5) {
                var6.addSuppressed(var5);
            }

            throw var6;
        }

        logging.close();
        return var4;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        assert world != null;
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            serverWorld.getChunkManager().markForUpdate(pos);
        }
    }

    public static void tick(World world, PedestalBlockEntity blockEntity) {
        if (world.isClient()) {
            return;
        }

        blockEntity.markDirty();
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        items.set(slot, stack);
        markDirty();

        if (world instanceof ServerWorld serverWorld) {
            serverWorld.getPlayers().forEach(serverPlayer -> ServerPlayNetworking.send(serverPlayer, new PedestalPacketPayload(this.pos, stack)));
            serverWorld.getChunkManager().markForUpdate(pos);
        }
    }

    @Override
    public ItemStack removeStack(int slot, int count) {
        ItemStack stack = Inventories.splitStack(getItems(), slot, count);
        if (!stack.isEmpty()) {
            markDirty();
        }

        setStack(slot, Items.AIR.getDefaultStack());

        return stack;
    }



    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[]{0};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return items.get(slot).isEmpty() && stack.getCount() > 0;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return !items.get(slot).isEmpty();
    }
}
