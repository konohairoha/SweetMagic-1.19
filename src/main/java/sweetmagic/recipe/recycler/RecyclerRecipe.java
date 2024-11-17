package sweetmagic.recipe.recycler;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import sweetmagic.init.RecipeSerializerInit;
import sweetmagic.init.RecipeTypeInit;
import sweetmagic.recipe.base.AbstractRecipe;

public class RecyclerRecipe extends AbstractRecipe {

	protected List<Ingredient> ingredList;
	protected List<Integer> countList;
	protected List<Float> chanceList;

	public RecyclerRecipe(ResourceLocation id, List<Ingredient> resultIngList, List<Float> chanceList, List<Ingredient> ingredList, List<Integer> countList) {
		super(id, ingredList, countList, true);
		this.setResultIngList(ImmutableList.copyOf(resultIngList));
		this.setChanceList(chanceList);
		if (resultIngList.isEmpty()) { throw new IllegalArgumentException("Recipe has no result: " + id.toString()); }
	}

	// LevelとItemStackのリストを引数に、利用可能なレシピを検索する
	public static Optional<RecyclerRecipe> getRecipe(Level world, List<ItemStack> ingredList) {
		return AbstractRecipe.getRecipe(world, RecipeTypeInit.RECYCLER.get()).filter(t -> t.matches(ingredList)).findFirst();
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializerInit.RECYCLER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return RecipeTypeInit.RECYCLER.get();
	}

	public ItemStack getResultItem() {
		return this.getResultIngList().get(0).getItems()[0].copy();
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
