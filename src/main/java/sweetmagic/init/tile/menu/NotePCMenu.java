package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.MenuInit;
import sweetmagic.init.capability.icap.ICookingStatus;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.sm.TileNotePC;

public class NotePCMenu extends BaseSMMenu {

	public final TileNotePC tile;

	public NotePCMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, MenuInit.getTile(TileNotePC::new, pInv, data));
	}

	public NotePCMenu(int windowId, Inventory pInv, TileNotePC tile) {
		super(MenuInit.notePcMenu, windowId, pInv, tile);
		this.tile = tile;
		ICookingStatus.sendPKT(this.player);

		IItemHandler out = this.tile.getOut();

		this.addSlot(new SMSlot(this.tile.getInput(), 0, 8, 14));

		for (int y = 0; y < 2; y++)
			for (int x = 0; x < 5; x++)
			this.addSlot(new SMSlot(out, x + y * 5, 84 + x * 18, 123 + y * 18, s -> false));

		this.setPInv(pInv, 12, 163);
		this.setSlotSize(11);
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		this.tile.clickButton();
		if(id == -1) { return false; }

		if(id <= 5) {
			this.tile.addButCount(id);
		}

		else if(id == 6) {
			this.tile.addButSale(player);
		}

		else if(id >= 7) {
			this.tile.addButBuy(player, id - 7);
		}

		return true;
	}
}
