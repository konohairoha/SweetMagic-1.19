package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.slot.SlotInput;
import sweetmagic.init.tile.sm.TileTransferGateVertical;

public class TransferGateVerticalMenu extends BaseSMMenu {

	public TransferGateVerticalMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, MenuInit.getTile(TileTransferGateVertical::new, pInv, data));
	}

	public TransferGateVerticalMenu(int windowId, Inventory pInv, TileTransferGateVertical tile) {
		super(MenuInit.transferGateVerticalMenu, windowId, pInv, tile);
		this.addSlot(new SMSlot(tile.getInput(), 0, 79, 14, SlotInput.ISCLERO));
		this.setPInv(pInv, 7, 50, 0);
		this.setSlotSize(1);
	}
}
