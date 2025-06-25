package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import sweetmagic.api.iitem.IPorch;
import sweetmagic.api.iitem.IRobe;
import sweetmagic.api.iitem.IWand;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SlotInput;
import sweetmagic.init.tile.slot.WandSlot;

public class SMWandMenu extends BaseItemMenu {

	public ItemStack stack;

	public SMWandMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, IWand.getWand(pInv.player));
	}

	public SMWandMenu(int windowId, Inventory pInv, ItemStack stack) {
		super(MenuInit.wandMenu, windowId, pInv, new WandInfo(stack).getInv());

		WandInfo info = new WandInfo(stack);
		this.stack = stack;
		int count = 0;

		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 7; x++) {

				count++;
				if (count > this.slotSize) { break; }

				this.addSlot(new WandSlot(this.inventory, y * 7 + x, 31 + x * 22, 14 + y * 19, SlotInput.isMagicItem(info.getWand().getWandTier())));
			}

			if (count > this.slotSize) { break; }
		}

		this.setPInv(pInv, 16, 112);
	}

	@Override
	public void clicked(int slotId, int dragType, ClickType clickType, Player player) {

		if (slotId == -999 || this.slots.size() >= slotId + 36 || slotId < 0) {
			super.clicked(slotId, dragType, clickType, player);
			return;
		}

		ItemStack stack = this.slots.get(slotId).getItem();
		if (stack.isEmpty() || !stack.is(player.getMainHandItem().getItem())) {
			super.clicked(slotId, dragType, clickType, player);
		}
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		switch (id) {
		case 0:
			ItemStack robe = player.getItemBySlot(EquipmentSlot.CHEST);
			((IRobe) robe.getItem()).openGui(player.getLevel(), player, robe);
			break;
		case 1:
			ItemStack porch = player.getItemBySlot(EquipmentSlot.LEGS);
			((IPorch) porch.getItem()).openGui(player.getLevel(), player, porch);
			break;
		}

		return true;
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
					int maxSize = Math.min(slot.getMaxStackSize(stack), stack.getMaxStackSize());

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
