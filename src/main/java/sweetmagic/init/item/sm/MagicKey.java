package sweetmagic.init.item.sm;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import sweetmagic.SweetMagicCore;

public class MagicKey extends SMItem {

	public MagicKey(String name) {
		super(name, SweetMagicCore.smMagicTab);
	}

	// ツールチップの表示
	public void addTip(ItemStack stack, List<Component> toolTip) {
		toolTip.add(this.getText(this.name).withStyle(GREEN));
	}
}
