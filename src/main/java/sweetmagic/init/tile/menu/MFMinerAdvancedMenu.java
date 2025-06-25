package sweetmagic.init.tile.menu;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.sm.TileMFMinerAdvanced;

public class MFMinerAdvancedMenu extends BaseSMMenu {

	public final TileMFMinerAdvanced tile;
	public final List<Slot> stoneSlotList = new ArrayList<>();;

	public MFMinerAdvancedMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, MenuInit.getTile(TileMFMinerAdvanced::new, pInv, data));
	}

	public MFMinerAdvancedMenu(int windowId, Inventory pInv, TileMFMinerAdvanced tile) {
		super(MenuInit.mfMinerMenu, windowId, pInv, tile);
		this.tile = tile;

		for (int y = 0; y < 2; y++) {
			for (int x = 0; x < 5; x++) {
				SMSlot slot= new SMSlot(this.tile.getInput(), x + y * 5, 43 + x * 18, 23 + y * 18);
				this.stoneSlotList.add(slot);
				this.addSlot(slot);
			}
		}

		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 9; x++)
				this.addSlot(new SMSlot(this.tile.getOut(), x + y * 9, 8 + x * 18, 89 + y * 18, s -> false));

		this.setPInv(pInv, 8, 146);
		this.setSlotSize(this.tile.getInvSize() + 10);
	}
}
