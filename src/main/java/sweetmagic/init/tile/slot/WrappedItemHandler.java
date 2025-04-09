package sweetmagic.init.tile.slot;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public record WrappedItemHandler(IItemHandlerModifiable handler, WrappedItemHandler.WriteMode mode) implements IItemHandlerModifiable {

	@Override
	public int getSlots() {
		return this.handler.getSlots();
	}

	@NotNull
	@Override
	public ItemStack getStackInSlot(int slot) {
		return this.handler.getStackInSlot(slot);
	}

	@NotNull
	@Override
	public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		return this.mode == WriteMode.IN || mode == WriteMode.IN_OUT ? this.handler.insertItem(slot, stack, simulate) : stack;
	}

	@NotNull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return this.mode == WriteMode.OUT || this.mode == WriteMode.IN_OUT ? this.handler.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot) {
		return this.handler.getSlotLimit(slot);
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return this.handler.isItemValid(slot, stack);
	}

	@Override
	public void setStackInSlot(int slot, @NotNull ItemStack stack) {
		this.handler.setStackInSlot(slot, stack);
	}

	public static enum WriteMode {
		IN, OUT, IN_OUT, NONE
	}
}
