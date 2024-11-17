package sweetmagic.recipe.pedal;

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

public class PedalRecipe extends AbstractRecipe {

	protected List<Ingredient> ingredList;
	protected List<Integer> countList;
	protected List<Integer> mfList;
	protected List<Boolean> nbtList;

	public PedalRecipe(ResourceLocation id, List<ItemStack> resultList, List<Ingredient> ingredList, List<Integer> countList, List<Integer> mfList, List<Boolean> keepList) {
		super(id, resultList, ingredList, countList);
		this.setMFList(mfList);
		this.setNBTList(keepList);
	}

	// LevelとItemStackのリストを引数に、利用可能なレシピを検索する
	public static Optional<PedalRecipe> getRecipe(Level world, List<ItemStack> ingredList) {
		return AbstractRecipe.getRecipe(world, RecipeTypeInit.PEDAL.get()).filter(t -> t.matches(ingredList)).findFirst();
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializerInit.PEDAL.get();
	}

	@Override
	public RecipeType<?> getType() {
		return RecipeTypeInit.PEDAL.get();
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

	// MF要求リストの取得
	public List<Integer> getMFList () {
		return this.mfList;
	}

	// MF要求リストの設定
	public void setMFList(List<Integer> mfList) {
		this.mfList = mfList;
	}

	// NBT保持リストの取得
	public List<Boolean> getNBTList () {
		return this.nbtList;
	}

	// NBT保持リストの設定
	public void setNBTList(List<Boolean> nbtList) {
		this.nbtList = nbtList;
	}
}
