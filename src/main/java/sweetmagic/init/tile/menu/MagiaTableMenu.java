package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.slot.SlotInput;
import sweetmagic.init.tile.sm.TileMagiaTable;

public class MagiaTableMenu extends BaseSMMenu {

	public final TileMagiaTable tile;

	public MagiaTableMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, MenuInit.getTile(TileMagiaTable::new, pInv, data));
	}

	public MagiaTableMenu(int windowId, Inventory pInv, TileMagiaTable tile) {
		super(MenuInit.magiaTableMenu, windowId, pInv, tile);
		this.tile = tile;

		this.addSlot(new SMSlot(this.tile.getInput(), 0, 29, 8, SlotInput.ISAMAGIC));

		for(int x = 0; x < this.tile.getInvSize(); x++)
			this.addSlot(new SMSlot(this.tile.getSub(), x, 62 + x * 18, 8));

		this.addSlot(new SMSlot(this.tile.getOutput(), 0, 29, 55, (s) -> false));

		this.setPInv(pInv, 8, 109, 0);
		this.setSlotSize(1 + this.tile.getInvSize() + 1);
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		if (this.tile.isCraft) { return false; }
		this.tile.craftStart(this.tile.getInputItem(), false);
		return true;
	}
}
