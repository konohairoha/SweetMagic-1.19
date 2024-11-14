package sweetmagic.api.iitem;

import java.util.Arrays;
import java.util.List;

import net.minecraft.world.item.enchantment.EnchantmentCategory;
import sweetmagic.init.EnchantInit;

public interface IChoker extends IMFTool, ISMArmor {

	public static final List<EnchantmentCategory> ENCHACATELIST = Arrays.<EnchantmentCategory> asList(
		EnchantInit.ISMFTOOL, EnchantInit.ISALL
	);
}
