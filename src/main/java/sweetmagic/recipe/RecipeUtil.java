package sweetmagic.recipe;

import java.util.List;

import net.minecraft.world.item.ItemStack;

public record RecipeUtil(List<ItemStack> inputList, List<ItemStack> resultList) {

	// ハンドの取得
	public ItemStack getHand () {
		return this.getInputList().get(0);
	}

	// 投入リストの取得
	public List<ItemStack> getInputList () {
		return this.inputList;
	}

	// 出力リストの取得
	public List<ItemStack> getResultList () {
		return this.resultList;
	}
}
