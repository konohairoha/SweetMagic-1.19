package sweetmagic.init.tile.inventory;

import net.minecraftforge.items.ItemStackHandler;
import sweetmagic.api.iitem.info.BookInfo;

public class SMBookInventory extends BaseSMInventory {

	public SMBookInventory(BookInfo info) {
		super(info);
		this.setInv(new ItemStackHandler(info.getBook().getSlotSize()));
		this.readFromNBT(info.getNBT());
	}

	@Override
	public int getSlotLimit(int slot) {
		return 1;
	}
}
