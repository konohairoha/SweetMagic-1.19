package sweetmagic.api.iblock;

import java.util.List;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import sweetmagic.recipe.RecipeHelper;

public interface ISMNeedItem {

	List<ItemStack> getNeedItemList ();

	List<ItemStack> getNeedHardItemList ();

	Item getNeedHardItem ();

	default boolean isHard (Player player) {
		return !RecipeHelper.getPlayerInv(player, ItemStack.EMPTY).stream().filter(s -> s.is(this.getNeedHardItem())).toList().isEmpty();
	}

	default boolean hasNeedItem (Player player) {

		// プレイヤーのインベントリを取得
		List<ItemStack> stackList = RecipeHelper.getPlayerInv(player, ItemStack.EMPTY);
		boolean hasHardItem = !stackList.stream().filter(s -> s.is(this.getNeedHardItem())).toList().isEmpty();
		List<ItemStack> needItemList = hasHardItem ? this.getNeedHardItemList() : this.getNeedItemList();

		for (ItemStack needStack : needItemList) {

			boolean isCheck = false;

			for (ItemStack stack : stackList) {

				if (needStack.is(stack.getItem()) && stack.getCount() >= needStack.getCount()) {
					isCheck = true;
					break;
				}
			}

			// アイテムが見つからなければ終了
			if (!isCheck) { return false; }
		}

		return true;
	}
}
