package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.sm.TileMFFisher;

public class MFFisherMenu extends BaseSMMenu {

	public final TileMFFisher tile;

    public MFFisherMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
        this(windowId, pInv, (TileMFFisher) MenuInit.getTile(pInv, data));
    }

	public MFFisherMenu(int windowId, Inventory pInv, TileMFFisher tile) {
		super(MenuInit.mfFisherMenu, windowId, pInv, tile);
		this.tile = tile;

		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 9; x++)
				this.addSlot(new SMSlot(this.tile.getInput(), x + y * 9, 8 + x * 18, 55 + y * 18, s -> false));

		this.setPInv(pInv, 8, 117);
		this.setSlotSize(this.tile.getInvSize());
	}
}
