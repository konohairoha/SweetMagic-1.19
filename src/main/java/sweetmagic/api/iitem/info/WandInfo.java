package sweetmagic.api.iitem.info;

import net.minecraft.world.item.ItemStack;
import sweetmagic.api.iitem.IWand;
import sweetmagic.init.tile.inventory.SMInventory.SMWandInventory;

public class WandInfo extends BaseItemInfo {

	private final int level;
	private final IWand wand;

	public WandInfo(ItemStack stack) {
		super(stack, IWand.getWand(stack).getNBT(stack));
		this.wand = IWand.getWand(stack);
		this.level = this.wand.getWandLevel(stack);
	}

	// 杖レベル取得
	public int getLevel() {
		return this.level;
	}

	// 杖の取得
	public IWand getWand() {
		return this.wand;
	}

	// 杖のインベントリ取得
	public SMWandInventory getInv() {
		return new SMWandInventory(this);
	}
}
