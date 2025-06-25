package sweetmagic.init.tile.inventory;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.misc.NotNull;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import sweetmagic.api.iitem.info.BaseItemInfo;

public abstract class BaseSMInventory implements IItemHandlerModifiable {

	public ItemStack stack;
	public ItemStackHandler inv;

	public BaseSMInventory(BaseItemInfo info) {
		this.setStack(info.getStack());
	}

	public BaseSMInventory(ItemStack stack) {
		this.setStack(stack);
	}

	public ItemStackHandler getInv() {
		return this.inv;
	}

	public void setInv(ItemStackHandler inv) {
		this.inv = inv;
	}

	public ItemStack getStack() {
		return this.stack;
	}

	public void setStack(ItemStack stack) {
		this.stack = stack;
	}

	public List<ItemStack> getStackList() {

		List<ItemStack> stackList = new ArrayList<>();

		for (int i = 0; i < this.getInv().getSlots(); ++i) {
			ItemStack stack = this.getInv().getStackInSlot(i);
			if (stack.isEmpty()) { continue; }

			stackList.add(stack);
		}

		return stackList;
	}

	public List<ItemStack> getStackAllList() {

		List<ItemStack> stackList = new ArrayList<>();

		for (int i = 0; i < this.getInv().getSlots(); ++i) {
			stackList.add(this.getInv().getStackInSlot(i));
		}

		return stackList;
	}

	@Override
	public int getSlots() {
		return this.getInv().getSlots();
	}

	@NotNull
	@Override
	public ItemStack getStackInSlot(int slot) {
		return this.getInv().getStackInSlot(slot);
	}

	@NotNull
	@Override
	public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		ItemStack ret = this.getInv().insertItem(slot, stack, simulate);
		this.writeBack();
		return ret;
	}

	@NotNull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack ret = this.getInv().extractItem(slot, amount, simulate);
		this.writeBack();
		return ret;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return this.getInv().isItemValid(slot, stack);
	}

	@Override
	public void setStackInSlot(int slot, @NotNull ItemStack stack) {
		this.getInv().setStackInSlot(slot, stack);
		this.writeBack();
	}

	public void writeBack() {
		for (int i = 0; i < this.getInv().getSlots(); ++i) {
			if (this.getInv().getStackInSlot(i).isEmpty()) {
				this.getInv().setStackInSlot(i, ItemStack.EMPTY);
			}
		}
		this.writeToNBT(this.getTag());
	}

	public CompoundTag getTag() {
		return this.getStack().getOrCreateTag();
	}

	public void readFromNBT(CompoundTag tags) {
		this.getInv().deserializeNBT(tags.getCompound("Items"));
	}

	public void writeToNBT(CompoundTag tags) {
		tags.put("Items", this.getInv().serializeNBT());
	}
}
