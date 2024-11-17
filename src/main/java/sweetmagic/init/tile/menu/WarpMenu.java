package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.slot.SlotInput;
import sweetmagic.init.tile.sm.TileWarp;

public class WarpMenu extends BaseSMMenu {

	public final TileWarp tile;
	public final Slot invSlot[] = new Slot[4];

    public WarpMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
        this(windowId, pInv, (TileWarp) MenuInit.getTile(pInv, data));
    }

	public WarpMenu(int windowId, Inventory pInv, TileWarp tile) {
		super(MenuInit.warpMenu, windowId, pInv, tile);
		this.tile = tile;

		IItemHandler input = this.tile.getInput();

		for (int x = 0; x < 4; x++)
				this.invSlot[x] = this.addSlot(new SMSlot(input, x, 25 + 36 * x, 12, SlotInput.ISCLERO));

		this.setPInv(pInv, 7, 52);
		this.setSlotSize(this.tile.getInvSize());
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		this.tile.doTereport(player, id);
		return true;
	}
}
