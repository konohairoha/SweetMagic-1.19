package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.sm.TileAetherLamplight;

public class AetherLamplightMenu extends BaseSMMenu {

	public final TileAetherLamplight tile;

	public AetherLamplightMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, MenuInit.getTile(TileAetherLamplight::new, pInv, data));
	}

	public AetherLamplightMenu(int windowId, Inventory pInv, TileAetherLamplight tile) {
		super(MenuInit.aetherLamplightMenu, windowId, pInv, tile);
		this.tile = tile;
		this.setPInv(pInv, 12, 92);
		this.setSlotSize(0);
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		switch(id) {
		case 4:
		case 5:
			this.tile.addOrder(id);
			break;
		case 7:
			this.tile.setOrder();
			break;
		default:
			this.tile.addRange(id);
			break;
		}
		return true;
	}
}
