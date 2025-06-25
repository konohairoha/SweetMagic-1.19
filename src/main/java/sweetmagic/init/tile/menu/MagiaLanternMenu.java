package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.sm.TileMagiaLantern;

public class MagiaLanternMenu extends BaseSMMenu {

	public final TileMagiaLantern tile;

	public MagiaLanternMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, MenuInit.getTile(TileMagiaLantern::new, pInv, data));
	}

	public MagiaLanternMenu(int windowId, Inventory pInv, TileMagiaLantern tile) {
		super(MenuInit.magiaLanternMenu, windowId, pInv, tile);
		this.tile = tile;
		this.setPInv(pInv, 7, 50);
		this.setSlotSize(0);
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		this.tile.addRange(id);
		return true;
	}
}
