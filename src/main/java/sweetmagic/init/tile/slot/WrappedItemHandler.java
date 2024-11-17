package sweetmagic.init.tile.slot;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public class WrappedItemHandler implements IItemHandlerModifiable {

	private final IItemHandlerModifiable handler;
	private final WriteMode mode;

	public WrappedItemHandler(IItemHandlerModifiable handler, WriteMode mode) {
		this.handler = handler;
		this.mode = mode;
	}

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
		if (this.mode == WriteMode.IN || mode == WriteMode.IN_OUT) {
			return this.handler.insertItem(slot, stack, simulate);
		}
		return stack;
	}

	@NotNull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (this.mode == WriteMode.OUT || this.mode == WriteMode.IN_OUT) {
			return this.handler.extractItem(slot, amount, simulate);
		}
		return ItemStack.EMPTY;
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

	public enum WriteMode {
		IN,
		OUT,
		IN_OUT,
		NONE
	}
}
