package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.sm.TileMagicBarrier;

public class MagicBarrierMenu extends BaseSMMenu {

	public final TileMagicBarrier tile;

    public MagicBarrierMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
        this(windowId, pInv, (TileMagicBarrier) MenuInit.getTile(pInv, data));
    }

	public MagicBarrierMenu(int windowId, Inventory pInv, TileMagicBarrier tile) {
		super(MenuInit.magicBarrierMenu, windowId, pInv, tile);
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
