package sweetmagic.api.iitem.info;

import net.minecraft.world.item.ItemStack;
import sweetmagic.api.iitem.IMagicBook;
import sweetmagic.init.tile.inventory.SMBookInventory;

public class BookInfo extends BaseItemInfo {

	private final IMagicBook book;

	public BookInfo (ItemStack stack) {
		super(stack, IMagicBook.getBook(stack).getNBT(stack));
		this.book = IMagicBook.getBook(stack);
	}

	// 本の取得
	public IMagicBook getBook () {
		return this.book;
	}

	// 本のインベントリ取得
	public SMBookInventory getInv() {
		return new SMBookInventory(this);
	}
}
