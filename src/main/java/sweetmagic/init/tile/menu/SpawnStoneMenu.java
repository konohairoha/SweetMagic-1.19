package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.sm.TileSpawnStone;

public class SpawnStoneMenu extends BaseSMMenu {

	public final TileSpawnStone tile;

	public SpawnStoneMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, (TileSpawnStone) MenuInit.getTile(pInv, data));
	}

	public SpawnStoneMenu(int windowId, Inventory pInv, TileSpawnStone tile) {
		super(MenuInit.spawnStoneMenu, windowId, pInv, tile);
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
