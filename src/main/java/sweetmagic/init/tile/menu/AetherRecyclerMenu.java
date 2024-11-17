package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.sm.TileAetherRecycler;

public class AetherRecyclerMenu extends BaseSMMenu {

	public final TileAetherRecycler tile;

    public AetherRecyclerMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
        this(windowId, pInv, (TileAetherRecycler) MenuInit.getTile(pInv, data));
    }

	public AetherRecyclerMenu(int windowId, Inventory pInv, TileAetherRecycler tile) {
		super(MenuInit.aetherRecyclerMenu, windowId, pInv, tile);
		this.tile = tile;

		this.addSlot(new SMSlot(this.tile.getInput(), 0, 143, 35));

		for (int x = 0; x < 6; x++)
			for (int y = 0; y < 4; y++)
				this.addSlot(new SMSlot(this.tile.getHand(), x + y * 6, 25 + x * 18, 10 + y * 18));

		for (int x = 0; x < 9; x++)
			for (int y = 0; y < 3; y++)
				this.addSlot(new SMSlot(this.tile.getOutput(), x + y * 9, 8 + x * 18, 90 + y * 18, s -> false));

		this.setPInv(pInv, 8, 148);
		this.setSlotSize(24 + 1 + this.tile.getInvSize());
	}
}
