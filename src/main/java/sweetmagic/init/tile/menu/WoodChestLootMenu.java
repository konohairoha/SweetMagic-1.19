package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.sm.TileWoodChest;

public class WoodChestLootMenu extends BaseSMMenu {

	public final TileWoodChest tile;

    public WoodChestLootMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
        this(windowId, pInv, (TileWoodChest) MenuInit.getTile(pInv, data));
    }

	public WoodChestLootMenu(int windowId, Inventory pInv, TileWoodChest tile) {
		super(MenuInit.woodChestLootMenu, windowId, pInv, tile);
		this.tile = tile;
		this.setPInv(pInv, 8, 92);
		this.setSlotSize(0);
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		this.tile.sendPKT();
		return true;
	}
}
