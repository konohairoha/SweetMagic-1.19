package sweetmagic.init.item.magic;

import java.util.Arrays;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import sweetmagic.api.iitem.ITier;

public class SMTierItem extends SMMagicItem implements ITier {

	protected final int tier;
	private List<String> nameList = Arrays.<String> asList(
		"aether_crystal_shard", "fluorite", "redberyl"
	);

	public SMTierItem (String name, int tier) {
		super(name);
		this.tier = tier;
	}

	public int getTier() {
		return this.tier;
	}

	// ツールチップの表示
	@Override
	public void addTip(ItemStack stack, List<Component> toolTip) {
		toolTip.add(this.tierTip(this.tier));

		if (this.nameList.contains(this.name)) {
			toolTip.add(this.getText(this.name).withStyle(GOLD));
		}
	}
}
