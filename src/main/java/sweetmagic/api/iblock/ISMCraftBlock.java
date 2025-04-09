package sweetmagic.api.iblock;

import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sweetmagic.recipe.RecipeHelper;
import sweetmagic.recipe.RecipeHelper.RecipeUtil;
import sweetmagic.recipe.base.AbstractRecipe;

public interface ISMCraftBlock {

	boolean notNullRecipe(Level world, List<ItemStack> stackList);

	AbstractRecipe getRecipe(Level world, List<ItemStack> stackList);

	default RecipeUtil getItemList(List<ItemStack> stackList, AbstractRecipe recipe) {
		return RecipeHelper.recipePreview(stackList, recipe);
	}

	default boolean canShiftCraft() {
		return false;
	}
}
