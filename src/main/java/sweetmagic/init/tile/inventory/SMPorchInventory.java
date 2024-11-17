package sweetmagic.init.tile.inventory;

import net.minecraftforge.items.ItemStackHandler;
import sweetmagic.api.iitem.info.PorchInfo;

public class SMPorchInventory extends BaseSMInventory {

	public SMPorchInventory(PorchInfo info) {
		super(info);
		this.setInv(new ItemStackHandler(info.getPorch().getSlotSize()));
		this.readFromNBT(info.getNBT());
	}
}
