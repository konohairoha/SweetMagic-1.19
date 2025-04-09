package sweetmagic.init.item.sm;

import sweetmagic.SweetMagicCore;
import sweetmagic.init.ItemInit;

public class SMFoodItem extends SMItem {

	public SMFoodItem(String name) {
		super(name, SweetMagicCore.smFoodTab);
		ItemInit.foodItemList.add(this);
	}
}
