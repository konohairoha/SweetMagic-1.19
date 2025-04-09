package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.sm.TileSpawnCrystal;

public class SpawnCrystalMenu extends BaseSMMenu {

	public final TileSpawnCrystal tile;

	public SpawnCrystalMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, (TileSpawnCrystal) MenuInit.getTile(pInv, data));
	}

	public SpawnCrystalMenu(int windowId, Inventory pInv, TileSpawnCrystal tile) {
		super(MenuInit.spawnCrystalMenu, windowId, pInv, tile);
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
