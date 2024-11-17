package sweetmagic.recipe.obmagia;

import java.util.Arrays;
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
import sweetmagic.recipe.RecipeInfo;
import sweetmagic.recipe.RecipeMathCheck;
import sweetmagic.recipe.base.AbstractRecipe;

public class ObMagiaRecipe extends AbstractRecipe {

	protected List<Ingredient> ingredList;
	protected List<Integer> countList;
	protected Ingredient pageList;
	protected Ingredient baseList;
	protected ItemStack requestPage;
	protected ItemStack requestBase;
	protected int craftTime;

	public ObMagiaRecipe(ResourceLocation id, ItemStack result, Ingredient pageList, Ingredient baseList, List<Ingredient> ingredList, List<Integer> countList, int craftTime) {
		super(id, Arrays.<ItemStack> asList(result), ingredList, countList);
		this.pageList = pageList;
		this.baseList = baseList;
		this.setCraftTime(craftTime);
	}

	// LevelとItemStackのリストを引数に、利用可能なレシピを検索する
	public static Optional<ObMagiaRecipe> getRecipe(Level world, List<ItemStack> ingredList, ItemStack page, ItemStack base) {
		return AbstractRecipe.getRecipe(world, RecipeTypeInit.OBMAGIA.get()).filter(t -> t.matches(ingredList, page, base)).findFirst();
	}

	// 要求アイテムが足りているかどうか
	public boolean matches(List<ItemStack> stackList, ItemStack page, ItemStack base) {
		RecipeInfo recipeInfo = RecipeMathCheck.checkObMagiaRecipe(stackList, page, base, this.getIngredList(), this.getCountList(), this.pageList, this.baseList);
		boolean isComplet = recipeInfo != null && recipeInfo.isCompleted();

		if (isComplet) {
			this.requestStackList = recipeInfo.getStackList();
			this.setPage(recipeInfo.getPage());
			this.setBase(recipeInfo.getBase());
		}

		return isComplet;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializerInit.OBMAGIA.get();
	}

	@Override
	public RecipeType<?> getType() {
		return RecipeTypeInit.OBMAGIA.get();
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

	public Ingredient getPageList () {
		return this.pageList;
	}

	// 要求する紙の取得
	public ItemStack getPage() {
		return this.requestPage;
	}

	// 要求する紙の設定
	public void setPage (ItemStack page) {
		this.requestPage = page;
	}

	public Ingredient getBaseList () {
		return this.baseList;
	}

	// 要求するベースアイテムの取得
	public ItemStack getBase() {
		return this.requestBase;
	}

	// 要求する紙の設定
	public void setBase (ItemStack base) {
		this.requestBase = base;
	}

	// クラフト時間の取得
	public int getCraftTime() {
		return this.craftTime;
	}

	// クラフト時間の設定
	public void setCraftTime (int craftTime) {
		this.craftTime = craftTime;
	}
}
