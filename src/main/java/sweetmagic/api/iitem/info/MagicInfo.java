package sweetmagic.api.iitem.info;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import sweetmagic.api.iitem.IMagicItem;

public class MagicInfo {

	private final ItemStack stack;
	private final Item item;
	private final IMagicItem magicItem;

	public MagicInfo(ItemStack stack) {
		this.stack = stack;
		this.item = this.stack.getItem();
		this.magicItem = (IMagicItem) this.item;
	}

	public ItemStack getStack() {
		return this.stack;
	}

	public Item getItem() {
		return this.item;
	}

	public IMagicItem getMagicItem() {
		return this.magicItem;
	}
}
