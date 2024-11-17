package sweetmagic.tab;

import net.minecraft.world.item.ItemStack;
import sweetmagic.init.ItemInit;

public class SMMagicTab extends SMTab {

	public SMMagicTab (String name) {
		super(name);
	}

	@Override
	public ItemStack makeIcon() {
		return new ItemStack (ItemInit.aether_wand);
	}
}
