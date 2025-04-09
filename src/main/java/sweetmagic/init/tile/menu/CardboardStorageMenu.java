package sweetmagic.init.tile.menu;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.MagiaSlot;
import sweetmagic.init.tile.sm.TileAbstractSM.MagiaHandler;
import sweetmagic.init.tile.sm.TileCardboardStorage;

public class CardboardStorageMenu extends BaseSMMenu {

	public final TileCardboardStorage tile;
	public final NonNullList<Slot> slotList = NonNullList.create();
	private final MagiaHandler inv;

	public CardboardStorageMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, (TileCardboardStorage) MenuInit.getTile(pInv, data));
	}

	public CardboardStorageMenu(int windowId, Inventory pInv, TileCardboardStorage tile) {
		super(MenuInit.cardboardStorageMenu, windowId, pInv, tile);
		this.tile = tile;
		this.inv = this.tile.getInput();

		this.addSlots(new MagiaSlot(tile, this.inv, 0, 79, 14), tile.getLevel());

		this.setPInv(pInv, 7, 50, 0);
		this.setSlotSize(this.tile.getInvSize());
	}

	protected Slot addSlots(Slot slot, Level world) {
		this.slotList.add(slot);
		return this.addSlot(slot);
	}

	protected boolean moveItemStackTo(ItemStack stack, int slotStart, int slotEnd, boolean par1) {

		boolean flag = false;
		int i = par1 ? slotEnd - 1 : slotStart;

		if (stack.isStackable()) {

			while (!stack.isEmpty()) {

				if (par1) {
					if (i < slotStart) { break; }
				}

				else if (i >= slotEnd) { break; }

				Slot slot = this.slots.get(i);
				ItemStack stack1 = slot.getItem();

				if (!stack1.isEmpty() && ItemStack.isSameItemSameTags(stack, stack1)) {
					int count = stack1.getCount() + stack.getCount();
					int maxSize = slot instanceof MagiaSlot ? slot.getMaxStackSize(stack) : Math.min(slot.getMaxStackSize(stack), stack.getMaxStackSize());

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
}
