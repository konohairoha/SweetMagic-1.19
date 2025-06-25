package sweetmagic.api.iitem.info;

import net.minecraft.world.item.ItemStack;
import sweetmagic.init.item.sm.Phone;
import sweetmagic.init.tile.inventory.SMInventory.PhoneInventory;

public class PhoneInfo extends BaseItemInfo {

	private final Phone phone;

	public PhoneInfo(ItemStack stack) {
		super(stack, stack.getOrCreateTag());
		this.phone = (Phone) stack.getItem();
	}

	// 本の取得
	public Phone getPhone() {
		return this.phone;
	}

	// 本のインベントリ取得
	public PhoneInventory getInv() {
		return new PhoneInventory(this);
	}
}
