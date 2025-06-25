package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.sm.TileRegister;

public class RegisterMenu extends BaseSMMenu {

	public final TileRegister tile;

	public RegisterMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, MenuInit.getTile(TileRegister::new, pInv, data));
	}

	public RegisterMenu(int windowId, Inventory pInv, TileRegister tile) {
		super(MenuInit.registerMenu, windowId, pInv, tile);
		this.tile = tile;
		this.setPInv(pInv, 7, 50);
		this.setSlotSize(0);
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		this.tile.addButton(player, id);
		return true;
	}
}
