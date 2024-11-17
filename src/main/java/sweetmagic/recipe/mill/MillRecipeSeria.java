package sweetmagic.recipe.mill;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import sweetmagic.recipe.base.AbstractRecipeSerializer;

public class MillRecipeSeria extends AbstractRecipeSerializer implements RecipeSerializer<MillRecipe> {

	// jsonからレシピを生成する
	@Override
	public MillRecipe fromJson(ResourceLocation id, JsonObject json) {
		List<ItemStack> resultList = this.readResultList(json, "result");		// クラフト後のアイテムリスト取得
		List<Float> chanceList = this.readChanceList(json, "result");			// 要求アイテムの取得チャンス
		List<Ingredient> ingredList = this.readIngredList(json, "ingredients");	// 要求アイテムリストの取得
		List<Integer> countList = this.readCountList(json, "ingredients");		// 要求アイテム個数の取得
		return new MillRecipe(id, resultList, chanceList, ingredList, countList);
	}

	// サーバー側から受け取ったパケットから、レシピを復元する
	@Override
	public @Nullable MillRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {

		// クラフト後アイテムのリストサイズ取得
		int resultSize = buffer.readVarInt();
		List<ItemStack> resultList = new ArrayList<>();

		// クラフト後のアイテム取得
		for (int k = 0; k < resultSize; k++) {
			resultList.add(buffer.readItem());
		}

		// クラフト後のチャンスを取得
		int chanceSize = buffer.readVarInt();
		List<Float> resultChanceList = new ArrayList<>();

		for (int k = 0; k < chanceSize; k++) {
			resultChanceList.add(buffer.readFloat());
		}

		// Ingredientの個数を取得
		int ingredSize = buffer.readVarInt();
		List<Ingredient> ingredList = new ArrayList<>();

		// 読み取った個数の分だけ、繰り返し処理でIngredientを取得
		for (int i = 0; i < ingredSize; i++) {
			ingredList.add(Ingredient.fromNetwork(buffer));
		}

		// Ingredientの個数を取得
		int countSize = buffer.readVarInt();
		List<Integer> countList = new ArrayList<>();

		// 読み取った個数の分だけ、繰り返し処理でIntegerを取得
		for (int i = 0; i < countSize; i++) {
			countList.add(buffer.readVarInt());
		}
		// 読み取った要素からレシピを作成
		return new MillRecipe(id, resultList, resultChanceList, ingredList, countList);
	}

	// サーバー側からクライアント側に同期するために、レシピをパケットに乗せる
	@Override
	public void toNetwork(FriendlyByteBuf buffer, MillRecipe recipe) {

		List<ItemStack> resultList = recipe.getResultList();
		buffer.writeVarInt(resultList.size());
		resultList.forEach(s -> buffer.writeItem(s));

		List<Float> chanceList = recipe.getChanceList();
		buffer.writeVarInt(chanceList.size());
		chanceList.forEach(f -> buffer.writeFloat(f));

		List<Ingredient> ingredList = recipe.getIngredList();
		buffer.writeVarInt(ingredList.size());
		ingredList.forEach(t -> t.toNetwork(buffer));

		List<Integer> countList = recipe.getCountList();
		buffer.writeVarInt(countList.size());
		countList.forEach(i -> buffer.writeVarInt(i));
	}
}
