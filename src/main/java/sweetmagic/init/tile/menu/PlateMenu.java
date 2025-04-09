package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.sm.TilePlate;

public class PlateMenu extends BaseSMMenu {

	public final TilePlate tile;
	public final int data;

	public PlateMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, (TilePlate) MenuInit.getTile(pInv, data));
	}

	public PlateMenu(int windowId, Inventory pInv, TilePlate tile) {
		super(MenuInit.plateMenu, windowId, pInv, tile);

		this.tile = tile;
		this.data = this.tile.getData();
		this.setInv(this.tile.getInput());

		this.setPInv(pInv, 7, 50);
		this.setSlotSize(this.tile.getInvSize());
	}

	public void setInv (IItemHandler chest) {
		switch (this.data) {
		case 3:
			for (int y = 0; y < 2; y++)
				this.addSlot(new SMSlot(chest, y, 79, 9 + y * 18));
			break;
		default:
			this.addSlot(new SMSlot(chest, 0, 79, 14));
			break;
		}
	}
}
