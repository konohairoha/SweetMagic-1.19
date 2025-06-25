package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.sm.TileMFBottler;

public class MFBottlerMenu extends BaseSMMenu {

	public final TileMFBottler tile;

	public MFBottlerMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, MenuInit.getTile(TileMFBottler::new, pInv, data));
	}

	public MFBottlerMenu(int windowId, Inventory pInv, TileMFBottler tile) {
		super(MenuInit.mfBottlerMenu, windowId, pInv, tile);
		this.tile = tile;

		for (int y = 0; y < 2; y++)
			for (int x = 0; x < 9; x++)
				this.addSlot(new SMSlot(this.tile.getInput(), x + y * 9, 12 + x * 18, 92 + y * 18, s -> false));

		this.setPInv(pInv, 12, 132);
		this.setSlotSize(this.tile.getInvSize());
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {

		int secCount = this.tile.setCount;

		if (id <= 3) {

			switch(id) {
			case 0:
				secCount += 1;
				break;
			case 1:
				secCount -= 1;
				break;
			case 2:
				secCount += this.tile.setCount == 1 ? 9 : 10;
				break;
			case 3:
				secCount -= 10;
				break;
			}

			this.tile.setCount = Math.max(1, Math.min(64, secCount));
		}

		else {
			this.tile.selectId = id == 99 ? -1 : id -4;
		}

		this.tile.sendPKT();
		this.tile.clickButton();
		return true;
	}
}
