package sweetmagic.recipe.mill;

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

public class MillRecipe extends AbstractRecipe {

	protected List<Ingredient> ingredList;
	protected List<Integer> countList;
	protected List<Float> chanceList;

	public MillRecipe(ResourceLocation id, List<ItemStack> resultList, List<Float> chanceList, List<Ingredient> ingredList, List<Integer> countList) {
		super(id, resultList, ingredList, countList);
		this.setChanceList(chanceList);
	}

	// LevelとItemStackのリストを引数に、利用可能なレシピを検索する
	public static Optional<MillRecipe> getRecipe(Level world, List<ItemStack> ingredList) {
		return AbstractRecipe.getRecipe(world, RecipeTypeInit.MILL).filter(t -> t.matches(ingredList)).findFirst();
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializerInit.MILL;
	}

	@Override
	public RecipeType<?> getType() {
		return RecipeTypeInit.MILL;
	}

	// クラフト後のチャンスを取得
	public List<Float> getChanceList() {
		return this.chanceList;
	}

	// クラフト後のチャンスを設定
	public void setChanceList(List<Float> chanceList) {
		this.chanceList = chanceList;
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
