package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import sweetmagic.api.iitem.IMagicBook;
import sweetmagic.api.iitem.IPorch;
import sweetmagic.api.iitem.IRobe;
import sweetmagic.api.iitem.info.BookInfo;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.slot.SlotInput;

public class MagicBookMenu extends BaseItemMenu {

	private final ItemStack stack;

	public MagicBookMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, pInv.player.getMainHandItem());
	}

	public MagicBookMenu(int windowId, Inventory pInv, ItemStack stack) {
		super(MenuInit.magicBookMenu, windowId, pInv, new BookInfo(stack).getInv());

		this.stack = stack;
		int count = 0;

		for (int y = 0; y < 2; y++) {
			for (int x = 0; x < 5; x++) {

				count++;
				if (count > this.slotSize) { break; }

				this.addSlot(new SMSlot(this.inventory, y * 5 + x, 37 + x * 22, 13 + y * 22, SlotInput.ISMAGICPAGE));
			}

			if (count > this.slotSize) { break; }
		}

		this.setPInv(pInv, 8, 66);
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
		case 2:
			((IMagicBook) this.stack.getItem()).openCraftGui(player.getLevel(), player, this.stack);
			break;
		}

		return true;
	}
}
