package sweetmagic.init.tile.menu;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.MagiaSlot;
import sweetmagic.init.tile.sm.TileAbstractSM.MagiaHandler;
import sweetmagic.init.tile.sm.TileMagiaStorage;
import sweetmagic.util.ItemHelper;

public class MagiaStorageMenu extends BaseSMMenu {

	public final TileMagiaStorage tile;
	public final NonNullList<Slot> slotList = NonNullList.create();
	private final MagiaHandler inv;

	public MagiaStorageMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, (TileMagiaStorage) MenuInit.getTile(pInv, data));
	}

	public MagiaStorageMenu(int windowId, Inventory pInv, TileMagiaStorage tile) {
		super(MenuInit.magiaStorageMenu, windowId, pInv, tile);
		this.tile = tile;
		this.inv = (MagiaHandler) this.tile.getInput();

		for (int y = 0; y < 8; y++)
			for (int x = 0; x < 13; x++)
			this.addSlots(new MagiaSlot(tile, this.inv, x + y * 13, 5 + x * 18, 5 + y * 18), tile.getLevel());

		this.setPInv(pInv, 41, 151, 1);
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

	public void removed(Player player) {
		super.removed(player);
//		this.tile.sendPKT();
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		switch (id) {
		case 0:
			ItemHelper.compactInventory(this.tile.inputInv);
			this.tile.clickButton();
			break;
		case 1:
			ItemHelper.inventoryInput(player, this.tile.inputInv);
			this.tile.clickButton();
			break;
		case 2:
			ItemHelper.inventoryOutput(player, this.tile.inputInv);
			this.tile.clickButton();
			break;
		}
		this.tile.sendPKT();
		return true;
	}
}
