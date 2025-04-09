package sweetmagic.init.block.sm;

import java.util.List;

import net.minecraft.network.chat.Component;
import sweetmagic.init.BlockInit;

public class ChestReader extends SMPlanks {

	public ChestReader(String name) {
		super(name);
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		toolTip.add(this.getText(this.name, BlockInit.aether_crafttable.getName().getString()).withStyle(GREEN));
	}
}
