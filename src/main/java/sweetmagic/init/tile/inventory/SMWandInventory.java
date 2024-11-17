package sweetmagic.init.tile.inventory;

import net.minecraftforge.items.ItemStackHandler;
import sweetmagic.api.iitem.info.WandInfo;

public class SMWandInventory extends BaseSMInventory {

	public SMWandInventory(WandInfo info) {
		super(info);
		this.setInv(new ItemStackHandler(info.getWand().getSlotCount(this.getStack())));
		this.readFromNBT(info.getNBT());
	}
}
