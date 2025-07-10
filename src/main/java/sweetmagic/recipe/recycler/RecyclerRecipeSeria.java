package sweetmagic.recipe.recycler;

import java.util.List;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import sweetmagic.recipe.base.AbstractRecipeSerializer;

public class RecyclerRecipeSeria extends AbstractRecipeSerializer implements RecipeSerializer<RecyclerRecipe> {

	// jsonからレシピを生成する
	@Override
	public RecyclerRecipe fromJson(ResourceLocation id, JsonObject json) {
		List<Ingredient> resultList = this.readIngredList(json, "result");		// クラフト後のアイテムリスト取得
		List<Float> chanceList = this.readChanceList(json, "result");			// 要求アイテムの取得チャンス
		List<Ingredient> ingredList = this.readIngredList(json, "ingredients");	// 要求アイテムリストの取得
		List<Integer> countList = this.readCountList(json, "ingredients");		// 要求アイテム個数の取得
		return new RecyclerRecipe(id, resultList, chanceList, ingredList, countList);
	}

	// サーバー側から受け取ったパケットから、レシピを復元する
	@Override
	public @Nullable RecyclerRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
		List<Ingredient> resultList = this.loadIngList(buf);
		List<Float> resultChanceList = this.loadFltList(buf);
		List<Ingredient> ingredList = this.loadIngList(buf);
		List<Integer> countList = this.loadIntList(buf);
		return new RecyclerRecipe(id, resultList, resultChanceList, ingredList, countList);
	}

	// サーバー側からクライアント側に同期するために、レシピをパケットに乗せる
	@Override
	public void toNetwork(FriendlyByteBuf buf, RecyclerRecipe recipe) {
		this.saveIngList(buf, recipe.getResultIngList());
		this.saveFltList(buf, recipe.getChanceList());
		this.saveIngList(buf, recipe.getIngredList());
		this.saveIntList(buf, recipe.getCountList());
	}
}
