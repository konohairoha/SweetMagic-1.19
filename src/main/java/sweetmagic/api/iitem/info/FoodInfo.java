package sweetmagic.api.iitem.info;

import net.minecraft.world.item.ItemStack;
import sweetmagic.api.iitem.IFood;

public class FoodInfo extends BaseItemInfo {

	private final IFood food;

	public FoodInfo(ItemStack stack) {
		super(stack, IFood.getFood(stack).getNBT(stack));
		this.food = IFood.getFood(stack);
	}

	// 本の取得
	public IFood getFood() {
		return this.food;
	}
}
