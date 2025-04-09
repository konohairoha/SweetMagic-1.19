package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.slot.SlotArmor;
import sweetmagic.init.tile.slot.SlotInput;
import sweetmagic.init.tile.sm.TileMFTable;
import sweetmagic.util.SMUtil;

public class MFTableMenu extends BaseSMMenu {

	public final TileMFTable tile;

	public MFTableMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, (TileMFTable) MenuInit.getTile(pInv, data));
	}

	public MFTableMenu(int windowId, Inventory pInv, TileMFTable tile) {
		super(MenuInit.tableMenu, windowId, pInv, tile);
		this.tile = tile;

		this.setSlot();
		this.addSlot(new SMSlot(this.tile.getFuel(), 0, 134, 67, SlotInput.HASMF));

		this.setPInv(pInv, 38, 138);
		this.setSlotSize(this.tile.getInvSize() + 1);

		// Armor slots
		for (int y = 0; y < 4; y++)
			this.addSlot(new SlotArmor(pInv.player, SMUtil.getEquipmentSlot(y), pInv, 39 - y, 10, 138 + y * 18));
	}

	public void setSlot () {

		IItemHandler input = this.tile.getInput();
		this.addSlot(new SMSlot(input, 0, 38, 49, SlotInput.ISMFTOOL));
		int size = this.tile.getInvSize();

		if (size >= 4) {
			this.addSlot(new SMSlot(input, 1, 38, 85, SlotInput.ISMFTOOL));
			this.addSlot(new SMSlot(input, 2, 15, 23, SlotInput.ISMFTOOL));
			this.addSlot(new SMSlot(input, 3, 62, 23, SlotInput.ISMFTOOL));
		}

		if (size >= 6) {
			this.addSlot(new SMSlot(input, 4, 2, 60, SlotInput.ISMFTOOL));
			this.addSlot(new SMSlot(input, 5, 74, 60, SlotInput.ISMFTOOL));
		}
	}
}
