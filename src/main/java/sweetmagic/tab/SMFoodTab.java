package sweetmagic.tab;

import net.minecraft.world.item.ItemStack;
import sweetmagic.init.ItemInit;

public class SMFoodTab extends SMTab {

	public SMFoodTab (String name) {
		super(name);
	}

	@Override
	public ItemStack makeIcon() {
		return new ItemStack (ItemInit.strawberry);
	}
}
