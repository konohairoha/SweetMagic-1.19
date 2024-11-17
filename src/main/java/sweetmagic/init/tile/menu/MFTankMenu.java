package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.sm.TileMFTank;

public class MFTankMenu extends BaseSMMenu {

	public final TileMFTank tile;

    public MFTankMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
        this(windowId, pInv, (TileMFTank) MenuInit.getTile(pInv, data));
    }

	public MFTankMenu(int windowId, Inventory pInv, TileMFTank tile) {
		super(MenuInit.tankMenu, windowId, pInv, tile);
		this.tile = tile;

		this.setSlot();

		this.setPInv(pInv, 8, 99);
		this.setSlotSize(this.tile.getInvSize() + this.tile.getSubInvSize());
	}

	public void setSlot () {

		switch (this.tile.getInvSize()) {
		case 1:
			this.addSlot(new SMSlot(this.tile.getInput(), 0, 80, 23));
			break;
		case 3:
			for (int x = 0; x < 3; x++)
				this.addSlot(new SMSlot(this.tile.getInput(), x, 62 + x * 18, 23));
			break;
		case 5:
			for (int x = 0; x < 5; x++)
				this.addSlot(new SMSlot(this.tile.getInput(), x, 44 + x * 18, 23));
			break;
		}

		switch (this.tile.getInvSize()) {
		case 1:
			for (int x = 0; x < 3; x++)
				this.addSlot(new SMSlot(this.tile.getOut(), x, 62 + x * 18, 74, s -> false));
			break;
		case 3:
			for (int x = 0; x < 5; x++)
				this.addSlot(new SMSlot(this.tile.getOut(), x, 44 + x * 18, 74, s -> false));
			break;
		case 5:
			for (int x = 0; x < 7; x++)
				this.addSlot(new SMSlot(this.tile.getOut(), x, 26 + x * 18, 74, s -> false));
			break;
		}
	}
}
