package sweetmagic.api.iitem.info;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class BaseItemInfo {

	protected final ItemStack stack;
	protected final CompoundTag tags;

	public BaseItemInfo (ItemStack stack, CompoundTag tags) {
		this.stack = stack;
		this.tags = tags;
	}

	// NBTの取得
	public CompoundTag getNBT () {
		return this.tags;
	}

	// アイテムの取得
	public ItemStack getStack () {
		return this.stack;
	}

	// アイテムの取得
	public Item getItem () {
		return this.getStack().getItem();
	}
}
