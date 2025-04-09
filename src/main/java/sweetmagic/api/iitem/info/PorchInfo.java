package sweetmagic.api.iitem.info;

import net.minecraft.world.item.ItemStack;
import sweetmagic.api.iitem.IPorch;
import sweetmagic.init.tile.inventory.SMInventory.SMPorchInventory;

public class PorchInfo extends BaseItemInfo {

	private final IPorch porch;

	public PorchInfo(ItemStack stack) {
		super(stack, stack.getOrCreateTag());
		this.porch = (IPorch) stack.getItem();
	}

	// ポーチの取得
	public IPorch getPorch() {
		return this.porch;
	}

	// ポーチのインベントリ取得
	public SMPorchInventory getInv() {
		return new SMPorchInventory(this);
	}
}
