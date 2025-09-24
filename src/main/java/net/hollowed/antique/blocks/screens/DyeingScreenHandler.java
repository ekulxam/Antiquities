package net.hollowed.antique.blocks.screens;

import net.hollowed.antique.index.AntiqueDataComponentTypes;
import net.hollowed.antique.index.AntiqueItems;
import net.hollowed.antique.index.AntiqueScreenHandlerType;
import net.hollowed.antique.util.resources.ClothSkinData;
import net.hollowed.antique.util.resources.ClothSkinListener;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;

public class DyeingScreenHandler extends ScreenHandler {
	private final ScreenHandlerContext context;
	@Nullable
	private String hexCode;
	public final Inventory inventory = new SimpleInventory(1) {
		@Override
		public void markDirty() {
			super.markDirty();
			DyeingScreenHandler.this.onContentChanged(this);
		}
	};
	private final CraftingResultInventory resultInventory = new CraftingResultInventory() {
		@Override
		public void markDirty() {
			DyeingScreenHandler.this.onContentChanged(this);
		}
	};

	@Override
	public void onContentChanged(Inventory inventory) {
		super.onContentChanged(inventory);
		if (inventory == this.inventory) {
			this.updateResult();
		}
	}

	public DyeingScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
	}

	public DyeingScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(AntiqueScreenHandlerType.DYE_TABLE, syncId);
		this.context = context;
		this.addSlot(new Slot(this.inventory, 0, 62, 37) {
			@Override
			public boolean canInsert(ItemStack stack) {
				if (stack.isOf(AntiqueItems.MYRIAD_TOOL)) {
					ClothSkinData.ClothSubData data = ClothSkinListener.getTransform(stack.getOrDefault(AntiqueDataComponentTypes.CLOTH_TYPE, "cloth"));
					return data.dyeable();
				}
				return stack.isIn(ItemTags.DYEABLE);
			}

			@Override
			public void onTakeItem(PlayerEntity player, ItemStack stack) {
				DyeingScreenHandler.this.resetHex();
			}
		});
		this.addSlot(new Slot(this.resultInventory, 0, 98, 37) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return false;
			}

			@Override
			public void onTakeItem(PlayerEntity player, ItemStack stack) {
				DyeingScreenHandler.this.removeItem();
			}
		});
		this.addPlayerSlots(playerInventory, 8, 84);
	}

	private void removeItem() {
		this.inventory.setStack(0, ItemStack.EMPTY);
		this.resetHex();
	}

	private void resetHex() {
		this.hexCode = "";
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int index) {
		ItemStack newStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);

		if (slot.hasStack()) {
			ItemStack originalStack = slot.getStack();
			newStack = originalStack.copy();

			int inputSlot = 0;
			int outputSlot = 1;
            int playerInvStart = 2;
			int playerInvEnd = playerInvStart + 27;
            int hotbarEnd = playerInvEnd + 9;

			if (index == outputSlot || index == inputSlot) {
				if (!insertItem(originalStack, playerInvStart, hotbarEnd, false)) {
					return ItemStack.EMPTY;
				}
			} else {
				if (originalStack.isIn(ItemTags.DYEABLE)) {
					if (!insertItem(originalStack, inputSlot, inputSlot + 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= playerInvStart && index < playerInvEnd) {
					if (!insertItem(originalStack, playerInvEnd, hotbarEnd, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= playerInvEnd && index < hotbarEnd) {
					if (!insertItem(originalStack, playerInvStart, playerInvEnd, false)) {
						return ItemStack.EMPTY;
					}
				}
			}

			if (originalStack.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (originalStack.getCount() == newStack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTakeItem(player, originalStack);
			this.sendContentUpdates();
		}

		return newStack;
	}

	@Override
	public void onClosed(PlayerEntity player) {
		super.onClosed(player);
		this.context.run((world, pos) -> this.dropInventory(player, this.inventory));
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

	public void updateResult() {
		ItemStack originalStack = this.inventory.getStack(0);
		ItemStack resultStack = originalStack.copy();
		if (this.hexCode != null && this.hexCode.length() == 6) {
			int intValue = 0;
			try {
				intValue = Integer.parseInt(this.hexCode, 16);
			} catch (NumberFormatException e) {
				System.err.println("Invalid hexadecimal string format: " + e.getMessage());
			}
			resultStack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(intValue));
		}
		this.resultInventory.setStack(0, resultStack);
	}

	public boolean setHexCode(String string) {
		if (string != null && !string.equals(this.hexCode) && string.length() == 6) {
			this.hexCode = string;
			this.updateResult();
			return true;
		} else {
			return false;
		}
	}
}
