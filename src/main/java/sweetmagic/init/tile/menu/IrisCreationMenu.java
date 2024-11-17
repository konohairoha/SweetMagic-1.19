package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.sm.TileIrisCreation;

public class IrisCreationMenu extends BaseSMMenu {

	public final TileIrisCreation tile;

    public IrisCreationMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
        this(windowId, pInv, (TileIrisCreation) MenuInit.getTile(pInv, data));
    }

	public IrisCreationMenu(int windowId, Inventory pInv, TileIrisCreation tile) {
		super(MenuInit.irisMenu, windowId, pInv, tile);
		this.tile = tile;

		IItemHandler input = this.tile.getInput();
		this.addSlot(new SMSlot(this.tile.getHand(), 0, 26, 16, (s) -> true));

		for (int y = 0; y < 2; y++)
			for (int x = 0; x < 4; x++)
			this.addSlot(new SMSlot(input, x + y * 4, 53 + x * 18, 50 + y * 18, (s) -> true));

		this.addSlot(new SMSlot(this.tile.getOutput(), 0, 133, 16, (s) -> false));

		this.setPInv(pInv, 8, 94);
		this.setSlotSize(this.tile.getInvSize() + 2);
	}
}
