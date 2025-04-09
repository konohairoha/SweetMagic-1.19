package sweetmagic.init.tile.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.slot.SlotInput;
import sweetmagic.init.tile.sm.TileAccessoryTable;

public class AccessoryTableMenu extends BaseSMMenu {

	public final TileAccessoryTable tile;
	public final Slot starSlot;

	public AccessoryTableMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, (TileAccessoryTable) MenuInit.getTile(pInv, data));
	}

	public AccessoryTableMenu(int windowId, Inventory pInv, TileAccessoryTable tile) {
		super(MenuInit.accessoryProcessingMenu, windowId, pInv, tile);
		this.tile = tile;

		this.addSlot(new SMSlot(this.tile.getInput(), 0, 44, 16, SlotInput.CANACCE));
		this.addSlot(new SMSlot(this.tile.getAcce(), 0, 70, 42, SlotInput.ISDUPACCE));
		this.starSlot = this.addSlot(new SMSlot(this.tile.getStar(), 0, 70, 64, SlotInput.ISSTAR));

		this.addSlot(new SMSlot(this.tile.getOutput(), 0, 134, 17, (s) -> false));

		this.setPInv(pInv, 8, 90);
		this.setSlotSize(this.tile.getInvSize() * 4);
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		if (this.tile.isCraft || !this.tile.canCraft()) { return false; }

		// 作成開始
		this.tile.craftStart();
		return true;
	}
}
