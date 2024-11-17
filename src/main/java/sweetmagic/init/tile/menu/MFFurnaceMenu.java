package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.FurnaceSlot;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.sm.TileMFFurnace;

public class MFFurnaceMenu extends BaseSMMenu {

	public final TileMFFurnace tile;

    public MFFurnaceMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
        this(windowId, pInv, (TileMFFurnace) MenuInit.getTile(pInv, data));
    }

	public MFFurnaceMenu(int windowId, Inventory pInv, TileMFFurnace tile) {
		super(MenuInit.mfFurnace_menu, windowId, pInv, tile);
		this.tile = tile;

		this.addSlot(new SMSlot(this.tile.getInput(), 0, 144, 26));

		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 6; x++)
				this.addSlot(new SMSlot(this.tile.getInput(), x + y * 6 + 1, 25 + x * 18, 17 + y * 18));

		for (int y = 0; y < 2; y++)
			for (int x = 0; x < 9; x++)
				this.addSlot(new FurnaceSlot(this.tile, pInv.player, this.tile.getOut(), x + y * 9, 8 + x * 18, 89 + y * 18, (s) -> false));

		this.setPInv(pInv, 8, 130);
		this.setSlotSize(this.tile.getInvSize() * 2 + 1);
	}
}
