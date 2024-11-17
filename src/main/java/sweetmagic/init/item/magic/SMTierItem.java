package sweetmagic.init.item.magic;

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class SMTierItem extends SMMagicItem {

	private final int tier;
	private List<String> nameList = Arrays.<String> asList(
		"aether_crystal_shard", "fluorite", "redberyl"
	);

	public SMTierItem (String name, int tier) {
		super(name);
		this.tier = tier;
	}

	// ツールチップの表示
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> toolTip, TooltipFlag flag) {
		toolTip.add(this.tierTip(this.tier));

		if (this.nameList.contains(this.name)) {
			toolTip.add(this.getText(this.name).withStyle(GOLD));
		}
	}
}
