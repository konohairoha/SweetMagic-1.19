package sweetmagic.util;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.entity.ai.DonMovGoal;

public class SMUtil {

	public static final EquipmentSlot[] EQUIPMENT_SLOTS = new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };

	public static EquipmentSlot getEquipmentSlot(int index) {
		return index >= 0 && index < EQUIPMENT_SLOTS.length ? EQUIPMENT_SLOTS[index] : null;
	}

	/**
	 * Inventoryからアイテムを探索する　メタデータいらない方		　static参照可能にしといた
	 * @param inv			プレイヤーのInventoryを参照するように設定
	 * @param item			Item
	 * @param minAmount	byte 最低個数　0にすると可変個数を取得するように
	 * @return	Object[]	Inventoryのポインタ[0]、アイテム情報(ItemStack)[1]、スタック数(int)[2]
	 */
	public static Object[] getStackFromPInv(NonNullList<ItemStack> inv, Item item, byte minAmount) {

		Object[] obj = new Object[3];
		for (int i = 0; i < inv.size(); i++) {

			ItemStack stack = inv.get(i);

			if (!stack.isEmpty() && (stack.getCount() >= minAmount || minAmount == 0) && stack.getItem() == item) {
				obj[0] = i;
				obj[1] = stack;
				obj[2] = stack.getCount();
				return obj;
			}
		}
		return null;
	}

	public static ItemStack getStack(Object obj) {
		return (ItemStack) obj;
	}

	public static Item getItem(Object obj) {
		return getStack(obj).getItem();
	}

	// 敵AIを動かさない
	public static void tameAIDonmov(Mob target, int tickTime) {

		boolean isLearning = false;
		for (WrappedGoal entry : target.goalSelector.getAvailableGoals()) {
			if (!(entry.getGoal() instanceof DonMovGoal goal)) { continue; }

			goal.tickTime = tickTime;
			isLearning = true;
			break;
		}

		if (isLearning) { return; }

		Set<WrappedGoal> goal = new HashSet<>(target.goalSelector.getAvailableGoals());
		target.goalSelector.removeAllGoals();
		DonMovGoal ai = new DonMovGoal(target, goal);
		ai.tickTime = tickTime;
		target.goalSelector.addGoal(0, ai);
		target.targetSelector.addGoal(0, ai);
	}
}
