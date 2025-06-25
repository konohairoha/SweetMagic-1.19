package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sweetmagic.api.iitem.IRobe;
import sweetmagic.api.iitem.IWand;
import sweetmagic.api.iitem.info.PorchInfo;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.slot.SlotInput;

public class SMPorchMenu extends BaseItemMenu {

	public SMPorchMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, pInv.player.getItemBySlot(EquipmentSlot.LEGS));
	}

	public SMPorchMenu(int windowId, Inventory pInv, ItemStack stack) {
		super(MenuInit.porchMenu, windowId, pInv, new PorchInfo(stack).getInv());

		int count = 0;

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 8; x++) {

				count++;
				if (count > this.slotSize) { break; }

				this.addSlot(new SMSlot(this.inventory, x + y * 8, 16 + x * 18, 8 + y * 18, SlotInput.ISSM_ACC));
			}
		}

		this.setPInv(pInv, 8, 66);
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		switch (id) {
		case 0:
			ItemStack robe = player.getItemBySlot(EquipmentSlot.CHEST);
			((IRobe) robe.getItem()).openGui(player.getLevel(), player, robe);
			break;
		case 1:
			ItemStack stack = IWand.getWand(player);
			if (stack.isEmpty() || !(stack.getItem() instanceof IWand wand)) { break; }

			wand.openGui(player.getLevel(), player, stack);
			break;
		}

		return true;
	}
}
