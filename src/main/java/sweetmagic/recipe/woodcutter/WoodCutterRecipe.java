package sweetmagic.recipe.woodcutter;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import sweetmagic.init.RecipeSerializerInit;
import sweetmagic.init.RecipeTypeInit;
import sweetmagic.recipe.base.AbstractRecipe;

public class WoodCutterRecipe extends AbstractRecipe {

	protected List<Ingredient> ingredList;
	protected List<Integer> countList;
	protected int mf;
	protected List<Integer> minList;
	protected List<Integer> maxList;

	public WoodCutterRecipe(ResourceLocation id, List<ItemStack> resultList, List<Ingredient> ingredList, List<Integer> countList, List<Integer> minList, List<Integer> maxList, int mf) {
		super(id, resultList, ingredList, countList);
		this.setMF(mf);
		this.setMinList(minList);
		this.setMaxList(maxList);
	}

	// LevelとItemStackのリストを引数に、利用可能なレシピを検索する
	public static Optional<WoodCutterRecipe> getRecipe(Level world, List<ItemStack> ingredList) {
		return AbstractRecipe.getRecipe(world, RecipeTypeInit.WOODCUTTER).filter(t -> t.matches(ingredList)).findFirst();
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializerInit.WOODCUTTER;
	}

	@Override
	public RecipeType<?> getType() {
		return RecipeTypeInit.WOODCUTTER;
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

	// 最低値リストの取得
	public List<Integer> getMinList() {
		return this.minList;
	}

	// 最低値リストの設定
	public void setMinList(List<Integer> minList) {
		this.minList = minList;
	}

	// 最高値リストの取得
	public List<Integer> getMaxList() {
		return this.maxList;
	}

	// 最高値リストの設定
	public void setMaxList(List<Integer> maxList) {
		this.maxList = maxList;
	}

	// MF要求リストの取得
	public int getMF() {
		return this.mf;
	}

	// MF要求リストの設定
	public void setMF(int mf) {
		this.mf = mf;
	}

	public int getCount(Random rand, int array) {
		int min = this.getMinList().get(array);
		return min + rand.nextInt(Math.max(1, this.getMaxList().get(array) - min));
	}
}
