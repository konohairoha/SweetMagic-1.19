package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.slot.SlotInput;
import sweetmagic.init.tile.sm.TileAlternativeTank;

public class AlternativeTankMenu extends BaseSMMenu {

	public final TileAlternativeTank tile;

	public AlternativeTankMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, MenuInit.getTile(TileAlternativeTank::new, pInv, data));
	}

	public AlternativeTankMenu(int windowId, Inventory pInv, TileAlternativeTank tile) {
		super(MenuInit.alternativeTankMenu, windowId, pInv, tile);

		this.tile = tile;
		this.addSlot(new SMSlot(tile.getInput(), 0, 80, 31, SlotInput.ISALTBUCKET));

		this.setPInv(pInv, 8, 96);
		this.setSlotSize(this.tile.getInvSize());
	}
}
