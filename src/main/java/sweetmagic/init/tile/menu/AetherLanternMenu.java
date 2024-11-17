package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.sm.TileAetherLanp;

public class AetherLanternMenu extends BaseSMMenu {

	public final TileAetherLanp tile;

    public AetherLanternMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
        this(windowId, pInv, (TileAetherLanp) MenuInit.getTile(pInv, data));
    }

	public AetherLanternMenu(int windowId, Inventory pInv, TileAetherLanp tile) {
		super(MenuInit.aetherLanternMenu, windowId, pInv, tile);
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
