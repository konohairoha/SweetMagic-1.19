package sweetmagic.api.iitem.info;

import net.minecraft.world.item.ItemStack;
import sweetmagic.api.iitem.IAcce;

public class AcceInfo extends BaseItemInfo {

	private final IAcce acce;

	public AcceInfo(ItemStack stack) {
		super(stack, IAcce.getAcce(stack).getNBT(stack));
		this.acce = IAcce.getAcce(stack);
	}

	// アクセサリーの取得
	public IAcce getAcce() {
		return this.acce;
	}
}
