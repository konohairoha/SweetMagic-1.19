package sweetmagic.recipe;

import java.util.List;

import net.minecraft.world.item.ItemStack;

public class RecipeInfo {

	private final List<ItemStack> stackList;
	private ItemStack page;
	private ItemStack base;

	public RecipeInfo (List<ItemStack> stackList) {
		this.stackList = stackList;
	}

	public RecipeInfo (List<ItemStack> stackList, ItemStack page, ItemStack base) {
		this.stackList = stackList;
		this.page = page;
		this.base = base;
	}

	public List<ItemStack> getStackList () {
		return this.stackList;
	}

	public ItemStack getPage() {
		return this.page;
	}

	public ItemStack getBase() {
		return this.base;
	}

	// 要求アイテムがすべてそろっているなら
	public boolean isCompleted () {
		return !this.getStackList().isEmpty();
	}
}
