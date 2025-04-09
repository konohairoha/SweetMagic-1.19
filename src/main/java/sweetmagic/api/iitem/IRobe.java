package sweetmagic.api.iitem;

import java.util.Arrays;
import java.util.List;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import sweetmagic.init.EnchantInit;

public interface IRobe extends ISMArmor, IMFTool {

	public static final List<EnchantmentCategory> ENCHACATELIST = Arrays.<EnchantmentCategory> asList(
		EnchantInit.ISMFTOOL, EnchantInit.ISALL, EnchantInit.ISWAND_HARNESS
	);

	// GUIを開く
	void openGui(Level world, Player player, ItemStack stack);

	// SMモブのダメージカット率（1だとダメージカット無し）
	default float getSMMobDamageCut() {
		return 0.67F;
	}

	// 魔法ダメージカット率（1だとダメージカット無し）
	default float getMagicDamageCut() {
		return 0.67F;
	}
}
