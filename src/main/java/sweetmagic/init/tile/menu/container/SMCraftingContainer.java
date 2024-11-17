package sweetmagic.init.tile.menu.container;

import java.util.stream.IntStream;

import javax.annotation.Nonnull;

import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.tile.sm.TileAbstractSM.StackHandler;

public class SMCraftingContainer extends CraftingContainer {

	private boolean doNotCallUpdates;
	private final StackHandler hand;
	private final AbstractContainerMenu menu;

	public SMCraftingContainer(AbstractContainerMenu menu, StackHandler handl) {
		super(menu, 3, 3);
		this.hand = handl;
		this.menu = menu;
		this.doNotCallUpdates = false;
	}

	@Nonnull
	@Override
	public ItemStack getItem(int slot) {
		this.validate(slot);
		return hand.getStackInSlot(slot);
	}

	public void validate(int slot) {
		if (this.isValid(slot)) { return; }
		throw new IndexOutOfBoundsException("Someone attempted to poll an outofbounds stack at slot " + slot + " report to them, NOT Crafting Station");
	}

	public boolean isValid(int slot) {
		return slot >= 0 && slot < this.getContainerSize();
	}

	@Nonnull
	@Override
	public ItemStack removeItem(int slot, int count) {

		this.validate(slot);
		ItemStack stack = this.hand.extractItem(slot, count, false);
		if (!stack.isEmpty()) {
			this.onCraftMatrixChanged();
		}

		return stack;
	}

	@Override
	public void setItem(int slot, @Nonnull ItemStack stack) {
		this.validate(slot);
		this.hand.setStackInSlot(slot, stack);
		this.onCraftMatrixChanged();
	}

	@Nonnull
	@Override
	public ItemStack removeItemNoUpdate(int index) {
		this.validate(index);
		ItemStack stack = getItem(index);
		if (stack.isEmpty()) { return ItemStack.EMPTY; }

		this.onCraftMatrixChanged();
		this.setItem(index, ItemStack.EMPTY);
		return stack;
	}

	public NonNullList<ItemStack> getStackList() {
		return this.hand.getContents();
	}

	@Override
	public boolean isEmpty() {
		return IntStream.range(0, this.hand.getSlots()).allMatch(i -> this.hand.getStackInSlot(i).isEmpty());
	}

	@Override
	public void clearContent() { }

	public void setDoNotCallUpdates(boolean doNotCallUpdate) {
		this.doNotCallUpdates = doNotCallUpdate;
	}

	public void onCraftMatrixChanged() {
		if (!this.doNotCallUpdates) {
			this.menu.slotsChanged(this);
		}
	}
}
