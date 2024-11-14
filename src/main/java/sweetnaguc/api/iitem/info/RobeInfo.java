package sweetmagic.api.iitem.info;

import net.minecraft.world.item.ItemStack;
import sweetmagic.api.iitem.IRobe;
import sweetmagic.init.EnchantInit;

public class RobeInfo extends BaseItemInfo {

	private final IRobe robe;

	public RobeInfo (ItemStack stack) {
		super(stack, stack.getOrCreateTag());
		this.robe = (IRobe) stack.getItem();
	}

	// ポーチの取得
	public IRobe getRobe () {
		return this.robe;
	}

	public void shrinkMF (int useMF) {

		IRobe robe = this.getRobe();
		ItemStack stack = this.getStack();
		int costDown = Math.min(99, robe.getEnchantLevel(EnchantInit.mfCostDown, stack) * 7);

		if (costDown > 0) {
			useMF *= (100 - costDown) / 100F;
		}

		robe.setMF(stack, robe.getMF(stack) - useMF);
	}
}
