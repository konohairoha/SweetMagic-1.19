package sweetmagic.init.tile.inventory;

import net.minecraftforge.items.ItemStackHandler;
import sweetmagic.api.iitem.info.BookInfo;
import sweetmagic.api.iitem.info.PorchInfo;
import sweetmagic.api.iitem.info.SMToolInfo;
import sweetmagic.api.iitem.info.WandInfo;

public class SMInventory {

	public static class SMWandInventory extends BaseSMInventory {

		public SMWandInventory(WandInfo info) {
			super(info);
			this.setInv(new ItemStackHandler(info.getWand().getSlotCount(this.getStack())));
			this.readFromNBT(info.getNBT());
		}
	}

	public static class SMRobeInventory extends BaseSMInventory {

		public SMRobeInventory(SMToolInfo info) {
			super(info);
			this.setInv(new ItemStackHandler(54));
			this.readFromNBT(info.getNBT());
		}
	}

	public static class SMPorchInventory extends BaseSMInventory {

		public SMPorchInventory(PorchInfo info) {
			super(info);
			this.setInv(new ItemStackHandler(info.getPorch().getSlotSize()));
			this.readFromNBT(info.getNBT());
		}
	}

	public static class SMBookInventory extends BaseSMInventory {

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
}
