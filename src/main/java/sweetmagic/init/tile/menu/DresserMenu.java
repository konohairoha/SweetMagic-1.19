package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.MagiaSlot;
import sweetmagic.init.tile.slot.SlotInput;
import sweetmagic.init.tile.sm.TileDresser;
import sweetmagic.util.ItemHelper;

public class DresserMenu extends BaseSMMenu {

	public final TileDresser tile;

	public DresserMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, (TileDresser) MenuInit.getTile(pInv, data));
	}

	public DresserMenu(int windowId, Inventory pInv, TileDresser tile) {
		super(MenuInit.dresserMenu, windowId, pInv, tile);
		this.tile = tile;
		IItemHandler fuel = this.tile.getInput();

		for (int y = 0; y < 6; y++)
			for (int x = 0; x < 9; x++)
				this.addSlot(new MagiaSlot(tile, fuel, x + y * 9, 8 + x * 18, 8 + y * 18, SlotInput.ISSM_ACC));

		this.setPInv(pInv, 8, 120);
		this.setSlotSize(this.tile.getInvSize());
	}

	public void removed(Player player) {
		super.removed(player);
		this.tile.playSound(this.tile.getBlockPos(), SoundEvents.BARREL_CLOSE, 0.5F, this.rand.nextFloat() * 0.1F + 0.9F);
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
		return true;
	}

	protected boolean moveItemStackTo(ItemStack stack, int slotStart, int slotEnd, boolean par1) {

		boolean flag = false;
		int i = par1 ? slotEnd - 1 : slotStart;

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
