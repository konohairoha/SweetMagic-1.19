package sweetmagic.api.iitem.info;

import net.minecraft.world.item.ItemStack;
import sweetmagic.api.iitem.IMFTool;

public class SMToolInfo extends BaseItemInfo {

	private final IMFTool mfTool;

	public SMToolInfo (ItemStack stack) {
		super(stack, ((IMFTool) stack.getItem()).getNBT(stack));
		this.mfTool = (IMFTool) stack.getItem();
	}

	// 杖の取得
	public IMFTool getMFTool() {
		return this.mfTool;
	}
}
