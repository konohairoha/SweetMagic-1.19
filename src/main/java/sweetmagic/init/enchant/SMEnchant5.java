package sweetmagic.init.enchant;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import sweetmagic.api.iitem.IWand;
import sweetmagic.init.EnchantInit;

public class SMEnchant5 extends Enchantment {

	private static final EquipmentSlot[] MAIN = { EquipmentSlot.MAINHAND };

	public SMEnchant5(String name, EnchantmentCategory cate, Enchantment.Rarity rare) {
		super(rare, cate, MAIN);
		EnchantInit.enchaMap.put(this, name);
	}

	public int getMinCost(int cost) {
		return 1 + 10 * (cost - 1);
	}

	public int getMaxCost(int cost) {
		return super.getMinCost(cost) + 50;
	}

	public int getMaxLevel() {
		return 5;
	}

	public boolean canEnchant(ItemStack stack) {

		if (this == EnchantInit.elementBonus) {
			return stack.getItem() instanceof IWand wand && wand.getWandTier() >= 5 ? true : false;
		}

		return stack.getItem() instanceof IWand ? true : super.canEnchant(stack);
	}
}
