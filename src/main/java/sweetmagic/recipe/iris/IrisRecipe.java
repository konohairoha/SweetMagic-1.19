package sweetmagic.recipe.iris;

import java.util.List;
import java.util.Optional;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import sweetmagic.init.RecipeSerializerInit;
import sweetmagic.init.RecipeTypeInit;
import sweetmagic.recipe.base.AbstractRecipe;

public class IrisRecipe extends AbstractRecipe {

	protected List<Ingredient> ingredList;
	protected List<Integer> countList;

	public IrisRecipe(ResourceLocation id, List<ItemStack> resultList, List<Ingredient> ingredList, List<Integer> countList) {
		super(id, resultList, ingredList, countList);
	}

	// LevelとItemStackのリストを引数に、利用可能なレシピを検索する
	public static Optional<IrisRecipe> getRecipe(Level world, List<ItemStack> ingredList) {
		return AbstractRecipe.getRecipe(world, RecipeTypeInit.IRIS.get()).filter(t -> t.matches(ingredList)).findFirst();
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializerInit.IRIS.get();
	}

	@Override
	public RecipeType<?> getType() {
		return RecipeTypeInit.IRIS.get();
	}

	// クラフト素材リストの取得
	public List<Ingredient> getIngredList() {
		return this.ingredList;
	}

	// クラフト素材リストの宣言
	public void setingredList(List<Ingredient> ingreadList) {
		this.ingredList = ingreadList;
	}

	// クラフト素材個数リストの取得
	public List<Integer> getCountList() {
		return this.countList;
	}

	// クラフト素材個数リストの宣言
	public void setCountList(List<Integer> countList) {
		this.countList = countList;
	}
}