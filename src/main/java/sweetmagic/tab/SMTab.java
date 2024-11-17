package sweetmagic.tab;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.BlockInit;

public class SMTab extends CreativeModeTab {

	public SMTab(String name) {
        super(CreativeModeTab.TABS.length, name);
    }

	@Override
	public ItemStack makeIcon() {
		return new ItemStack (BlockInit.antique_brick_0);
	}
}
