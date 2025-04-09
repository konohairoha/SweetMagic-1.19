package sweetmagic.api.iitem;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import sweetmagic.api.util.ISMTip;

public interface ISMArmor extends ISMTip {

	// エンチャレベル取得
	default int getEnchantLevel(Enchantment enchant, ItemStack stack) {
		return Math.min(EnchantmentHelper.getItemEnchantmentLevel(enchant, stack), 10);
	}

	int getTier();
}
