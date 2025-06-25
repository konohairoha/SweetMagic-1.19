package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.sm.TileMagiaAccelerator;

public class MagiaAcceleratorMenu extends BaseSMMenu {

	public final TileMagiaAccelerator tile;

	public MagiaAcceleratorMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, MenuInit.getTile(TileMagiaAccelerator::new, pInv, data));
	}

	public MagiaAcceleratorMenu(int windowId, Inventory pInv, TileMagiaAccelerator tile) {
		super(MenuInit.magiaaccelerator_menu, windowId, pInv, tile);
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
