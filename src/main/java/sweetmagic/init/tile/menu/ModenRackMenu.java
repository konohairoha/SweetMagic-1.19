package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.sm.TileModenRack;

public class ModenRackMenu extends BaseSMMenu {

	public final TileModenRack tile;
	public final int data;

	public ModenRackMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, MenuInit.getTile(TileModenRack::new, pInv, data));
	}

	public ModenRackMenu(int windowId, Inventory pInv, TileModenRack tile) {
		super(MenuInit.modenRackMenu, windowId, pInv, tile);

		this.tile = tile;
		this.data = this.tile.getData();
		this.setInv(this.tile.getInput());

		this.setPInv(pInv, 7, 50);
		this.setSlotSize(this.tile.getInvSize());
	}

	public void setInv (IItemHandler chest) {
		switch (this.data) {
		case 0:
			for (int y = 0; y < 2; y++)
				for (int x = 0; x < 9; x++)
					this.addSlot(new SMSlot(chest, x + y * 9, 7 + 18 * x, 8 + 18 * y));
			break;
		case 1:
		case 9:
			for (int x = 0; x < 3; x++)
				this.addSlot(new SMSlot(chest, x, 61 + x * 18, 8));
			break;
		case 2:
			for (int y = 0; y < 2; y++)
				for (int x = 0; x < 3; x++)
				this.addSlot(new SMSlot(chest, x + y * 3, 61 + x * 18, 8 + y * 18));
			break;
		case 3:
			for (int y = 0; y < 2; y++)
				for (int x = 0; x < 3; x++)
				this.addSlot(new SMSlot(chest, x + y * 3, 61 + x * 18 - 36, 8 + y * 18));

			for (int y = 0; y < 2; y++)
				for (int x = 0; x < 3; x++)
				this.addSlot(new SMSlot(chest, x + y * 3 + 6, 61 + x * 18 + 36, 8 + y * 18));
			break;
		case 4:
		case 5:
			for (int y = 0; y < 2; y++)
				this.addSlot(new SMSlot(chest, y, 79, 8 + y * 18));
			break;
		case 6:
		case 7:
			for (int y = 0; y < 2; y++)
				for (int x = 0; x < 4; x++)
				this.addSlot(new SMSlot(chest, x + y * 4, 52 + x * 18, 8 + y * 18));
			break;
		case 8:
			for (int x = 0; x < 4; x++)
				this.addSlot(new SMSlot(chest, x, 52 + x * 18, 17));
			break;
		}
	}
}
