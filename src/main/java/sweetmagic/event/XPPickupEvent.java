package sweetmagic.event;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sweetmagic.api.iitem.IMFTool;
import sweetmagic.api.iitem.IPorch;
import sweetmagic.init.EnchantInit;
import sweetmagic.init.ItemInit;

public class XPPickupEvent {

	private static final EquipmentSlot[] ARMORSLOT = new EquipmentSlot[] {
		EquipmentSlot.MAINHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
	};

	// 経験値取得イベント
	@SubscribeEvent
	public static void onBulletRenderEvent(PlayerXpEvent.PickupXp event) {
		int xpValue = event.getOrb().value;
		Player player = event.getEntity();
		if (player == null || xpValue < 1) { return; }

		for (EquipmentSlot slot : ARMORSLOT) {

			ItemStack stack = player.getItemBySlot(slot);
			if (stack.isEmpty()) { continue; }

			if (stack.getItem() instanceof IMFTool mfTool) {

				// MFが満タンなら次へ
				if (mfTool.isMaxMF(stack)) { continue; }

				XPPickupEvent.healRepair(player, stack, mfTool, xpValue);
			}

			else if (stack.getItem() instanceof IPorch porch) {
				XPPickupEvent.emelaldPiasEffect(player, stack, porch, xpValue);
			}
		}
	}

	// エーテルヒールによる回復
	public static boolean healRepair (Player player, ItemStack stack, IMFTool mfTool, int value) {

		// MF回復エンチャがついていないかエンチャが0以下かMFが最大値なら終了
		int level = EnchantmentHelper.getItemEnchantmentLevel(EnchantInit.aetherheal, stack);
		if (level <= 0 || mfTool.isMaxMF(stack)) { return false; }

		// エンチャレベル分増やす
		value = value >= 4 ? value / 3 : value;
		value *= level;

		// 取得した経験値分MFを増やす
		mfTool.insetMF(stack, value);
		return true;
	}

	// エーテルヒールによる回復
	public static void emelaldPiasEffect (Player player, ItemStack stack, IPorch porch, int value) {
		int count = porch.acceCount(stack, ItemInit.emelald_pias, 10);
		if (count <= 0) { return; }

		float addXPRate = count * 0.1F;
		player.giveExperiencePoints((int) (value * addXPRate));
	}
}
