package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.sm.TileAetherLamplight;

public class AetherLamplightMenu extends BaseSMMenu {

	public final TileAetherLamplight tile;

    public AetherLamplightMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
        this(windowId, pInv, (TileAetherLamplight) MenuInit.getTile(pInv, data));
    }

	public AetherLamplightMenu(int windowId, Inventory pInv, TileAetherLamplight tile) {
		super(MenuInit.aetherLamplightMenu, windowId, pInv, tile);
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
