package sweetmagic.init.item.sm;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import sweetmagic.api.iitem.ITier;
import sweetmagic.init.item.magic.SMMagicItem;

public class SMDropItem extends SMMagicItem implements ITier {

	private final int data;

	public SMDropItem(String name, int data) {
		super(name);
		this.data = data;
	}

	public int getTier() {
		return this.data == 6 ? 2 : 1;
	}

	// ツールチップの表示
	public void addTip(ItemStack stack, List<Component> toolTip) {

		String mobName = "";

		switch (this.data) {
		case 1:
			mobName = "blazetempest";
			break;
		case 2:
			mobName = "archspider";
			break;
		case 3:
			mobName = "electriccube";
			break;
		case 4:
			mobName = "creepercalamity";
			break;
		case 5:
			mobName = "endermage";
			break;
		case 6:
			mobName = "windwitch";
			break;
		default:
			mobName = "skullfrost";
			break;
		}

		mobName = "entity.sweetmagic." + mobName;
		toolTip.add(this.getTipArray(this.getText("dropmob"), ": ", this.getTip(mobName).withStyle(WHITE)).withStyle(GREEN));
	}
}
