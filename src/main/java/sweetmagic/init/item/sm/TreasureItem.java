package sweetmagic.init.item.sm;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import sweetmagic.init.item.magic.SMMagicItem;

public class TreasureItem extends SMMagicItem {

	protected final int data;
	protected final int tier;

	public TreasureItem (String name, int tier, int data) {
		super(name);
		this.data = data;
		this.tier = tier;
	}

	// ツールチップの表示
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> toolTip, TooltipFlag flag) {

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