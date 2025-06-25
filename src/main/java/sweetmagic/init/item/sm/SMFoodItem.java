package sweetmagic.init.item.sm;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit;
import sweetmagic.init.ItemInit;

public class SMFoodItem extends SMItem {

	private final int data;

	public SMFoodItem(String name) {
		super(name, SweetMagicCore.smFoodTab);
		ItemInit.foodItemList.add(this);
		this.data = 0;
	}

	public SMFoodItem(String name, int data) {
		super(name, SweetMagicCore.smFoodTab);
		ItemInit.foodItemList.add(this);
		this.data = data;
	}

	// ツールチップの表示
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> toolTip, TooltipFlag flag) {
		if(this.data == 0) { return; }
		toolTip.add(this.getText(this.name, BlockInit.maple_hole_log.getName().getString()).withStyle(GREEN));
	}
}
