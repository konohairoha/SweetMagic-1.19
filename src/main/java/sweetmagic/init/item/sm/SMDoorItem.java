package sweetmagic.init.item.sm;

import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.level.block.Block;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.ItemInit;

public class SMDoorItem extends DoubleHighBlockItem {

	public SMDoorItem(String name, Block block) {
		super(block, SMItem.setItem(SweetMagicCore.smTab));
		ItemInit.itemMap.put(this, name);
	}
}
