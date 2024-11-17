package sweetmagic.init.item.sm;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import sweetmagic.SweetMagicCore;

public class MagicKey extends SMItem {

	public MagicKey (String name) {
		super(name, SweetMagicCore.smMagicTab);
	}

	// ツールチップの表示
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> toolTip, TooltipFlag flag) {
		toolTip.add(this.getText(this.name).withStyle(GREEN));
	}
}
