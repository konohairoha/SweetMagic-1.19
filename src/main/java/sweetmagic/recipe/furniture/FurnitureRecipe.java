package sweetmagic.recipe.furniture;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import sweetmagic.init.RecipeSerializerInit;
import sweetmagic.init.RecipeTypeInit;
import sweetmagic.recipe.base.AbstractRecipe;

public class FurnitureRecipe extends AbstractRecipe {

	protected List<Ingredient> ingredList;

	public FurnitureRecipe(ResourceLocation id, List<ItemStack> resultList, List<Ingredient> ingredList) {
		super(id, resultList, ingredList, Arrays.<Integer> asList(1));
	}

	public boolean matches(Container con, Level world) {
		return this.ingredList.get(0).test(con.getItem(0));
	}

	// LevelとItemStackのリストを引数に、利用可能なレシピを検索する
	public static Optional<FurnitureRecipe> getRecipe(Level world, List<ItemStack> ingredList) {
		return AbstractRecipe.getRecipe(world, RecipeTypeInit.FURNITURE).filter(t -> t.matches(ingredList)).findFirst();
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializerInit.FURNITURE;
	}

	@Override
	public RecipeType<?> getType() {
		return RecipeTypeInit.FURNITURE;
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
		return Arrays.<Integer> asList(1);
	}

	// クラフト素材個数リストの宣言
	public void setCountList(List<Integer> countList) { }
}
