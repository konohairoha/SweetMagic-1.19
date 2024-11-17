package sweetmagic.recipe.base;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import sweetmagic.recipe.RecipeInfo;
import sweetmagic.recipe.RecipeMathCheck;

public abstract class AbstractRecipe implements Recipe<Container> {

	protected final ResourceLocation id;
	protected List<Ingredient> resultIngList;
	protected final List<ItemStack> resultList;
	protected List<ItemStack> requestStackList = new ArrayList<>();
	protected boolean tagResult = false;

	public AbstractRecipe(ResourceLocation id, List<ItemStack> resultList, List<Ingredient> ingredList, List<Integer> countList) {
		this.id = id;
		this.resultList = resultList;
		this.setingredList(ImmutableList.copyOf(ingredList));
		this.setCountList(countList);
		if (resultList.isEmpty()) { throw new IllegalArgumentException("Recipe has no result: " + id.toString()); }
		if (ingredList.isEmpty()) { throw new IllegalArgumentException("Recipe has no ingredients: " + id.toString()); }
	}

	public AbstractRecipe(ResourceLocation id, List<Ingredient> ingredList, List<Integer> countList, boolean flag) {
		this.id = id;
		this.resultList = new ArrayList<>();
		this.setingredList(ImmutableList.copyOf(ingredList));
		this.setCountList(countList);
		this.tagResult = true;
		if (ingredList.isEmpty()) { throw new IllegalArgumentException("Recipe has no ingredients: " + id.toString()); }
	}

	// containerでチェックする場合必要だが不要
	@Override
	public boolean matches(Container container, Level world) {
		throw new UnsupportedOperationException("Use a method that takes a List<ItemStack> as an argument.");
	}

	// containerでチェックする場合必要だが不要
	@Override
	public ItemStack assemble(Container container) {
		throw new UnsupportedOperationException("Use a method that takes a List<ItemStack> as an argument.");
	}

	// containerでチェックする場合必要だが不要
	@Override
	public boolean canCraftInDimensions(int width, int height) {
		throw new UnsupportedOperationException();
	}

	// 要求アイテムが足りているかどうか
	public boolean matches(List<ItemStack> stackList) {
		RecipeInfo recipeInfo = RecipeMathCheck.checkRecipe(stackList, this.getIngredList(), this.getCountList());
		boolean isComplet = recipeInfo != null && recipeInfo.isCompleted();

		if (isComplet) {
			this.requestStackList = recipeInfo.getStackList();
		}

		return isComplet;
	}

	// 完成品のアイテムを取得
	public List<ItemStack> getResultList() {
		return new ArrayList<ItemStack>(this.resultList);
	}

	// クラフト素材リストの宣言
	public void setResultIngList(List<Ingredient> ingreadList) {
		this.resultIngList = ingreadList;
	}

	// 完成品のアイテムを取得
	public List<Ingredient> getResultIngList() {
		return new ArrayList<Ingredient>(this.resultIngList);
	}

	// 完成品のItemStackを取得
	@Override
	public ItemStack getResultItem() {
		return this.resultList.get(0).copy();
	}

	// 要求アイテムリストを取得
	public List<ItemStack> getRequestList () {
		return this.requestStackList;
	}

	// クラフト素材リストの取得
	public abstract List<Ingredient> getIngredList();

	// クラフト素材リストの宣言
	public abstract void setingredList(List<Ingredient> ingreadList);

	// クラフト素材個数リストの取得
	public abstract List<Integer> getCountList();

	// クラフト素材個数リストの宣言
	public abstract void setCountList(List<Integer> countList);

	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	public boolean isTagResult () {
		return this.tagResult;
	}

	public static <C extends Container, T extends Recipe<C>> Stream<T> getRecipe(Level world, RecipeType<T> recipeType) {
		return world.getRecipeManager().getAllRecipesFor(recipeType).stream();
	}

	@Override
	public abstract RecipeSerializer<?> getSerializer();

	@Override
	public abstract RecipeType<?> getType();
}
