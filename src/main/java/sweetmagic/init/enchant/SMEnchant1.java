package sweetmagic.init.enchant;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import sweetmagic.init.EnchantInit;

public class SMEnchant1 extends Enchantment {

	private static final EquipmentSlot[] MAIN = { EquipmentSlot.MAINHAND };

	public SMEnchant1(String name, EnchantmentCategory cate, Enchantment.Rarity rare) {
		super(rare, cate, MAIN);
		EnchantInit.enchaMap.put(this, name);
	}

	public int getMinCost(int cost) {
	      return 25;
	}

	public int getMaxCost(int cost) {
		return super.getMinCost(cost) + 75;
	}

	public int getMaxLevel() {
		return 1;
	}

	public boolean canEnchant(ItemStack stack) {
		return super.canEnchant(stack);
	}
}
