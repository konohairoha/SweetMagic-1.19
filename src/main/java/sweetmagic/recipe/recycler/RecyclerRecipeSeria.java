package sweetmagic.recipe.recycler;

import java.util.ArrayList;
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
	public @Nullable RecyclerRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {

		int resultSize = buffer.readVarInt();
		List<Ingredient> resultList = new ArrayList<>();

		for (int k = 0; k < resultSize; k++) {
			resultList.add(Ingredient.fromNetwork(buffer));
		}

		// クラフト後のチャンスを取得
		int chanceSize = buffer.readVarInt();
		List<Float> resultChanceList = new ArrayList<>();

		for (int k = 0; k < chanceSize; k++) {
			resultChanceList.add(buffer.readFloat());
		}

		// Ingredientの個数を読み込む
		int ingredSize = buffer.readVarInt();
		List<Ingredient> ingredList = new ArrayList<>();

		// 読み取った個数の分だけ、繰り返し処理でIngredientを読み込む
		for (int i = 0; i < ingredSize; i++) {
			ingredList.add(Ingredient.fromNetwork(buffer));
		}

		// Ingredientの個数を読み込む
		int countSize = buffer.readVarInt();
		List<Integer> countList = new ArrayList<>();

		// 読み取った個数の分だけ、繰り返し処理でIntegerを読み込む
		for (int i = 0; i < countSize; i++) {
			countList.add(buffer.readVarInt());
		}

		// 読み取った要素からレシピを作成
		return new RecyclerRecipe(id, resultList, resultChanceList, ingredList, countList);
	}

	// サーバー側からクライアント側に同期するために、レシピをパケットに乗せる
	@Override
	public void toNetwork(FriendlyByteBuf buffer, RecyclerRecipe recipe) {

		List<Ingredient> resultList = recipe.getResultIngList();
		buffer.writeVarInt(resultList.size());
		resultList.forEach(t -> t.toNetwork(buffer));

		List<Float> chanceList = recipe.getChanceList();
		buffer.writeVarInt(chanceList.size());
		chanceList.forEach(t -> buffer.writeFloat(t));

		// Ingredientsのサイズを書き込む
		List<Ingredient> ingredList = recipe.getIngredList();
		buffer.writeVarInt(ingredList.size());
		ingredList.forEach(t -> t.toNetwork(buffer));

		List<Integer> countList = recipe.getCountList();
		buffer.writeVarInt(countList.size());
		countList.forEach(t -> buffer.writeVarInt(t));
	}
}
