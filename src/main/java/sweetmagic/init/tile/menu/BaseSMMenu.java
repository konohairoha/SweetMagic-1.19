package sweetmagic.init.tile.menu;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.tile.slot.MagiaSlot;
import sweetmagic.init.tile.sm.TileAbstractSM;

public abstract class BaseSMMenu extends AbstractContainerMenu {

	public int tickCount = 0;
	public int oldIndex = 0;
	protected final Random rand = new Random();
	public final TileAbstractSM tile;
	public int slotSize = 0;
	public Player player;

	public BaseSMMenu(MenuType<?> type, int windowId, Inventory pInv, TileAbstractSM tile) {
		super(type, windowId);
		this.tile = tile;
		this.player = pInv.player;
	}

	public void setPInv(Inventory pInv, int tX, int tY) {
		this.setPInv(pInv, tX, tY, 0);
	}

	public void setPInv(Inventory pInv, int tX, int tY, int addY) {

		//Player Inventorye
		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 9; x++)
				this.addSlot(new Slot(pInv, x + y * 9 + 9, tX + x * 18, tY + y * 18));

		//Player HotBar
		for (int x = 0; x < 9; x++)
			this.addSlot(new Slot(pInv, x, tX + x * 18, tY + 58 + addY));
	}

	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if(index <= this.getSlotSize() && this.oldIndex == index && this.tickCount == player.tickCount) { return stack; }

		this.tickCount = player.tickCount;
		this.oldIndex = index;

		if (slot != null && slot.hasItem()) {

			ItemStack stack1 = slot.getItem();
			stack = stack1.copy();
			int slotCount = this.getSlotSize();

			if (index < slotCount) {
				if (!this.moveItemStackTo(stack1, slotCount, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			}

			else if (!this.moveItemStackTo(stack1, 0, slotCount, false)) {
				return ItemStack.EMPTY;
			}
			if (stack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			}

			else {
				slot.setChanged();
			}

			this.quickMoveStack(player, slot, stack, stack1);
		}
		return stack;
	}

	public boolean moveSlot(ItemStack stack, int slotStart, int slotEnd, boolean par1) {

		boolean flag = false;
		int i = par1 ? slotEnd - 1 : slotStart;

		if (this.checkStack(stack)) {
			while (!stack.isEmpty()) {

				if (par1) {
					if (i < slotStart) { break; }
				}

				else if (i >= slotEnd) { break; }

				Slot slot = this.slots.get(i);
				ItemStack stack1 = slot.getItem();

				if (!stack1.isEmpty() && ItemStack.isSameItemSameTags(stack, stack1)) {
					int count = stack1.getCount() + stack.getCount();
					int maxSize = this.checkSlotValue(slot, stack) ? slot.getMaxStackSize(stack) : Math.min(slot.getMaxStackSize(stack), stack.getMaxStackSize());


					if (count <= maxSize) {
						stack.setCount(0);
						stack1.setCount(count);
						slot.setChanged();
						flag = true;
					}

					else if (stack1.getCount() < maxSize) {
						stack.shrink(maxSize - stack1.getCount());
						stack1.setCount(maxSize);
						slot.setChanged();
						flag = true;
					}
				}

				i = par1 ? i - 1 : i + 1;
			}
		}

		if (!stack.isEmpty()) {

			i = par1 ? slotEnd - 1 : slotStart;

			while (true) {

				if (par1) {
					if (i < slotStart) { break; }
				}

				else if (i >= slotEnd) { break; }

				Slot slot1 = this.slots.get(i);
				ItemStack stack2 = slot1.getItem();

				if (stack2.isEmpty() && slot1.mayPlace(stack)) {
					slot1.set(stack.getCount() > slot1.getMaxStackSize(stack) ? stack.split(slot1.getMaxStackSize(stack)) : stack.split(stack.getCount()));
					slot1.setChanged();
					flag = true;
					break;
				}

				i = par1 ? i - 1 : i + 1;
			}
		}

		return flag;
	}

	public boolean checkStack(ItemStack stack) {
		return stack.isStackable();
	}

	public boolean checkSlotValue(Slot slot, ItemStack stack) {
		return slot instanceof MagiaSlot;
	}

	public void quickMoveStack(Player player, Slot slot, ItemStack oldStack, ItemStack newStack) { }

	// ブロックエンティティの取得
	public TileAbstractSM getTile() {
		return this.tile;
	}

	// スロットサイズの取得
	public void setSlotSize(int slotSize) {
		this.slotSize = slotSize;
	}

	// スロットサイズの設定
	public int getSlotSize() {
		return this.slotSize;
	}

	@Override
	public boolean stillValid(Player player) {
		BlockPos pos = this.getTile().getBlockPos();
		return player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64D;
	}
}
