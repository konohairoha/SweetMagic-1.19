package sweetmagic.init.tile.inventory;

import net.minecraftforge.items.ItemStackHandler;
import sweetmagic.api.iitem.info.SMToolInfo;

public class SMRobeInventory extends BaseSMInventory {

	public SMRobeInventory(SMToolInfo info) {
		super(info);
		this.setInv(new ItemStackHandler(54));
		this.readFromNBT(info.getNBT());
	}
}
