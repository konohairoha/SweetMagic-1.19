package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.sm.TileSMSpawner;

public class SMSpawnerMenu extends BaseSMMenu {

	public final TileSMSpawner tile;

	public SMSpawnerMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, (TileSMSpawner) MenuInit.getTile(pInv, data));
	}

	public SMSpawnerMenu(int windowId, Inventory pInv, TileSMSpawner tile) {
		super(MenuInit.smSpawmerMenu, windowId, pInv, tile);
		this.tile = tile;
		this.setPInv(pInv, 7, 51);
		this.setSlotSize(0);
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		this.tile.clickButton(id);
		return true;
	}
}
