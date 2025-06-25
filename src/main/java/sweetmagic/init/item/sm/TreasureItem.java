package sweetmagic.init.item.sm;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.item.magic.SMTierItem;

public class TreasureItem extends SMTierItem {

	protected final int data;

	public TreasureItem(String name, int tier, int data) {
		super(name, tier);
		this.data = data;
	}

	// ツールチップの表示
	public void addTip(ItemStack stack, List<Component> toolTip) {

		toolTip.add(this.tierTip(this.tier));

		if (this.data == 2) {
			toolTip.add(this.getText("magicpage").withStyle(GREEN));
		}

		toolTip.add(this.getText(this.name).withStyle(GREEN));

		String tip;

		switch (this.data) {
		case 0:
			tip = "boss_drop";
			break;
		default:
			tip = "dungeon_drop";
			break;
		}

		toolTip.add(this.getText(tip).withStyle(GOLD));
	}
}
