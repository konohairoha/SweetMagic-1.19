package sweetmagic.init.item.magic;

import sweetmagic.SweetMagicCore;
import sweetmagic.init.ItemInit;
import sweetmagic.init.item.sm.SMItem;

public class SMMagicItem extends SMItem {

	public SMMagicItem (String name) {
		super(name, SweetMagicCore.smMagicTab);
	}

	public SMMagicItem(String name, Properties pro) {
		super(name, pro);
		ItemInit.itemMap.put(this, this.name);
	}
}
