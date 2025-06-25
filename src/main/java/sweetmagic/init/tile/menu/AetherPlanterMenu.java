package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.sm.TileAetherPlanter;

public class AetherPlanterMenu extends BaseSMMenu {

	public final TileAetherPlanter tile;

	public AetherPlanterMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, MenuInit.getTile(TileAetherPlanter::new, pInv, data));
	}

	public AetherPlanterMenu(int windowId, Inventory pInv, TileAetherPlanter tile) {
		super(MenuInit.aetherPlanterMenu, windowId, pInv, tile);
		this.tile = tile;

		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 9; x++)
				this.addSlot(new SMSlot(this.tile.getInput(), x + y * 9, 8 + x * 18, 55 + y * 18, s -> false));

		this.setPInv(pInv, 8, 112);
		this.setSlotSize(this.tile.getInvSize());
	}
}
