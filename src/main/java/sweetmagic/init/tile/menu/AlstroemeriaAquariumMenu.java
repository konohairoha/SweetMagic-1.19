package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.sm.TileAlstroemeriaAquarium;

public class AlstroemeriaAquariumMenu extends BaseSMMenu {

	public final TileAlstroemeriaAquarium tile;

	public AlstroemeriaAquariumMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, (TileAlstroemeriaAquarium) MenuInit.getTile(pInv, data));
	}

	public AlstroemeriaAquariumMenu(int windowId, Inventory pInv, TileAlstroemeriaAquarium tile) {
		super(MenuInit.alstroemeriaAquariumMenu, windowId, pInv, tile);
		this.tile = tile;

		this.addSlot(new SMSlot(this.tile.getHand(), 0, 80, 13));

		for (int y = 0; y < 2; y++)
			for (int x = 0; x < 5; x++)
				this.addSlot(new SMSlot(this.tile.getInput(), x + y * 5, 44 + x * 18, 48 + y * 18));

		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 9; x++)
				this.addSlot(new SMSlot(this.tile.getOutput(), x + y * 9, 8 + x * 18, 90 + y * 18, (s) -> false));

		this.setPInv(pInv, 8, 148);
		this.setSlotSize(this.tile.getInvSize() + 10 + 1);
	}
}
